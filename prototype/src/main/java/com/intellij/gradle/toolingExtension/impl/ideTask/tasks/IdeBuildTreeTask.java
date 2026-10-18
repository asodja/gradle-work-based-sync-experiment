package com.intellij.gradle.toolingExtension.impl.ideTask.tasks;

import com.intellij.gradle.toolingExtension.impl.ideTask.payload.BuildTreePayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelBundle;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelPayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelText;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

/**
 * Writes the build's {@link BuildTreePayload} as a one-entry bundle (D29). Registered in the root project of every
 * build of the tree; {@link #AGGREGATE_NAME} in the same project depends on it, on every project's model task and on
 * the included builds' aggregates, so one task request runs the whole sync.
 */
@DisableCachingByDefault(because = "absolute paths")
public abstract class IdeBuildTreeTask extends DefaultTask {

    public static final String NAME = "ideBuildTree";
    public static final String AGGREGATE_NAME = "ideModels";

    /** Canonical text of the payload: the up-to-date input. */
    @Input
    public abstract Property<String> getTreeText();

    @Internal
    public abstract Property<BuildTreePayload> getTree();

    @OutputFile
    public abstract RegularFileProperty getTreeFile();

    @TaskAction
    public void write() throws IOException {
        BuildTreePayload tree = getTree().get();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(tree);
        }
        ModelPayload payload = new ModelPayload(BuildTreePayload.class.getName(), ModelPayload.JAVA, ModelText.fingerprint(tree), bytes.toByteArray());
        File file = getTreeFile().get().getAsFile();
        Files.createDirectories(file.getParentFile().toPath());
        try (OutputStream out = Files.newOutputStream(file.toPath()); DataOutputStream data = new DataOutputStream(out)) {
            ModelBundle.write(data, List.of(payload));
        }
    }
}
