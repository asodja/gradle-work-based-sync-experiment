package com.intellij.gradle.toolingExtension.impl.ideTask;

import java.io.Serializable;
import java.util.List;

/**
 * Requested per project in the projects-loaded phase, like Kotlin's PrepareKotlinIdeImportTaskModel: the builder
 * adds the project's model tasks to the start parameter and returns their output files so the consumer knows
 * what to read after the build finished.
 */
public interface IdeModelTaskRequest extends Serializable {

    /** Task paths scheduled for this project. Empty when the project has no model task. */
    List<String> getTaskPaths();

    /** Absolute paths of the bundle files the tasks write, same order as the task paths. */
    List<String> getModelFiles();
}
