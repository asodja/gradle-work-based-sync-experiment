package com.intellij.gradle.toolingExtension.impl.ideTask;

import com.intellij.gradle.toolingExtension.impl.model.sourceSetModel.GradleSourceSetModelBuilder;
import com.intellij.gradle.toolingExtension.impl.model.sourceSetModel.GradleSourceSetSerialisationService;
import com.intellij.gradle.toolingExtension.impl.modelBuilder.DefaultModelBuilderContext;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.invocation.Gradle;
import org.jetbrains.plugins.gradle.model.GradleSourceSetModel;
import org.jetbrains.plugins.gradle.tooling.ModelBuilderContext;

import java.io.IOException;
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
            task.getModels().add(project.provider(() -> sourceSetModel(project)));
        });
    }

    private static ModelPayload sourceSetModel(Project project) throws IOException {
        ModelBuilderContext context = new DefaultModelBuilderContext(project.getGradle());
        GradleSourceSetModel model = (GradleSourceSetModel) new GradleSourceSetModelBuilder()
            .buildAll(GradleSourceSetModel.class.getName(), project, context);
        byte[] bytes = new GradleSourceSetSerialisationService().write(model, GradleSourceSetModel.class);
        return new ModelPayload(GradleSourceSetModel.class.getName(), bytes);
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
