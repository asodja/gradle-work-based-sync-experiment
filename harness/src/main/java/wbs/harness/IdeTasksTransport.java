package wbs.harness;

import com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest;
import com.intellij.gradle.toolingExtension.impl.ideTask.ModelBundle;
import com.intellij.gradle.toolingExtension.impl.ideTask.ModelPayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.PortedModels;
import com.intellij.gradle.toolingExtension.impl.modelSerialization.ToolingSerializer;
import com.intellij.gradle.toolingExtension.impl.util.GradleModelProviderUtil;
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

    /** The request provider plus one file provider per IntelliJ phase that a ported model belongs to. */
    List<ProjectImportModelProvider> providers() {
        List<ProjectImportModelProvider> result = new ArrayList<>();
        result.add(new RequestProvider());
        for (Map.Entry<String, List<String>> e : PortedModels.PROJECT_MODEL_TASK.entrySet()) {
            result.add(new FileModelProvider(phaseByName(e.getKey()), new java.util.HashSet<>(e.getValue())));
        }
        return result;
    }

    private static GradleModelFetchPhase phaseByName(String name) {
        for (GradleModelFetchPhase phase : List.of(GradleModelFetchPhase.PROJECT_LOADED_PHASE, GradleModelFetchPhase.PROJECT_MODEL_PHASE,
            GradleModelFetchPhase.PROJECT_SOURCE_SET_PHASE, GradleModelFetchPhase.PROJECT_SOURCE_SET_DEPENDENCY_PHASE,
            GradleModelFetchPhase.ADDITIONAL_MODEL_PHASE)) {
            if (phase.getName().equals(name)) {
                return phase;
            }
        }
        throw new IllegalArgumentException("unknown phase " + name);
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
            // Parallel like IntelliJ's own providers (idea.parallelModelFetch.enabled): in the projects-loaded phase each
            // project-scoped request configures its project, so a sequential loop would configure 287 projects one by one.
            GradleModelProviderUtil.buildModels(controller, buildModels, IdeModelTaskRequest.class, new GradleModelConsumer() {
                @Override
                public void consumeProjectModel(BasicGradleProject project, Object object, Class<?> clazz) {
                    IdeModelTaskRequest request = (IdeModelTaskRequest) object;
                    if (!request.getModelFiles().isEmpty()) {
                        synchronized (filesByProjectDir) {
                            filesByProjectDir.put(project.getProjectDirectory().getPath(), new ArrayList<>(request.getModelFiles()));
                        }
                    }
                }
            });
        }
    }

    /** Build-finished: read the bundles the tasks wrote and hand this phase's models to the consumer. */
    final class FileModelProvider implements ProjectImportModelProvider {
        private final GradleModelFetchPhase phase;
        private final java.util.Set<String> modelClassNames;

        FileModelProvider(GradleModelFetchPhase phase, java.util.Set<String> modelClassNames) {
            this.phase = phase;
            this.modelClassNames = modelClassNames;
        }

        @Override
        public GradleModelFetchPhase getPhase() {
            return phase;
        }

        @Override
        public String getName() {
            return "IdeFileModelProvider(" + phase.getName() + ")";
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
                                if (!modelClassNames.contains(payload.getModelClassName())) {
                                    continue;
                                }
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
