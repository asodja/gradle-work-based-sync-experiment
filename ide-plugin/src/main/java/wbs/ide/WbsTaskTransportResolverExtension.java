package wbs.ide;

import com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTasks;
import org.jetbrains.plugins.gradle.model.ProjectImportModelProvider;
import org.jetbrains.plugins.gradle.service.project.AbstractProjectResolverExtension;
import org.jetbrains.plugins.gradle.service.execution.GradleInitScriptUtil;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.gradle.BasicGradleProject;
import org.gradle.tooling.model.gradle.GradleBuild;
import org.jetbrains.plugins.gradle.service.execution.GradleExecutionContextImpl;
import org.jetbrains.plugins.gradle.service.execution.GradleExecutionHelper;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The task transport in the IDE (M5, DECISIONS D21). Active when the IDE runs with {@code -Dwbs.transport=tasks}:
 * registers the model tasks through an init script, puts the prototype jar on the daemon's init script classpath
 * (the IDE does that for every tooling extension class), and contributes the request and file providers. The
 * built-in providers of the ported models are dropped by the patched {@code GradleModelFetchAction}
 * ({@code -Dwbs.transport.drop}).
 */
public final class WbsTaskTransportResolverExtension extends AbstractProjectResolverExtension {

    static boolean active() {
        return System.getProperty("wbs.transport") != null;
    }

    /** {@code -Dwbs.transport.run=plain}: the model tasks run as a plain task run before the build action (M5 round 2). */
    static boolean plainRun() {
        String mode = System.getProperty("wbs.transport.run");
        return "plain".equals(mode) || "only".equals(mode);
    }

    /** {@code -Dwbs.transport.run=only}: no build action at all; the patched resolver takes {@link #buildState()}. */
    static boolean onlyPlainRun() {
        return "only".equals(System.getProperty("wbs.transport.run"));
    }

    private GradleBuild rootBuild;
    private Map<com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelId, Object> dslBaseScriptModels = Map.of();
    private final List<GradleBuild> allBuilds = new ArrayList<>();
    private org.gradle.util.GradleVersion gradleVersion;
    /** bundle reads started while the plain run is still going, keyed by project directory (D23 streaming). */
    private final Map<String, java.util.concurrent.Future<ReadBundle>> streamedReads = new java.util.concurrent.ConcurrentHashMap<>();
    private java.util.concurrent.ExecutorService readPool;

    /** project directory -> bundle files, filled by the plain run; the file providers then read without a request. */
    private final Map<String, List<String>> bundlesByProjectDir = new LinkedHashMap<>();

    /**
     * Runs every project's {@code ideProjectModel} task as a plain task run on a second connection to the same
     * project, before the build action. A plain run's configuration cache key has no client classloader id, so it
     * is a hit after an IDE restart (harness `launcher`), where the action always misses. The task paths come from
     * {@code GradleBuild} as in the harness's LauncherSync; included builds are addressed by their root dir name.
     */
    @Override
    public void preImportCheck() {
        if (!active() || !plainRun()) {
            return;
        }
        GradleExecutionContextImpl context = (GradleExecutionContextImpl) resolverCtx;
        long started = System.currentTimeMillis();
        try {
            GradleExecutionHelper.execute(context, connection -> {
                runModelTasks(connection, context);
                return null;
            });
            WbsLog.info("[wbs] plain model task run: " + bundlesByProjectDir.size() + " projects in "
                + (System.currentTimeMillis() - started) + " ms");
        } catch (RuntimeException e) {
            String message = "[wbs] plain model task run failed after " + (System.currentTimeMillis() - started) + " ms: " + e;
            WbsLog.info(message);
            if (onlyPlainRun()) {
                // no build action to fall back to: the sync fails here, with the cause, instead of in buildState()
                throw new IllegalStateException(message, e);
            }
            // the sync goes on with the request provider scheduling the tasks inside the action
            bundlesByProjectDir.clear();
        }
    }

