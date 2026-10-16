package com.intellij.gradle.toolingExtension.impl.ideTask;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.initialization.IncludedBuild;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.GradleInternal;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Registers the IDE model tasks. Called once per build from IntelliJ's init script
 * ({@code IdeModelTasks.apply(gradle)}); Isolated-Projects safe: {@code gradle.lifecycle.beforeProject} and only the
 * project's own state.
 */
public final class IdeModelTasks {

    public static final String MODEL_DIR = "ide/models";

    private IdeModelTasks() {
    }

    /**
     * The build structure captured when its settings were evaluated, per build of the tree (D29), keyed by the build's
     * identity path: under Isolated Projects the {@code Gradle} object a project sees is not the settings' instance.
     */
    private static final Map<String, BuildTreePayload> TREES = Collections.synchronizedMap(new java.util.HashMap<>());

    public static void apply(Gradle gradle) {
        gradle.settingsEvaluated(IdeModelTasks::captureTree);
        gradle.getLifecycle().beforeProject(IdeModelTasks::registerIn);
        gradle.getLifecycle().afterProject(project -> {
            if (project.getParent() == null) {
                GradleOwnedModels.prepareForParallelCaptures(project);
            }
            ModelCapture.indexSourceSetArtifacts(project);
        });
        gradle.getTaskGraph().whenReady(IdeModelTasks::checkDependencies);
    }

    // Registered in every project, not only java-base ones: the builders return a (mostly empty) model for
    // non-JVM projects too, and the comparison test wants identical output.
    private static void registerIn(Project project) {
        if (project.getParent() == null) {
            registerBuildTree(project);
        }
        project.getTasks().register(IdeProjectModelTask.NAME, IdeProjectModelTask.class, task -> {
            task.getProjectIdentityPath().set(project.getPath());
            task.getModelFile().set(project.getLayout().getBuildDirectory().file(MODEL_DIR + "/" + IdeProjectModelTask.NAME + ".bin"));
            // Evaluated when the task is realized for the graph, after the project is configured: the same
            // moment the builder runs today. Stored by value in the configuration cache entry.
            // One memoized capture feeds both properties, so the builders run once per project.
            // -PideModelTasks.compact=false: every model as deflated serialized bytes (the pre-D26 state, for comparison).
            boolean compact = !"false".equals(project.findProperty("ideModelTasks.compact"));
            Provider<List<CapturedModel>> capture = ModelCapture.captures(project, PortedModels.captureOrder(), compact);
            task.getModels().addAll(capture);
            task.getFingerprint().set(capture.map(models -> {
                StringBuilder all = new StringBuilder();
                for (CapturedModel model : models) {
                    all.append(model.getModelClassName()).append('=').append(model.getFingerprint()).append('\n');
                }
                return all.toString();
            }));
        });
    }

    private static void captureTree(Settings settings) {
        Gradle gradle = settings.getGradle();
        BuildTreePayload tree = new BuildTreePayload();
        tree.identityPath = ((GradleInternal) gradle).getIdentityPath().getPath();
        tree.rootDir = settings.getRootDir().getAbsolutePath();
        tree.gradleVersion = gradle.getGradleVersion();
        tree.rootProject = node(settings.getRootProject(), tree.identityPath);
        TREES.put(tree.identityPath, tree);
    }

    private static BuildTreePayload.Node node(ProjectDescriptor descriptor, String identityPath) {
        BuildTreePayload.Node node = new BuildTreePayload.Node();
        node.name = descriptor.getName();
        node.path = descriptor.getPath();
        node.projectDir = descriptor.getProjectDir().getAbsolutePath();
        // as GradleBuildBuilder: the build's identity path plus the project path, ":" stays ":" in the root build
        node.buildTreePath = identityPath.equals(":") ? node.path : node.path.equals(":") ? identityPath : identityPath + node.path;
        List<ProjectDescriptor> children = new ArrayList<>(descriptor.getChildren());
        children.sort(Comparator.comparing(ProjectDescriptor::getPath));
        for (ProjectDescriptor child : children) {
            node.children.add(node(child, identityPath));
        }
        return node;
    }

