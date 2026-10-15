package com.intellij.gradle.toolingExtension.impl.ideTask;

import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.invocation.Gradle;
import org.gradle.internal.build.BuildState;
import org.jetbrains.plugins.gradle.tooling.AbstractModelBuilderService;
import org.jetbrains.plugins.gradle.tooling.ModelBuilderContext;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Projects-loaded phase: schedules the project's model tasks by adding them to the root build's start parameter
 * (the mechanism of PrepareKotlinIdeaImportTaskModelBuilder) and tells the consumer where their files will be.
 */
public final class IdeModelTaskRequestBuilder extends AbstractModelBuilderService {

    @Override
    public boolean canBuild(String modelName) {
        return IdeModelTaskRequest.class.getName().equals(modelName);
    }

    @Override
    public Object buildAll(String modelName, Project project, ModelBuilderContext context) {
        List<String> taskPaths = new ArrayList<>();
        List<String> files = new ArrayList<>();
        if (project.getTasks().getNames().contains(IdeProjectModelTask.NAME)) {
            taskPaths.add(absoluteTaskPath(project, IdeProjectModelTask.NAME));
            java.io.File file = project.getLayout().getBuildDirectory().file(IdeModelTasks.MODEL_DIR + "/" + IdeProjectModelTask.NAME + ".bin")
                .get().getAsFile();
            // A stale bundle from an earlier sync must not be mistaken for this sync's output.
            file.delete();
            files.add(file.getAbsolutePath());
        }
        if (!taskPaths.isEmpty()) {
            Gradle rootBuild = project.getGradle();
            while (rootBuild.getParent() != null) {
                rootBuild = rootBuild.getParent();
            }
            // Requests for different projects run in parallel; the read-modify-write of the task names must not
            // lose updates (PrepareKotlinIdeaImportTaskModelBuilder has the same race).
            synchronized (LOCK) {
                Set<String> names = new LinkedHashSet<>(rootBuild.getStartParameter().getTaskNames());
                names.addAll(taskPaths);
                rootBuild.getStartParameter().setTaskNames(names);
            }
        }
        return new DefaultIdeModelTaskRequest(taskPaths, files);
    }

    private static final Object LOCK = new Object();

    /** {@code :path:task} in the root build, {@code :buildPath:path:task} in an included build. */
    private static String absoluteTaskPath(Project project, String taskName) {
        String projectPart = project.getPath().equals(":") ? "" : project.getPath();
        if (project.getGradle().getParent() == null) {
            return projectPart + ":" + taskName;
        }
        String buildPath = ((ProjectInternal) project).getServices().get(BuildState.class).getBuildIdentifier().getBuildPath();
        return buildPath + projectPart + ":" + taskName;
    }
}