    private void runModelTasks(ProjectConnection connection, GradleExecutionContextImpl context) {
        // The classpath snippet of the model tasks init script uses mapPath() from the IDE's path mapper script,
        // which the action gets prepended by the resolver; the plain run needs it too.
        List<String> arguments = new ArrayList<>(List.of("--init-script", GradleInitScriptUtil.createTargetPathMapperInitScript().toString()));
        if (!context.getSettings().isDownloadSources()) {
            // As the resolver does for its action: the idea plugin configurator turns source and javadoc download off,
            // which is what makes IntelliJ's dependency resolver leave library sources out of the models.
            arguments.addAll(List.of("--init-script", GradleInitScriptUtil.createIdeaPluginConfiguratorInitScript().toString()));
        }
        for (String arg : context.getSettings().getArguments()) {
            // IntelliJ's per-sync correlation id would make every run's key different; blank entries would be
            // read as task paths
            if (!arg.startsWith("-Porg.gradle.kotlin.dsl.provider.cid=") && !arg.isBlank()) {
                arguments.add(arg);
            }
        }
        // D29: the build structure from the ideBuildTree bundles (-Dwbs.transport.tree=task) or from the GradleBuild model
        boolean treeMode = treeFromTask();
        GradleBuild root = null;
        BuildTreeModels trees = null;
        if (treeMode) {
            trees = new BuildTreeModels(new File(context.getProjectPath()));
        } else {
            ModelBuilder<GradleBuild> builds = connection.model(GradleBuild.class);
            configure(builds, context, arguments);
            try {
                root = builds.get();
            } catch (RuntimeException e) {
                throw new IllegalStateException("GradleBuild request failed: " + e.getMessage(), e);
            }
        }
        gradleVersion = context.getGradleVersion();
        if (!treeMode) {
            dslBaseScriptModels = fetchDslBaseScriptModel(connection, context, arguments);
        }
        // Model mode: one task path per project and the bundle locations, known before the run. Tree mode: the root
        // aggregate only; projects are resolved from the tree bundles as their tasks finish (or after the run).
        List<String> taskPaths = new ArrayList<>();
        Map<String, File> projectDirByTaskPath = new java.util.HashMap<>();
        if (treeMode) {
            taskPaths.add(":" + com.intellij.gradle.toolingExtension.impl.ideTask.IdeBuildTreeTask.AGGREGATE_NAME);
        } else {
            for (GradleBuild b : traverse(root)) {
                String buildPath = b == root ? "" : ":" + b.getBuildIdentifier().getRootDir().getName();
                for (BasicGradleProject project : b.getProjects()) {
                    String projectPart = project.getPath().equals(":") ? "" : project.getPath();
                    String taskPath = buildPath + projectPart + ":" + com.intellij.gradle.toolingExtension.impl.ideTask.IdeProjectModelTask.NAME;
                    taskPaths.add(taskPath);
                    projectDirByTaskPath.put(taskPath, project.getProjectDirectory());
                }
            }
        }
        // Streaming: a project's bundle is read the moment its task finishes, while the run goes on; the holder state
        // only waits for the reads still in flight when the run ends.
        streamedReads.clear();
        if (readPool != null) {
            readPool.shutdownNow();
        }
        readPool = java.util.concurrent.Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors()));
        final java.util.concurrent.ExecutorService pool = readPool;
        final BuildTreeModels treesRef = trees;
        List<String> unresolved = java.util.Collections.synchronizedList(new ArrayList<>());
        String modelTaskSuffix = ":" + com.intellij.gradle.toolingExtension.impl.ideTask.IdeProjectModelTask.NAME;
        org.gradle.tooling.events.ProgressListener streaming = event -> {
            if (event instanceof org.gradle.tooling.events.task.TaskFinishEvent) {
                org.gradle.tooling.events.task.TaskFinishEvent finish = (org.gradle.tooling.events.task.TaskFinishEvent) event;
                String taskPath = finish.getDescriptor().getTaskPath();
                if (treesRef != null && taskPath.endsWith(":" + com.intellij.gradle.toolingExtension.impl.ideTask.IdeBuildTreeTask.NAME)) {
                    // the bundle read so far may be the previous run's (an up-to-date check is a finish event too)
                    treesRef.invalidate();
                    return;
                }
                if (!taskPath.endsWith(modelTaskSuffix) || finish.getResult() instanceof org.gradle.tooling.events.task.TaskFailureResult) {
                    return;
                }
                File dir = projectDirByTaskPath.get(taskPath);
                if (dir == null && treesRef != null) {
                    String resolved = treesRef.projectDirOf(taskPath);
                    dir = resolved == null ? null : new File(resolved);
                }
                if (dir == null) {
                    unresolved.add(taskPath);
                    return;
                }
                File bundle = bundleFile(dir);
                streamedReads.put(dir.getPath(), pool.submit(() -> readBundle(bundle)));
            }
        };
        BuildLauncher launcher = connection.newBuild();
        configure(launcher, context, arguments);
        launcher.forTasks(taskPaths.toArray(new String[0]));
        launcher.addProgressListener(streaming, org.gradle.tooling.events.OperationType.TASK);
        // -Dwbs.transport.log=<file>: the plain run's output and task events with timestamps, for diagnosis
        String logPath = System.getProperty("wbs.transport.log");
        java.io.PrintStream log = null;
        if (logPath != null) {
            try {
                log = new java.io.PrintStream(new java.io.FileOutputStream(logPath, true), true);
            } catch (java.io.IOException e) {
                throw new java.io.UncheckedIOException(e);
            }
            final java.io.PrintStream out = log;
            out.println("== plain run " + java.time.LocalTime.now() + " tasks=" + taskPaths.size());
            launcher.setStandardOutput(new TimestampedStream(out));
            launcher.setStandardError(new TimestampedStream(out));
            launcher.addProgressListener((org.gradle.tooling.events.ProgressListener) event ->
                out.println(java.time.LocalTime.now() + " " + event.getDisplayName()), org.gradle.tooling.events.OperationType.TASK);
        }
        try {
            launcher.run();
        } finally {
            if (log != null) {
                log.println("== plain run end " + java.time.LocalTime.now());
                log.close();
            }
        }
        if (treeMode) {
            trees.invalidate();   // read the trees the run has just written, not what streaming saw mid-run
            if (!trees.loadAll()) {
                throw new IllegalStateException("build tree bundles missing after the task run: " + trees.missing());
            }
            root = trees.rootBuild();
            // the base script model travels in the root tree bundle; a request only if the task could not capture it
            dslBaseScriptModels = trees.dslBaseScriptModels();
            if (dslBaseScriptModels == null) {
                dslBaseScriptModels = fetchDslBaseScriptModel(connection, context, arguments);
            } else if (!dslBaseScriptModels.isEmpty()) {
                WbsLog.info("[wbs] GradleDslBaseScriptModel from the tree bundle");
            }
            for (String taskPath : unresolved) {
                String resolved = trees.projectDirOf(taskPath);
                if (resolved != null) {
                    File bundle = bundleFile(new File(resolved));
                    streamedReads.put(resolved, pool.submit(() -> readBundle(bundle)));
                }
            }
            WbsLog.info("[wbs] build tree from tasks: " + trees.buildCount() + " builds; " + unresolved.size() + " of " + (streamedReads.size()) + " bundles resolved after the run");
        }
        List<GradleBuild> all = traverse(root);
        Map<String, List<String>> bundles = new LinkedHashMap<>();
        for (GradleBuild b : all) {
            for (BasicGradleProject project : b.getProjects()) {
                bundles.put(project.getProjectDirectory().getPath(), List.of(bundleFile(project.getProjectDirectory()).getPath()));
            }
        }
        rootBuild = root;
        bundlesByProjectDir.clear();
        bundlesByProjectDir.putAll(bundles);
        allBuilds.clear();
        allBuilds.addAll(all);
    }

    /** {@code -Dwbs.transport.tree=task}: the build structure from the ideBuildTree bundles, no GradleBuild request (D29). */
    static boolean treeFromTask() {
        return "task".equals(System.getProperty("wbs.transport.tree"));
    }

    private static File bundleFile(File projectDir) {
        return new File(projectDir, "build/" + IdeModelTasks.MODEL_DIR + "/" + com.intellij.gradle.toolingExtension.impl.ideTask.IdeProjectModelTask.NAME + ".bin");
    }

    /** The root build and every editable build below it, each once, root first. */
    private static List<GradleBuild> traverse(GradleBuild root) {
        List<GradleBuild> all = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        java.util.ArrayDeque<GradleBuild> queue = new java.util.ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            GradleBuild b = queue.remove();
            if (seen.add(b.getBuildIdentifier().getRootDir().getPath())) {
                all.add(b);
                queue.addAll(b.getEditableBuilds());
            }
        }
        return all;
    }

    private int allPayloads;

    private record ReadBundle(Map<String, Object> models) {
    }

    private static ReadBundle readBundle(File file) throws Exception {
        Map<String, Object> models = new LinkedHashMap<>();
        if (!file.isFile()) {
            WbsLog.info("[wbs] missing bundle " + file);
            return new ReadBundle(models);
        }
        List<com.intellij.gradle.toolingExtension.impl.ideTask.ModelPayload> bundle;
        try (java.io.DataInputStream in = new java.io.DataInputStream(java.nio.file.Files.newInputStream(file.toPath()))) {
            bundle = com.intellij.gradle.toolingExtension.impl.ideTask.ModelBundle.read(in);
        }
        for (com.intellij.gradle.toolingExtension.impl.ideTask.ModelPayload payload : bundle) {
            try {
                Object value = payload.readModel(WbsTaskTransportResolverExtension.class.getClassLoader());
                if (value != null) {
                    models.put(payload.getModelClassName(), value);
                }
            } catch (Exception e) {
                WbsLog.info("[wbs] cannot read " + payload.getModelClassName() + " from " + file + ": " + e);
            }
        }
        return new ReadBundle(models);
    }

    /** GradleModelId.createClassId without the Class: simple name plus the hash of the qualified name. */
    private static String classIdOf(String className) {
        int cut = Math.max(className.lastIndexOf('.'), className.lastIndexOf('$'));
        return className.substring(cut + 1) + className.hashCode();
    }

    /**
     * The sync's models as one holder state, read from the bundles the plain run produced (M5 round 2c, one Gradle
     * invocation): the build structure from {@code GradleBuild}, every payload under its model id (Ion bytes as the
     * holder reads them lazily, Java-serialized ones as objects), and the two Gradle-owned models assembled per
     * build. Called reflectively by the patched {@code GradleProjectResolver} instead of running the build action.
     */
    public com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelHolderState buildState() throws Exception {
        if (rootBuild == null) {
            throw new IllegalStateException("no plain model task run happened before buildState()");
        }
        List<org.jetbrains.plugins.gradle.model.DefaultGradleLightBuild> light =
            org.jetbrains.plugins.gradle.model.DefaultGradleLightBuild.convertGradleBuilds(allBuilds, gradleVersion);
        Map<com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelId, Object> models = new LinkedHashMap<>();
        // Every payload is deserialized here with a fresh IntelliJ serializer, one bundle per worker: the holder would
        // read all Ion documents of a class with one reader, whose object table assumes ids continuing across
        // documents, while each bundle's documents were written by their own writer (ids restart; the harness's
        // transport had the same rule). Java-serialized payloads load through this plugin's classloader, which sees
        // the Kotlin plugin's model classes as well.
        java.util.concurrent.ExecutorService pool = readPool != null ? readPool
            : java.util.concurrent.Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors()));
        Map<GradleBuild, List<Map.Entry<BasicGradleProject, java.util.concurrent.Future<ReadBundle>>>> reads = new LinkedHashMap<>();
        int streamed = 0;
        try {
            for (GradleBuild build : allBuilds) {
                for (BasicGradleProject project : build.getProjects()) {
                    String dir = project.getProjectDirectory().getPath();
                    List<String> files = bundlesByProjectDir.get(dir);
                    if (files == null) {
                        continue;
                    }
                    java.util.concurrent.Future<ReadBundle> inFlight = streamedReads.get(dir);
                    if (inFlight != null) {
                        streamed++;
                        reads.computeIfAbsent(build, b -> new ArrayList<>()).add(Map.entry(project, inFlight));
                        continue;
                    }
                    for (String file : files) {
                        reads.computeIfAbsent(build, b -> new ArrayList<>()).add(Map.entry(project, pool.submit(() -> readBundle(new File(file)))));
                    }
                }
            }
            int payloads = 0;
            for (GradleBuild build : allBuilds) {
                String buildId = com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelId.createBuildId(build.getBuildIdentifier());
                List<com.intellij.gradle.toolingExtension.impl.ideTask.IdeaModulePayload> idea = new ArrayList<>();
                List<com.intellij.gradle.toolingExtension.impl.ideTask.KotlinDslScriptPayload> scripts = new ArrayList<>();
                for (Map.Entry<BasicGradleProject, java.util.concurrent.Future<ReadBundle>> entry : reads.getOrDefault(build, List.of())) {
                    ReadBundle read = entry.getValue().get();
                    String projectId = com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelId.createProjectModelId(entry.getKey(), Object.class).getProjectId();
                    for (Map.Entry<String, Object> e : read.models.entrySet()) {
                        payloads++;
                        Object value = e.getValue();
                        if (value instanceof com.intellij.gradle.toolingExtension.impl.ideTask.IdeaModulePayload) {
                            idea.add((com.intellij.gradle.toolingExtension.impl.ideTask.IdeaModulePayload) value);
                        } else if (value instanceof com.intellij.gradle.toolingExtension.impl.ideTask.KotlinDslScriptPayload) {
                            scripts.add((com.intellij.gradle.toolingExtension.impl.ideTask.KotlinDslScriptPayload) value);
                        } else {
                            models.put(new com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelId(classIdOf(e.getKey()), buildId, projectId), value);
                        }
                    }
                }
                if (!idea.isEmpty()) {
                    String prefix = build == rootBuild ? "" : ":" + build.getBuildIdentifier().getRootDir().getName();
                    models.put(com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelId.createBuildModelId(build, org.gradle.tooling.model.idea.IdeaProject.class),
                        com.intellij.gradle.toolingExtension.impl.ideTask.IdeaProjectFromPayloads.assemble(idea, prefix));
                }
                if (!scripts.isEmpty()) {
                    org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel assembled =
                        com.intellij.gradle.toolingExtension.impl.ideTask.KotlinDslScriptsModelFromPayloads.assemble(scripts);
                    // 2025.3's Kotlin plugin reads it as a project model of the root project, 2026.2's as a build model
                    // (ProjectResolverContext.getBuildModel); registered under both ids
                    models.put(com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelId.createProjectModelId(build.getRootProject(), org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel.class), assembled);
                    models.put(com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelId.createBuildModelId(build, org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel.class), assembled);
                }
            }
            allPayloads = payloads;
            WbsLog.info("[wbs] bundles read during the run: " + streamed + " of " + bundlesByProjectDir.size());
        } finally {
            pool.shutdown();
            readPool = null;
            streamedReads.clear();
        }
        models.putAll(dslBaseScriptModels);
        int payloads = allPayloads;
        StringBuilder names = new StringBuilder();
        for (org.jetbrains.plugins.gradle.model.DefaultGradleLightBuild b : light) {
            names.append(' ').append(b.getName()).append('(').append(b.getProjects().size()).append(" projects)");
        }
        WbsLog.info("[wbs] holder state built from bundles: " + allBuilds.size() + " builds, " + payloads + " payloads, " + models.size()
            + " models; light builds:" + names);
        return new com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelHolderState(light.get(0), light.subList(1, light.size()), models);
    }

    /**
     * 2026.2 fetches Gradle's {@code GradleDslBaseScriptModel} (base script classpath, implicit imports; a function
     * of the distribution, built without configuring projects) first thing in the action and the Kotlin plugin
     * reads it from the holder state. The action does not run in the single-invocation mode, so it is requested here
     * on the same connection; 2025.3 has no holder class and nothing reading it, so nothing is requested.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Map<com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelId, Object> fetchDslBaseScriptModel(
        ProjectConnection connection, GradleExecutionContextImpl context, List<String> arguments) {
        Class<?> holderClass;
        Class modelClass;
        try {
            holderClass = Class.forName("com.intellij.gradle.toolingExtension.impl.model.dslBaseScriptModel.GradleDslBaseScriptModelHolder");
            modelClass = Class.forName("org.gradle.tooling.model.dsl.GradleDslBaseScriptModel");
        } catch (ClassNotFoundException e) {
            return Map.of();
        }
        try {
            long started = System.currentTimeMillis();
            ModelBuilder<Object> builder = connection.model(modelClass);
            configure(builder, context, arguments);
            Object model = builder.get();
            com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelHolderState wrapped =
                (com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelHolderState) holderClass.getMethod("wrap", modelClass).invoke(null, model);
            WbsLog.info("[wbs] GradleDslBaseScriptModel fetched: " + wrapped.getModels().size() + " model(s) in "
                + (System.currentTimeMillis() - started) + " ms");
            return new LinkedHashMap<>(wrapped.getModels());
        } catch (Exception e) {
            WbsLog.info("[wbs] GradleDslBaseScriptModel not fetched: " + e);
            return Map.of();
        }
    }

    @Override
    public Set<Class<?>> getToolingExtensionsClasses() {
        return active() ? Set.of(IdeModelTasks.class) : Set.of();
    }

    @Override
    public List<String> getExtraCommandLineArgs() {
        if (!active()) {
            return List.of();
        }
        String content = GradleInitScriptUtil.loadToolingExtensionProvidingInitScript(initScriptClasses())
            + System.lineSeparator() + IdeModelTasks.class.getName() + ".apply(gradle)" + System.lineSeparator();
        Path script = GradleInitScriptUtil.createInitScript("wbsModelTasks", content);
        return List.of("--init-script", script.toString());
    }

    /**
     * The prototype jar plus the classpath of the IDE's main init script: the IDE's own tooling extension classes and
     * those of every resolver extension (the Kotlin plugin's builders among them). Each init script has its own
     * classloader, and the captures run IntelliJ's builders, so the model tasks script needs the same jars.
     */
    private static Set<Class<?>> initScriptClasses() {
        // Sorted by name: the extensions return Set.of(...) collections whose iteration order changes per JVM run, which
        // changes the generated init script's classpath order, and a changed init script is a configuration cache miss
        // for every sync after an IDE restart (seen as "init script 'wbsModelTasks2.gradle' has changed").
        Set<Class<?>> classes = new java.util.TreeSet<>(java.util.Comparator.comparing(Class::getName));
        classes.addAll(GradleInitScriptUtil.GRADLE_TOOLING_EXTENSION_CLASSES);
        for (org.jetbrains.plugins.gradle.service.project.GradleProjectResolverExtension extension
            : org.jetbrains.plugins.gradle.service.project.GradleProjectResolverExtension.EP_NAME.getExtensionList()) {
            if (extension instanceof WbsTaskTransportResolverExtension) {
                continue;
            }
            try {
                classes.addAll(extension.getToolingExtensionsClasses());
            } catch (RuntimeException ignored) {
                // an extension that needs a live resolver context contributes nothing here, as in the IDE's own loop
            }
        }
        classes.add(IdeModelTasks.class);
        return classes;
    }

    /**
     * The sync settings applied by hand: {@code GradleExecutionHelper.prepareForExecution} would also apply the sync's
     * empty task list, which fails every operation with "Cannot locate matching tasks for an empty path".
     */
    private static void configure(org.gradle.tooling.LongRunningOperation operation, GradleExecutionContextImpl context, List<String> arguments) {
        org.jetbrains.plugins.gradle.settings.GradleExecutionSettings settings = context.getSettings();
        if (settings.getJavaHome() != null) {
            operation.setJavaHome(new File(settings.getJavaHome()));
        }
        if (!settings.getJvmArguments().isEmpty()) {
            operation.setJvmArguments(settings.getJvmArguments());
        }
        if (!settings.isPassParentEnvs()) {
            operation.setEnvironmentVariables(settings.getEnv());
        } else if (!settings.getEnv().isEmpty()) {
            Map<String, String> env = new LinkedHashMap<>(System.getenv());
            env.putAll(settings.getEnv());
            operation.setEnvironmentVariables(env);
        }
        operation.withCancellationToken(context.getCancellationToken());
        operation.withArguments(arguments);
    }

    @Override
    public List<ProjectImportModelProvider> getModelProviders() {
        return active() ? new TaskTransport(bundlesByProjectDir).providers() : List.of();
    }

    /** Prefixes every line with the time of day. */
    private static final class TimestampedStream extends java.io.OutputStream {
        private final java.io.PrintStream target;
        private final StringBuilder line = new StringBuilder();

        TimestampedStream(java.io.PrintStream target) {
            this.target = target;
        }

        @Override
        public synchronized void write(int b) {
            if (b == '\n') {
                target.println(java.time.LocalTime.now() + " | " + line);
                line.setLength(0);
            } else {
                line.append((char) b);
            }
        }
    }
}
