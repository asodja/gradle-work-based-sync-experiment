package wbs.harness;

import com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelFetchAction;
import com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelHolderState;
import com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelId;
import com.intellij.gradle.toolingExtension.modelAction.GradleModelFetchPhase;
import org.gradle.tooling.BuildActionExecuter;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.IntermediateResultHandler;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.ResultHandler;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.events.OperationType;
import org.gradle.tooling.events.ProgressEvent;
import org.gradle.tooling.events.task.TaskFailureResult;
import org.gradle.tooling.events.task.TaskFinishEvent;
import org.gradle.tooling.events.task.TaskSkippedResult;
import org.gradle.tooling.events.task.TaskSuccessResult;
import org.gradle.tooling.model.build.BuildEnvironment;
import org.gradle.util.GradleVersion;
import org.jetbrains.plugins.gradle.model.ProjectImportModelProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs IntelliJ's {@code GradleModelFetchAction} against a build the way the IDE does during sync, and prints
 * where the time went. See design/measurement.md ("Harness").
 */
public final class SyncHarness {

    public static void main(String[] args) throws Exception {
        Options o = Options.parse(args);
        Path initDir = Files.createTempDirectory("wbs-init");
        int code = 0;
        // All repeats run in this one JVM: the configuration cache key hashes the serialized action, which
        // carries the client classloader's UUID (ClientSidePayloadClassLoaderRegistry), so a new JVM per run
        // could never reuse an entry, while the IDE process keeps one classloader across syncs.
        for (int run = o.firstRun; run < o.firstRun + o.repeat; run++) {
            if (o.beforeRun != null) {
                runHook(o.beforeRun, run);
            }
            File out = o.outFor(run);
            PrintStream summary = out == null ? System.out : new PrintStream(new FileOutputStream(out), true, "UTF-8");
            try {
                int c = new SyncHarness(o, initDir, summary, out).run();
                if (c != 0) {
                    code = c;
                }
            } finally {
                summary.flush();
                if (out != null) {
                    summary.close();
                    System.out.println("run " + run + " -> " + out + " exit " + code);
                }
            }
        }
        System.exit(code);
    }

