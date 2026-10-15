package wbs.harness;

import com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest;
import com.intellij.gradle.toolingExtension.impl.ideTask.ModelBundle;
import com.intellij.gradle.toolingExtension.impl.ideTask.ModelPayload;
import com.intellij.gradle.toolingExtension.impl.modelSerialization.ToolingSerializer;
import com.intellij.gradle.toolingExtension.modelAction.GradleModelFetchPhase;
import org.gradle.tooling.BuildController;
import org.gradle.tooling.model.gradle.BasicGradleProject;
import org.gradle.tooling.model.gradle.GradleBuild;
import org.jetbrains.plugins.gradle.model.ProjectImportModelProvider;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The task-based transport (design/task-contract.md "Transport"), harness side. Both providers are fields of the
 * same fetch action, so the map filled in the projects-loaded phase is still there in the build-finished phase
 * inside the daemon. Replaces IntelliJ's GradleSourceSetModelProvider.
 */
final class IdeTasksTransport implements Serializable {

    /** project directory path -> bundle files announced by the request model. */
    private final Map<String, List<String>> filesByProjectDir = new LinkedHashMap<>();

    List<ProjectImportModelProvider> providers() {
        return List.of(new RequestProvider(), new FileModelProvider());
    }

    /** Projects-loaded phase: ask every project for its model tasks; the builder schedules them. */
    final class RequestProvider implements ProjectImportModelProvider {
        @Override
        public GradleModelFetchPhase getPhase() {
            return GradleModelFetchPhase.PROJECT_LOADED_PHASE;
        }

        @Override
        public String getName() {
            return "IdeModelTaskRequestProvider";
        }

        @Override
        public void populateModels(BuildController controller, Collection<? extends GradleBuild> buildModels, GradleModelConsumer consumer) {
            for (GradleBuild build : buildModels) {
                for (BasicGradleProject project : build.getProjects()) {
                    IdeModelTaskRequest request = controller.findModel(project, IdeModelTaskRequest.class);
                    if (request != null && !request.getModelFiles().isEmpty()) {
                        filesByProjectDir.put(project.getProjectDirectory().getPath(), new ArrayList<>(request.getModelFiles()));
                    }
                }
            }
        }
    }

    /** Build-finished, source set phase: read the bundles the tasks wrote and hand the models to the consumer. */
    final class FileModelProvider implements ProjectImportModelProvider {
        @Override
        public GradleModelFetchPhase getPhase() {
            return GradleModelFetchPhase.PROJECT_SOURCE_SET_PHASE;
        }

        @Override
        public String getName() {
            return "IdeFileModelProvider";
        }

        @Override
        public void populateModels(BuildController controller, Collection<? extends GradleBuild> buildModels, GradleModelConsumer consumer) {
            ToolingSerializer serializer = new ToolingSerializer();
            for (GradleBuild build : buildModels) {
                for (BasicGradleProject project : build.getProjects()) {
                    List<String> files = filesByProjectDir.get(project.getProjectDirectory().getPath());
                    if (files == null) {
                        continue;
                    }
                    for (String path : files) {
                        File file = new File(path);
                        if (!file.isFile()) {
                            System.err.println("[wbs-tasks] missing model file " + path + " for " + project.getPath());
                            continue;
                        }
                        try (InputStream in = Files.newInputStream(file.toPath()); DataInputStream data = new DataInputStream(in)) {
                            for (ModelPayload payload : ModelBundle.read(data)) {
                                Class<?> modelClass = Class.forName(payload.getModelClassName(), true, IdeTasksTransport.class.getClassLoader());
                                Object model = serializer.read(payload.getBytes(), modelClass);
                                if (model != null) {
                                    consumer.consumeProjectModel(project, model, modelClass);
                                }
                            }
                        } catch (IOException | ClassNotFoundException | RuntimeException e) {
                            System.err.println("[wbs-tasks] cannot read " + path + " for " + project.getPath() + ": " + e);
                        } catch (Exception e) {
                            System.err.println("[wbs-tasks] cannot deserialize " + path + " for " + project.getPath() + ": " + e);
                        }
                    }
                }
            }
        }
    }
}
