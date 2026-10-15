package wbs.harness;

import com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTasks;
import com.intellij.gradle.toolingExtension.impl.ideTask.IdeProjectModelTask;
import com.intellij.gradle.toolingExtension.impl.ideTask.ModelBundle;
import com.intellij.gradle.toolingExtension.impl.ideTask.ModelPayload;
import com.intellij.gradle.toolingExtension.impl.modelSerialization.ToolingSerializer;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.ResultHandler;
import org.gradle.tooling.events.OperationType;
import org.gradle.tooling.model.gradle.BasicGradleProject;
import org.gradle.tooling.model.gradle.GradleBuild;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The "pure task run" transport (DECISIONS D14): no build action at all. The project structure comes from the
 * GradleBuild model (settings only), the model tasks run through a BuildLauncher (a task run, so Isolated Projects
 * can update the entry per project after an edit), and the bundles are read and deserialized in the client, which
 * is where the IDE would read them. Bundle locations assume the default build directory.
 */
final class LauncherSync {

    static final class Outcome {
        final Map<String, Integer> modelsPerClass = new LinkedHashMap<>();
        long bytes;
        int projects;
        int builds;
        int missingBundles;
        List<String> errors = new ArrayList<>();
    }

    static Outcome run(ProjectConnection connection, SyncHarness.Options o, List<String> arguments, SyncHarness.Timing t,
                       SyncHarness.Results results, OutputStream buildOut) throws Exception {
        // 1. Project structure: GradleBuild for the root build and every editable (included) build.
        GradleBuild root = connection.getModel(GradleBuild.class);
        t.mark("gradleBuild");
        List<GradleBuild> builds = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        Queue<GradleBuild> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            GradleBuild b = queue.remove();
            if (seen.add(b.getBuildIdentifier().getRootDir().getPath())) {
                builds.add(b);
                queue.addAll(b.getEditableBuilds());
            }
        }
        List<String> taskPaths = new ArrayList<>();
        List<File> bundles = new ArrayList<>();
        for (GradleBuild b : builds) {
            String buildPath = b == root ? "" : buildPathOf(b, root);
            for (BasicGradleProject p : b.getProjects()) {
                String projectPart = p.getPath().equals(":") ? "" : p.getPath();
                taskPaths.add(buildPath + projectPart + ":" + IdeProjectModelTask.NAME);
                bundles.add(new File(p.getProjectDirectory(), "build/" + IdeModelTasks.MODEL_DIR + "/" + IdeProjectModelTask.NAME + ".bin"));
            }
        }
        Outcome outcome = new Outcome();
        outcome.builds = builds.size();
        outcome.projects = taskPaths.size();

        // 2. Run the tasks.
        BuildLauncher launcher = connection.newBuild().forTasks(taskPaths.toArray(new String[0]));
        launcher.withArguments(arguments);
        if (!o.jvmArgs.isEmpty()) {
            launcher.addJvmArguments(o.jvmArgs);
        }
        launcher.withSystemProperties(java.util.Collections.emptyMap());
        launcher.setStandardOutput(buildOut);
        launcher.setStandardError(buildOut);
        launcher.addProgressListener(results::onTaskEvent, OperationType.TASK);
        CountDownLatch done = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        t.mark("tasksStart");
        launcher.run(new ResultHandler<Void>() {
            @Override
            public void onComplete(Void result) {
                done.countDown();
            }

            @Override
            public void onFailure(GradleConnectionException e) {
                failure.set(e);
                done.countDown();
            }
        });
        done.await(6, TimeUnit.HOURS);
        t.mark("tasksEnd");
        if (failure.get() != null) {
            throw new IllegalStateException("task run failed", failure.get());
        }

        // 3. Read the bundles here, in the client.
        for (File bundle : bundles) {
            if (!bundle.isFile()) {
                outcome.missingBundles++;
                continue;
            }
            List<ModelPayload> payloads;
            try (InputStream in = Files.newInputStream(bundle.toPath()); DataInputStream data = new DataInputStream(in)) {
                payloads = ModelBundle.read(data);
            } catch (IOException | RuntimeException e) {
                outcome.errors.add(bundle + ": " + e);
                continue;
            }
            for (ModelPayload payload : payloads) {
                try {
                    Class<?> modelClass = Class.forName(payload.getModelClassName());
                    Object model = ModelPayload.JAVA.equals(payload.getFormat())
                        ? IdeTasksTransport.readJava(payload.getBytes())
                        : new ToolingSerializer().read(payload.getBytes(), modelClass);
                    if (model != null) {
                        outcome.modelsPerClass.merge(modelClass.getSimpleName(), 1, Integer::sum);
                        outcome.bytes += payload.getBytes().length;
                    }
                } catch (Exception e) {
                    outcome.errors.add(bundle + " " + payload.getModelClassName() + ": " + e);
                }
            }
        }
        t.mark("bundlesRead");
        return outcome;
    }

    /** {@code :name} for an included build: the build path is its root dir name relative to the root build's includes. */
    private static String buildPathOf(GradleBuild build, GradleBuild root) {
        return ":" + build.getBuildIdentifier().getRootDir().getName();
    }
}
