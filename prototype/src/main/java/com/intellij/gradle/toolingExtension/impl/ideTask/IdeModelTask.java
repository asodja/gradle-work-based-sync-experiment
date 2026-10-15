package com.intellij.gradle.toolingExtension.impl.ideTask;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.work.DisableCachingByDefault;

/** Placeholder until the contract is decided (design/task-contract.md). */
@DisableCachingByDefault(because = "inputs contain absolute paths")
public abstract class IdeModelTask extends DefaultTask {

    @Input
    public abstract org.gradle.api.provider.Property<String> getProjectPath();

    @OutputFile
    public abstract RegularFileProperty getModelFile();
}
