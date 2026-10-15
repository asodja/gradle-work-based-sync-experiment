package com.intellij.gradle.toolingExtension.impl.ideTask;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.invocation.Gradle;

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
        gradle.getLifecycle().beforeProject(IdeModelTasks::registerIn);
        gradle.getTaskGraph().whenReady(IdeModelTasks::checkDependencies);
    }

    // Registered in every project, not only java-base ones: the builders return a (mostly empty) model for
    // non-JVM projects too, and the comparison test wants identical output.
    private static void registerIn(Project project) {
        project.getTasks().register(IdeProjectModelTask.NAME, IdeProjectModelTask.class, task -> {
            task.getProjectIdentityPath().set(project.getPath());
            task.getModelFile().set(project.getLayout().getBuildDirectory().file(MODEL_DIR + "/" + IdeProjectModelTask.NAME + ".bin"));
            // Evaluated when the task is realized for the graph, after the project is configured: the same
            // moment the builder runs today. Stored by value in the configuration cache entry.
            task.getModels().addAll(ModelCapture.payloads(project, PortedModels.captureOrder()));
        });
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
