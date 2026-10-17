package wbs.ide;

import com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest;
import com.intellij.gradle.toolingExtension.impl.ideTask.IdeaModulePayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.IdeaProjectFromPayloads;
import com.intellij.gradle.toolingExtension.impl.ideTask.KotlinDslScriptPayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.KotlinDslScriptsModelFromPayloads;
import com.intellij.gradle.toolingExtension.impl.ideTask.ModelBundle;
import com.intellij.gradle.toolingExtension.impl.ideTask.ModelPayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.PortedModels;
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
 * The task-based transport (design/task-contract.md "Transport"), IDE side: a copy of the harness class. Both providers are fields of the
 * same fetch action, so the map filled in the projects-loaded phase is still there in the build-finished phase
 * inside the daemon. Replaces IntelliJ's GradleSourceSetModelProvider.
 */
public final class TaskTransport implements Serializable {

    /** project directory path -> bundle files announced by the request model, or known from the plain run. */
    private final Map<String, List<String>> filesByProjectDir = new LinkedHashMap<>();
    private final boolean filesKnown;

    public TaskTransport(Map<String, List<String>> knownFiles) {
        filesByProjectDir.putAll(knownFiles);
        filesKnown = !knownFiles.isEmpty();
    }

    /** The request provider plus one file provider per IntelliJ phase that a ported model belongs to. */
    public List<ProjectImportModelProvider> providers() {
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
            if (filesKnown) {
                return; // the plain run produced the bundles already; nothing to schedule in the action
            }
            // Parallel like IntelliJ's own providers (idea.parallelModelFetch.enabled): in the projects-loaded phase each
            // project-scoped request configures its project, so a sequential loop would configure 287 projects one by one.
            GradleModelProviderUtil.buildModels(controller, buildModels, IdeModelTaskRequest.class, new GradleModelConsumer() {
                @Override
                public void consumeBuildModel(org.gradle.tooling.model.BuildModel buildModel, Object object, Class<?> clazz) {
                    // the request is a project model; abstract in 2026.2, a default method in 2025.3
                }

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
            // The two Gradle-owned payloads are not models the IDE knows: they are assembled per build into the
            // IdeaProject (a build model) and the KotlinDslScriptsModel (a project model of the build's root project,
            // as the Kotlin plugin's provider delivers it) and handed over under those classes (D17, M5 round 2b).
            Map<GradleBuild, List<IdeaModulePayload>> ideaByBuild = new LinkedHashMap<>();
            Map<GradleBuild, List<KotlinDslScriptPayload>> scriptsByBuild = new LinkedHashMap<>();
            for (GradleBuild build : buildModels) {
                for (BasicGradleProject project : build.getProjects()) {
                    List<String> files = filesByProjectDir.get(project.getProjectDirectory().getPath());
                    if (files == null) {
                        continue;
                    }
                    for (String path : files) {
                        File file = new File(path);
                        if (!file.isFile()) {
                            WbsLog.info("[wbs-tasks] missing model file " + path + " for " + project.getPath());
                            continue;
                        }
                        List<ModelPayload> payloads;
                        try (InputStream in = Files.newInputStream(file.toPath()); DataInputStream data = new DataInputStream(in)) {
                            payloads = ModelBundle.read(data);
                        } catch (IOException | RuntimeException e) {
                            WbsLog.info("[wbs-tasks] cannot read " + path + " for " + project.getPath() + ": " + e);
                            continue;
                        }
                        for (ModelPayload payload : payloads) {
                            if (!modelClassNames.contains(payload.getModelClassName())) {
                                continue;
                            }
                            // One entry that does not deserialize must not lose the others.
                            try {
                                Class<?> modelClass = Class.forName(payload.getModelClassName(), true, TaskTransport.class.getClassLoader());
                                Object model = payload.readModel(TaskTransport.class.getClassLoader());
                                if (model instanceof IdeaModulePayload) {
                                    ideaByBuild.computeIfAbsent(build, b -> new ArrayList<>()).add((IdeaModulePayload) model);
                                } else if (model instanceof KotlinDslScriptPayload) {
                                    scriptsByBuild.computeIfAbsent(build, b -> new ArrayList<>()).add((KotlinDslScriptPayload) model);
                                } else if (model != null) {
                                    consumer.consumeProjectModel(project, model, modelClass);
                                }
                            } catch (Exception e) {
                                WbsLog.info("[wbs-tasks] cannot deserialize " + payload.getModelClassName() + " from " + path
                                    + " for " + project.getPath() + ": " + e);
                            }
                        }
                    }
                }
            }
            for (Map.Entry<GradleBuild, List<IdeaModulePayload>> e : ideaByBuild.entrySet()) {
                try {
                    GradleBuild root = buildModels.iterator().next();
                    String prefix = e.getKey() == root ? "" : ":" + e.getKey().getBuildIdentifier().getRootDir().getName();
                    org.gradle.tooling.model.idea.IdeaProject assembled = IdeaProjectFromPayloads.assemble(e.getValue(), prefix);
                    WbsLog.info("[wbs-tasks] IdeaProject assembled for " + e.getKey().getBuildIdentifier().getRootDir().getName()
                        + ": " + assembled.getModules().size() + " modules (phase " + phase.getName() + ")");
                    consumer.consumeBuildModel(e.getKey(), assembled, org.gradle.tooling.model.idea.IdeaProject.class);
                } catch (RuntimeException ex) {
                    WbsLog.info("[wbs-tasks] cannot assemble IdeaProject for " + e.getKey().getBuildIdentifier().getRootDir() + ": " + ex);
                }
            }
            for (Map.Entry<GradleBuild, List<KotlinDslScriptPayload>> e : scriptsByBuild.entrySet()) {
                try {
                    org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel scripts = KotlinDslScriptsModelFromPayloads.assemble(e.getValue());
                    WbsLog.info("[wbs-tasks] KotlinDslScriptsModel assembled for " + e.getKey().getBuildIdentifier().getRootDir().getName()
                        + ": " + scripts.getScriptModels().size() + " scripts (phase " + phase.getName() + ")");
                    consumer.consumeProjectModel(e.getKey().getRootProject(), scripts, org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel.class);
                    consumer.consumeBuildModel(e.getKey(), scripts, org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel.class);
                } catch (RuntimeException ex) {
                    WbsLog.info("[wbs-tasks] cannot assemble KotlinDslScriptsModel for " + e.getKey().getBuildIdentifier().getRootDir() + ": " + ex);
                }
            }
        }
    }
}
