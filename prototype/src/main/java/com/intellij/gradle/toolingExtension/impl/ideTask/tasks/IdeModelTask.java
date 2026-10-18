package com.intellij.gradle.toolingExtension.impl.ideTask.tasks;

import com.intellij.gradle.toolingExtension.impl.ideTask.capture.CapturedModel;
import com.intellij.gradle.toolingExtension.impl.ideTask.capture.ProjectModelCapture;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelBundle;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelPayload;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Task;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

/**
 * Base class of the IDE model tasks (design/task-contract.md, DECISIONS D8/D9). The task writes a bundle of
 * serialized IntelliJ models for its project; the models are captured at configuration time ({@link #getModels()})
 * and the action serializes and writes them, so it works when the task is loaded from the configuration cache and
 * recreates a deleted bundle from the loaded state.
 */
@DisableCachingByDefault(because = "model payloads contain absolute paths")
public abstract class IdeModelTask extends DefaultTask {

    /** Task names IntelliJ already schedules during a sync; a dependency on them is allowed. */
    static final Set<String> ALLOWED_FOREIGN_TASKS = Set.of("prepareKotlinIdeaImport", "prepareKotlinBuildScriptModel");

    /** Identity path of the project, for the consumer to key the models. */
    @Input
    public abstract org.gradle.api.provider.Property<String> getProjectIdentityPath();

    /**
     * Content fingerprint of all payloads: the only up-to-date input. The payloads themselves are {@code @Internal}
     * so that task graph calculation, which materializes {@code @Nested} beans to infer task dependencies, does
     * not evaluate the captures one task at a time on its single thread; they are evaluated when the configuration
     * cache entry is stored, in parallel across projects.
     */
    @Input
    public abstract org.gradle.api.provider.Property<String> getFingerprint();

    /** The captured models in their state form ({@link CapturedModel}); the action turns them into the bundle. */
    @org.gradle.api.tasks.Internal
    public abstract ListProperty<CapturedModel> getModels();

    @OutputFile
    public abstract RegularFileProperty getModelFile();

    @TaskAction
    public void writeModels() throws IOException {
        File file = getModelFile().get().getAsFile();
        Files.createDirectories(file.getParentFile().toPath());
        List<ModelPayload> payloads = ProjectModelCapture.toPayloads(getModels().get());
        try (OutputStream out = Files.newOutputStream(file.toPath()); DataOutputStream data = new DataOutputStream(out)) {
            ModelBundle.write(data, payloads);
        } catch (IOException | RuntimeException e) {
            Files.deleteIfExists(file.toPath());
            throw e;
        }
    }

    /**
     * "Sync never builds": every dependency of a model task must be another model task or one of the IDE's own
     * prepare tasks. Called from a {@code taskGraph.whenReady} listener with the graph's view of the dependencies.
     */
    public static void checkDependencies(Task task, Set<Task> dependencies) {
        for (Task dependency : dependencies) {
            if (dependency instanceof IdeModelTask || ALLOWED_FOREIGN_TASKS.contains(dependency.getName())) {
                continue;
            }
            throw new GradleException("IDE model task " + task.getPath() + " depends on " + dependency.getPath()
                + ", which is not an IDE model task. Model tasks must not pull build work into the sync; "
                + "use getLocationOnly()/@Input strings for the input that introduced it.");
        }
    }
}