    /**
     * Root project only: the build tree task and the aggregate that pulls in every model task of this build and the
     * aggregates of the included builds. Included builds are known here (settings evaluated, projects loaded).
     */
    private static void registerBuildTree(Project project) {
        Gradle gradle = project.getGradle();
        BuildTreePayload tree = TREES.remove(((GradleInternal) gradle).getIdentityPath().getPath());
        if (tree == null) {
            project.getLogger().warn("[ide-model-tasks] no build tree captured for {}; the IDE falls back to the GradleBuild model", project.getPath());
            return;
        }
        if (tree.identityPath.equals(":")) {
            tree.dslBaseScript = captureDslBaseScript(gradle, project);
        }
        for (IncludedBuild included : gradle.getIncludedBuilds()) {
            BuildTreePayload.IncludedBuild ref = new BuildTreePayload.IncludedBuild();
            ref.name = included.getName();
            ref.rootDir = included.getProjectDir().getAbsolutePath();
            tree.includedBuilds.add(ref);
        }
        TaskProvider<IdeBuildTreeTask> treeTask = project.getTasks().register(IdeBuildTreeTask.NAME, IdeBuildTreeTask.class, task -> {
            task.getTree().set(tree);
            task.getTreeText().set(ModelText.canonical(tree));
            task.getTreeFile().set(project.getLayout().getBuildDirectory().file(MODEL_DIR + "/" + IdeBuildTreeTask.NAME + ".bin"));
        });
        project.getTasks().register(IdeBuildTreeTask.AGGREGATE_NAME, task -> {
            task.setDescription("Runs every IDE model task of this build and of its included builds (work-based sync).");
            task.dependsOn(treeTask);
            List<String> paths = new ArrayList<>();
            collectPaths(tree.rootProject, paths);
            for (String path : paths) {
                task.dependsOn((path.equals(":") ? "" : path) + ":" + IdeProjectModelTask.NAME);
            }
            for (BuildTreePayload.IncludedBuild included : tree.includedBuilds) {
                task.dependsOn(gradle.includedBuild(included.name).task(":" + IdeBuildTreeTask.AGGREGATE_NAME));
            }
        });
    }

    /**
     * Gradle's own builder for {@code GradleDslBaseScriptModel} (2026.2's Kotlin plugin needs the model; the stock sync
     * requests it first thing in its action), run here so that the IDE needs no request for it. Internal API, by
     * reflection: a missing class or method yields no payload and the IDE requests the model instead.
     */
    @SuppressWarnings("unchecked")
    private static BuildTreePayload.DslBaseScript captureDslBaseScript(Gradle gradle, Project project) {
        try {
            Object builder = Class.forName("org.gradle.plugins.ide.internal.tooling.GradleDslBaseScriptModelBuilder").getConstructor().newInstance();
            Object buildState = GradleInternal.class.getMethod("getOwner").invoke(gradle);
            Object model = builder.getClass().getMethod("create", Class.forName("org.gradle.internal.build.BuildState")).invoke(builder, buildState);
            Object kotlin = call(model, "getKotlinDslBaseScriptModel");
            Object groovy = call(model, "getGroovyDslBaseScriptModel");
            BuildTreePayload.DslBaseScript result = new BuildTreePayload.DslBaseScript();
            result.kotlinScriptTemplatesClassPath = paths(kotlin, "getScriptTemplatesClassPath");
            result.kotlinCompileClassPath = paths(kotlin, "getCompileClassPath");
            result.kotlinImplicitImports = new ArrayList<>((List<String>) call(kotlin, "getImplicitImports"));
            try {
                result.kotlinTemplateClassNames = new ArrayList<>((List<String>) call(kotlin, "getTemplateClassNames"));
            } catch (NoSuchMethodException e) {
                // older model without template class names
            }
            result.groovyCompileClassPath = paths(groovy, "getCompileClassPath");
            result.groovyImplicitImports = new ArrayList<>((List<String>) call(groovy, "getImplicitImports"));
            return result;
        } catch (ReflectiveOperationException | RuntimeException e) {
            project.getLogger().warn("[ide-model-tasks] GradleDslBaseScriptModel not captured ({}); the IDE requests it", e.toString());
            return null;
        }
    }

    /** The model classes are package-private; their public getters need the accessibility flag. */
    private static Object call(Object target, String getter) throws ReflectiveOperationException {
        java.lang.reflect.Method method = target.getClass().getMethod(getter);
        method.setAccessible(true);
        return method.invoke(target);
    }

    private static List<String> paths(Object model, String getter) throws ReflectiveOperationException {
        List<String> result = new ArrayList<>();
        for (Object file : (List<?>) call(model, getter)) {
            result.add(((java.io.File) file).getPath());
        }
        return result;
    }

    private static void collectPaths(BuildTreePayload.Node node, List<String> paths) {
        paths.add(node.path);
        for (BuildTreePayload.Node child : node.children) {
            collectPaths(child, paths);
        }
    }

    private static void checkDependencies(TaskExecutionGraph graph) {
        List<Task> modelTasks = new ArrayList<>();
        for (Task task : graph.getAllTasks()) {
            if (task instanceof IdeModelTask) {
                modelTasks.add(task);
            }
        }
        for (Task task : modelTasks) {
            Set<Task> dependencies = graph.getDependencies(task);
            IdeModelTask.checkDependencies(task, dependencies);
        }
    }
}
