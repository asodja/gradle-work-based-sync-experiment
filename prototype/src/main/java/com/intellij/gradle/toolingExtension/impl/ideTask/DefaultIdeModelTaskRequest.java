package com.intellij.gradle.toolingExtension.impl.ideTask;

import java.util.List;

public final class DefaultIdeModelTaskRequest implements IdeModelTaskRequest {

    private final List<String> taskPaths;
    private final List<String> modelFiles;

    public DefaultIdeModelTaskRequest(List<String> taskPaths, List<String> modelFiles) {
        this.taskPaths = taskPaths;
        this.modelFiles = modelFiles;
    }

    @Override
    public List<String> getTaskPaths() {
        return taskPaths;
    }

    @Override
    public List<String> getModelFiles() {
        return modelFiles;
    }
}