    private static void runHook(String command, int run) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", command);
        pb.environment().put("RUN", Integer.toString(run));
        pb.inheritIO();
        int exit = pb.start().waitFor();
        if (exit != 0) {
            throw new IllegalStateException("--before-run command failed with exit " + exit + ": " + command);
        }
    }

    private final Options o;
    private final Path initDir;
    private final PrintStream summary;
    private final File out;
    private final List<String> providersLog = new ArrayList<>();

    private SyncHarness(Options o, Path initDir, PrintStream summary, File out) {
        this.o = o;
        this.initDir = initDir;
        this.summary = summary;
        this.out = out;
    }

    private int run() throws Exception {
        GradleConnector connector = GradleConnector.newConnector()
            .forProjectDirectory(o.project.toFile())
            .useGradleUserHomeDir(o.gradleUserHome.toFile());
        if (o.installation != null) {
            connector.useInstallation(o.installation.toFile());
        } else {
            connector.useBuildDistribution();
        }

        Timing t = new Timing();
        File buildLog = out == null ? null : new File(out.getPath() + ".build.log");
        try (ProjectConnection connection = connector.connect();
             OutputStream buildOut = buildLog == null ? System.out : new FileOutputStream(buildLog)) {

            // IntelliJ fetches BuildEnvironment before the action to learn the Gradle version.
            t.mark("start");
            BuildEnvironment env = connection.getModel(BuildEnvironment.class);
            GradleVersion gradleVersion = GradleVersion.version(env.getGradle().getGradleVersion());
            t.mark("buildEnvironment");

            IdeProviders providers = IdeProviders.create();
            IdeTasksTransport transport = null;
            if (o.tasks) {
                transport = new IdeTasksTransport();
                providers.useTaskTransport(transport);
            }
            providersLog.addAll(providers.log);
            InitScripts scripts = InitScripts.write(initDir, providers.toolingExtensionClasses(), o.extraToolingJars, o.tasks);

            GradleModelFetchAction action = new GradleModelFetchAction(gradleVersion);
            action.addProjectImportModelProviders(providers.providers());
            action.addTargetTypes(providers.targetTypes());
            action.setUseProjectsLoadedPhase(true);
            action.setUseStreamedValues(o.streaming);

            List<String> arguments = new ArrayList<>();
            // What GradleExecutionHelper and GradleProjectResolver add to every sync.
            arguments.add("-Didea.active=true");
            arguments.add("-Didea.version=" + o.ideaVersion);
            arguments.add("-Didea.vendor.name=JetBrains");
            arguments.add("-Didea.gradle.download.sources=false");
            arguments.add("-Didea.sync.active=true");
            arguments.add("-Didea.resolveSourceSetDependencies=true");
            if (o.parallelFetch) {
                arguments.add("-Didea.parallelModelFetch.enabled=true");
            }
            for (String extra : providers.extraCommandLineArgs()) {
                if (o.dropArgPrefixes.stream().noneMatch(extra::startsWith)) {
                    arguments.add(extra);
                }
            }
            for (Path script : scripts.inOrder()) {
                arguments.add("--init-script");
                arguments.add(script.toString());
            }
            arguments.addAll(o.gradleArgs);

            Results results = new Results();
            results.dumpDir = o.dumpModels == null ? null : o.dumpModels.toFile();
            CountDownLatch done = new CountDownLatch(1);
            AtomicReference<Throwable> failure = new AtomicReference<>();

            BuildActionExecuter<Void> executer = connection.action()
                .projectsLoaded(action, phaseHandler(results, t, "projectsLoaded"))
                .buildFinished(action, phaseHandler(results, t, "buildFinished"))
                .build()
                .forTasks(Collections.emptyList());
            executer.withArguments(arguments);
            if (!o.jvmArgs.isEmpty()) {
                executer.addJvmArguments(o.jvmArgs);
            }
            executer.withSystemProperties(Collections.emptyMap());
            executer.setStandardOutput(buildOut);
            executer.setStandardError(buildOut);
            executer.addProgressListener(results::onTaskEvent, OperationType.TASK);
            if (o.streaming) {
                executer.setStreamedValueListener(value -> {
                    if (value instanceof GradleModelHolderState) {
                        GradleModelHolderState state = (GradleModelHolderState) value;
                        String name = state.getPhase() == null ? "unknown" : state.getPhase().getName();
                        t.mark("streamed:" + name);
                        results.addModels(name, state);
                    }
                });
            }

            t.mark("actionStart");
            executer.run(new ResultHandler<Void>() {
                @Override
                public void onComplete(Void result) {
                    done.countDown();
                }

                @Override
                public void onFailure(GradleConnectionException e) {
                    failure.set(e);
                    done.countDown();
                }
            });
            done.await(6, TimeUnit.HOURS);
            t.mark("end");

            buildOut.flush();
            report(gradleVersion, arguments, t, results, failure.get(), buildLog);
            return failure.get() == null ? 0 : 1;
        }
    }

    private static IntermediateResultHandler<GradleModelHolderState> phaseHandler(Results results, Timing t, String name) {
        return state -> {
            t.mark(name);
            if (!results.streamedPhases()) {
                results.addModels(name, state);
            }
        };
    }

    private void report(GradleVersion gradleVersion, List<String> arguments, Timing t, Results results,
                        Throwable failure, File buildLog) throws IOException {
        PrintStream p = summary;
        p.println("project=" + o.project);
        p.println("gradleUserHome=" + o.gradleUserHome);
        p.println("gradleVersion=" + gradleVersion.getVersion());
        p.println("ideaHome=" + o.ideaHome);
        p.println("arguments=" + String.join(" ", arguments));
        p.println("jvmArgs=" + String.join(" ", o.jvmArgs));
        p.println("streaming=" + o.streaming + " parallelFetch=" + o.parallelFetch);
        p.println("status=" + (failure == null ? "OK" : "FAILED"));
        p.println();
        p.println("# resolver extensions");
        providersLog.forEach(p::println);

        p.println();
        p.println("# timing (ms from start)");
        long start = t.marks.get("start");
        for (Map.Entry<String, Long> e : t.marks.entrySet()) {
            p.printf("%-40s %8d%n", e.getKey(), e.getValue() - start);
        }
        p.println();
        p.printf("total_ms=%d%n", t.marks.get("end") - start);
        p.printf("buildEnvironment_ms=%d%n", t.marks.get("buildEnvironment") - start);
        p.printf("action_ms=%d%n", t.marks.get("end") - t.marks.get("actionStart"));
        Long loaded = t.marks.get("projectsLoaded");
        Long finished = t.marks.get("buildFinished");
        if (loaded != null) {
            p.printf("toProjectsLoaded_ms=%d%n", loaded - t.marks.get("actionStart"));
        }
        if (loaded != null && finished != null) {
            p.printf("projectsLoadedToBuildFinished_ms=%d%n", finished - loaded);
            p.printf("afterBuildFinished_ms=%d%n", t.marks.get("end") - finished);
        }
        p.println();
        p.println("# tasks");
        p.println("tasks=" + results.taskOutcomes);
        p.println();
        p.println("# models per phase (count, serialized bytes)");
        for (Map.Entry<String, Results.PhaseModels> e : results.perPhase.entrySet()) {
            p.printf("%-28s models=%-5d bytes=%d%n", e.getKey(), e.getValue().count, e.getValue().bytes);
        }
        p.println();
        p.println("# models per class");
        for (Map.Entry<String, Results.PhaseModels> e : results.perClass.entrySet()) {
            p.printf("%-100s %5d %10d%n", e.getKey(), e.getValue().count, e.getValue().bytes);
        }
        p.println();
        p.println("builds=" + results.builds + " projects=" + results.projects);
        if (buildLog != null) {
            p.println();
            p.println("# configuration cache / problems lines from " + buildLog.getName());
            String log = new String(Files.readAllBytes(buildLog.toPath()), StandardCharsets.UTF_8);
            Matcher m = Pattern.compile("(?m)^.*(Configuration cache entry|Reusing configuration cache|Calculating task graph|problems? (was|were) found|Isolated projects|BUILD (SUCCESSFUL|FAILED)|FAILURE:|What went wrong).*$").matcher(log);
            while (m.find()) {
                p.println(m.group());
            }
        }
        if (failure != null) {
            p.println();
            p.println("# failure");
            failure.printStackTrace(p);
        }
    }

    /** Wall-clock marks in order. */
    static final class Timing {
        final Map<String, Long> marks = new LinkedHashMap<>();

        void mark(String name) {
            marks.put(name, System.currentTimeMillis());
        }
    }

    static final class Results {
        static final class PhaseModels {
            int count;
            long bytes;
        }

        final Map<String, PhaseModels> perPhase = new LinkedHashMap<>();
        final Map<String, PhaseModels> perClass = new TreeMap<>();
        final Map<String, Integer> taskOutcomes = new TreeMap<>();
        int builds;
        int projects;
        private boolean streamed;

        synchronized boolean streamedPhases() {
            return streamed;
        }

        File dumpDir;

        synchronized void addModels(String phase, GradleModelHolderState state) {
            if (phase.startsWith("streamed:") || state.getPhase() != null) {
                streamed = true;
            }
            if (dumpDir != null) {
                dump(state);
            }
            if (state.getRootBuild() != null) {
                builds = 1 + state.getNestedBuilds().size();
                projects = state.getRootBuild().getProjects().size();
                state.getNestedBuilds().forEach(b -> projects += b.getProjects().size());
            }
            PhaseModels pm = perPhase.computeIfAbsent(phase, k -> new PhaseModels());
            for (Map.Entry<GradleModelId, Object> e : state.getModels().entrySet()) {
                long size = e.getValue() instanceof byte[] ? ((byte[]) e.getValue()).length : 0;
                pm.count++;
                pm.bytes += size;
                PhaseModels pc = perClass.computeIfAbsent(e.getKey().getClassId(), k -> new PhaseModels());
                pc.count++;
                pc.bytes += size;
            }
        }

        private void dump(GradleModelHolderState state) {
            for (Map.Entry<GradleModelId, Object> e : state.getModels().entrySet()) {
                if (!(e.getValue() instanceof byte[])) {
                    continue;
                }
                GradleModelId id = e.getKey();
                File dir = new File(dumpDir, sanitize(id.getClassId()) + "/" + sanitize(id.getBuildId()));
                dir.mkdirs();
                try {
                    Files.write(new File(dir, sanitize(id.getProjectId()) + ".bin").toPath(), (byte[]) e.getValue());
                } catch (IOException ex) {
                    throw new java.io.UncheckedIOException(ex);
                }
            }
        }

        private static String sanitize(String s) {
            return s.replaceAll("[^A-Za-z0-9._-]", "_");
        }

        void onTaskEvent(ProgressEvent event) {
            if (!(event instanceof TaskFinishEvent)) {
                return;
            }
            Object result = ((TaskFinishEvent) event).getResult();
            String outcome;
            if (result instanceof TaskSuccessResult) {
                TaskSuccessResult r = (TaskSuccessResult) result;
                outcome = r.isUpToDate() ? "up-to-date" : r.isFromCache() ? "from-cache" : "executed";
            } else if (result instanceof TaskSkippedResult) {
                outcome = "skipped";
            } else if (result instanceof TaskFailureResult) {
                outcome = "failed";
            } else {
                outcome = "other";
            }
            synchronized (this) {
                taskOutcomes.merge(outcome, 1, Integer::sum);
            }
        }
    }

    static final class Options {
        Path project;
        Path gradleUserHome;
        Path ideaHome;
        Path installation;
        String out;
        int repeat = 1;
        int firstRun = 1;
        String beforeRun;
        String ideaVersion = "2025.3";
        boolean streaming = true;
        boolean parallelFetch = true;
        final List<String> gradleArgs = new ArrayList<>();
        final List<String> jvmArgs = new ArrayList<>();
        final List<String> dropArgPrefixes = new ArrayList<>();
        final List<Path> extraToolingJars = new ArrayList<>();
        boolean tasks;
        Path dumpModels;

        static Options parse(String[] args) {
            Options o = new Options();
            for (int i = 0; i < args.length; i++) {
                String a = args[i];
                switch (a) {
                    case "--project": o.project = Path.of(args[++i]).toAbsolutePath(); break;
                    case "--gradle-user-home": o.gradleUserHome = Path.of(args[++i]).toAbsolutePath(); break;
                    case "--idea-home": o.ideaHome = Path.of(args[++i]).toAbsolutePath(); break;
                    case "--installation": o.installation = Path.of(args[++i]).toAbsolutePath(); break;
                    case "--out": o.out = args[++i]; break;
                    case "--repeat": o.repeat = Integer.parseInt(args[++i]); break;
                    case "--first-run": o.firstRun = Integer.parseInt(args[++i]); break;
                    case "--before-run": o.beforeRun = args[++i]; break;
                    case "--idea-version": o.ideaVersion = args[++i]; break;
                    case "--arg": o.gradleArgs.add(args[++i]); break;
                    case "--jvm-arg": o.jvmArgs.add(args[++i]); break;
                    case "--drop-arg-prefix": o.dropArgPrefixes.add(args[++i]); break;
                    case "--tasks": o.tasks = true; break;
                    case "--extra-tooling-jar": o.extraToolingJars.add(Path.of(args[++i]).toAbsolutePath()); break;
                    case "--dump-models": o.dumpModels = Path.of(args[++i]).toAbsolutePath(); break;
                    case "--no-streaming": o.streaming = false; break;
                    case "--no-parallel-fetch": o.parallelFetch = false; break;
                    default: throw new IllegalArgumentException("unknown option " + a + USAGE);
                }
            }
            if (o.project == null || o.gradleUserHome == null) {
                throw new IllegalArgumentException("--project and --gradle-user-home are required" + USAGE);
            }
            if (o.repeat > 1 && o.out != null && !o.out.contains("{run}")) {
                throw new IllegalArgumentException("--out must contain {run} when --repeat > 1" + USAGE);
            }
            return o;
        }

        /** Output file of one run; {@code {run}} in --out is replaced by the run number. */
        File outFor(int run) {
            if (out == null) {
                return null;
            }
            File f = new File(out.replace("{run}", Integer.toString(run)));
            if (f.getParentFile() != null) {
                f.getParentFile().mkdirs();
            }
            return f;
        }

        static final String USAGE = "\nusage: harness --project DIR --gradle-user-home DIR [--idea-home DIR] [--installation DIR]"
            + " [--out FILE(with {run})] [--repeat N] [--first-run N] [--before-run SHELL]"
            + " [--arg GRADLE_ARG]... [--jvm-arg ARG]... [--drop-arg-prefix P]... [--tasks] [--extra-tooling-jar JAR]... [--dump-models DIR] [--no-streaming] [--no-parallel-fetch] [--idea-version V]";
    }
}
