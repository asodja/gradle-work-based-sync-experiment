package com.intellij.gradle.toolingExtension.impl.ideTask;

import com.intellij.gradle.toolingExtension.impl.ideTask.capture.CapturedModel;
import com.intellij.gradle.toolingExtension.impl.ideTask.capture.BuildTreeCapture;
import com.intellij.gradle.toolingExtension.impl.ideTask.capture.GradleOwnedModelCapture;
import com.intellij.gradle.toolingExtension.impl.ideTask.capture.ProjectModelCapture;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.BuildTreePayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelText;
import com.intellij.gradle.toolingExtension.impl.ideTask.tasks.IdeBuildTreeTask;
import com.intellij.gradle.toolingExtension.impl.ideTask.tasks.IdeModelTask;
import com.intellij.gradle.toolingExtension.impl.ideTask.tasks.IdeProjectModelTask;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.initialization.IncludedBuild;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;

import java.util.ArrayList;
import java.util.List;
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

    public static void apply(Gradle gradle) {
        gradle.settingsEvaluated(BuildTreeCapture::capture);
        gradle.getLifecycle().beforeProject(IdeModelTasks::registerIn);
        gradle.getLifecycle().afterProject(project -> {
            if (project.getParent() == null) {
                GradleOwnedModelCapture.prepareForParallelCaptures(project);
            }
            ProjectModelCapture.indexSourceSetArtifacts(project);
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
            Provider<List<CapturedModel>> capture = ProjectModelCapture.captures(project, PortedModels.captureOrder(), compact);
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

    /**
     * Root project only: the build tree task and the aggregate that pulls in every model task of this build and the
     * aggregates of the included builds. Included builds are known here (settings evaluated, projects loaded).
     */
    private static void registerBuildTree(Project project) {
        Gradle gradle = project.getGradle();
        BuildTreePayload tree = BuildTreeCapture.take(gradle);
        if (tree == null) {
            project.getLogger().warn("[ide-model-tasks] no build tree captured for {}; the IDE falls back to the GradleBuild model", project.getPath());
            return;
        }
        if (tree.identityPath.equals(":")) {
            tree.dslBaseScript = BuildTreeCapture.dslBaseScript(gradle, project);
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
