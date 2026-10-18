package com.intellij.gradle.toolingExtension.impl.ideTask.capture;

import com.intellij.gradle.toolingExtension.impl.ideTask.PortedModels;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.CompactDependenciesPayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.KotlinDslScriptPayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelPayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelText;

import com.intellij.gradle.toolingExtension.impl.modelSerialization.SerializationServiceNotFoundException;
import com.intellij.gradle.toolingExtension.impl.modelSerialization.ToolingSerializer;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel;
import org.jetbrains.plugins.gradle.tooling.ModelBuilderContext;
import org.jetbrains.plugins.gradle.tooling.ModelBuilderService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Captures a model at task configuration time by running IntelliJ's own builder for it (DECISIONS D8) and puts it
 * into its state form ({@link CapturedModel}); {@link #toPayloads} turns the state into the bundle entries when the
 * task runs (IntelliJ's serializer for the class, plain Java serialization when the model has no Ion serializer,
 * the compact bytes for a {@link CompactDependenciesPayload}).
 */
public final class ProjectModelCapture {

    private static final List<ModelBuilderService> BUILDERS = loadBuilders();

    static final String ARTIFACT_INDEX_REQUEST =
        "com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest";

    private ProjectModelCapture() {
    }

    public static ModelBuilderContext context(Project project) {
        return BuilderContextService.of(project.getGradle());
    }

    /**
     * Fills the shared artifact index with this project's jar -> source set entries, right after the project is
     * evaluated. IntelliJ requests this for every project before it builds any dependency model; here the order is
     * given by Gradle: resolving a project dependency inside a capture evaluates the target project first, so its
     * entries are present when the capture looks them up.
     */
    public static void indexSourceSetArtifacts(Project project) {
        ModelBuilderContext context = context(project);
        try {
            build(ARTIFACT_INDEX_REQUEST, project, context);
            realizeSourceSetState(project);
        } catch (Exception e) {
            report(ARTIFACT_INDEX_REQUEST, project, context, e);
        }
        // The dependency download policy is build-level state in the shared context that IntelliJ's dependency
        // resolver reads for every project; a parallel capture that runs before the owning project's capture would
        // otherwise fall back to the default policy and attach library sources ("policy wasn't collected").
        try {
            build(DOWNLOAD_POLICY, project, context);
        } catch (Exception e) {
            report(DOWNLOAD_POLICY, project, context, e);
        }
    }

    static final String DOWNLOAD_POLICY = "com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy";

    /**
     * Other projects' dependency captures read this project's source sets through the index (source dirs, output
     * dirs), in parallel and without this project's lock. Evaluating those file collections realizes lazily
     * registered tasks ({@code output.dir(taskProvider)}, {@code srcDir(taskProvider)}), which must happen once, here,
     * under the project's own lock: concurrently from the captures it fails with "Could not create task" and the
     * dependency model of every reader is lost for that run. IntelliJ's task warm-up request realizes every task
     * before its builders run for the same reason.
     */
    private static void realizeSourceSetState(Project project) {
        SourceSetContainer sourceSets = project.getExtensions().findByType(SourceSetContainer.class);
        if (sourceSets == null) {
            return;
        }
        for (SourceSet sourceSet : sourceSets) {
            sourceSet.getAllJava().getSrcDirs();
            sourceSet.getOutput().getClassesDirs().getFiles();
            sourceSet.getOutput().getResourcesDir();
            sourceSet.getOutput().getDirs().getFiles();
        }
    }

    public static Provider<List<CapturedModel>> captures(Project project, List<String> modelClassNames, boolean compact) {
        // Memoized: the same provider value is read for the fingerprint and for the models.
        Object[] memo = new Object[1];
        return project.provider(() -> {
            synchronized (memo) {
                if (memo[0] != null) {
                    @SuppressWarnings("unchecked") List<CapturedModel> cached = (List<CapturedModel>) memo[0];
                    return cached;
                }
                List<CapturedModel> computed = capture(project, modelClassNames, compact);
                memo[0] = computed;
                return computed;
            }
        });
    }

    /** The bundle entries for the captured models, whatever state form each one is in. */
    public static List<ModelPayload> toPayloads(List<CapturedModel> models) throws IOException {
        // One serializer per call: IntelliJ's Ion SerializationServices keep per-instance write state and are not
        // thread-safe (the IDE serializes on a single-thread executor); the configuration cache store evaluates task
        // inputs of different projects in parallel, and so do the task actions.
        ToolingSerializer serializer = new ToolingSerializer();
        List<ModelPayload> result = new ArrayList<>(models.size());
        for (CapturedModel model : models) {
            Object state = model.getModel();
            if (state instanceof ModelPayload) {
                result.add((ModelPayload) state);
            } else if (state instanceof CompactDependenciesPayload) {
                result.add(new ModelPayload(model.getModelClassName(), ModelPayload.COMPACT_DEPENDENCIES, model.getFingerprint(),
                    ((CompactDependenciesPayload) state).toBytes()));
            } else {
                result.add(serialize(serializer, model));
            }
        }
        return result;
    }

    /**
     * The state form of a captured model (D26). The dependency model becomes a string table plus records, the
     * script payload stays an object (lists of paths, which the configuration cache's string table shares across
     * projects); everything else is serialized now, so the entry holds its deflated bytes.
     */
    private static Object stateOf(ToolingSerializer serializer, CapturedModel captured, boolean compact) throws IOException {
        Object model = captured.getModel();
        if (compact) {
            if (model instanceof GradleSourceSetDependencyModel) {
                try {
                    return CompactDependenciesPayload.of((GradleSourceSetDependencyModel) model);
                } catch (IllegalArgumentException e) {
                    // a dependency type the compact format does not know: keep the serialized form
                }
            }
            if (model instanceof KotlinDslScriptPayload) {
                return model;
            }
        }
        return serialize(serializer, captured);
    }

    private static List<CapturedModel> capture(Project project, List<String> modelClassNames, boolean compact) {
        {
            ModelBuilderContext context = context(project);
            ToolingSerializer serializer = new ToolingSerializer();
            List<CapturedModel> result = new ArrayList<>();
            boolean timing = project.findProperty("ideModelTasks.timing") != null;
            for (String modelClassName : modelClassNames) {
                long started = System.nanoTime();
                long startedEpoch = System.currentTimeMillis();
                // Like ExtraModelBuilder.buildServiceModel: a failing builder reports and yields no model, it does
                // not fail the sync (or here: the task's input evaluation).
                Object model;
                try {
                    model = GradleOwnedModelCapture.handles(modelClassName)
                        ? GradleOwnedModelCapture.capture(modelClassName, project)
                        : build(modelClassName, project, context);
                } catch (Exception e) {
                    report(modelClassName, project, context, e);
                    continue;
                }
                if (model == null || PortedModels.CONTEXT_ONLY.contains(modelClassName)) {
                    continue;
                }
                try {
                    CapturedModel captured = new CapturedModel(modelClassName, ModelText.fingerprint(model), model);
                    result.add(new CapturedModel(modelClassName, captured.getFingerprint(), stateOf(serializer, captured, compact)));
                } catch (Exception e) {
                    report(modelClassName, project, context, e);
                } finally {
                    if (timing) {
                        // project model ms startEpochMs thread: enough for a timeline of the parallel captures
                        project.getLogger().lifecycle("[ide-model-tasks] timing {} {} {} {} {}", project.getPath(), modelClassName,
                            (System.nanoTime() - started) / 1_000_000, startedEpoch, Thread.currentThread().getName().replace(' ', '_'));
                    }
                }
            }
            return result;
        }
    }

    public static ModelPayload serialize(ToolingSerializer serializer, CapturedModel captured) throws IOException {
        Object model = captured.getModel();
        try {
            return new ModelPayload(captured.getModelClassName(), ModelPayload.ION, captured.getFingerprint(), serializer.write(model));
        } catch (SerializationServiceNotFoundException e) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
                out.writeObject(model);
            }
            return new ModelPayload(captured.getModelClassName(), ModelPayload.JAVA, captured.getFingerprint(), bytes.toByteArray());
        }
    }

    private static void report(String modelClassName, Project project, ModelBuilderContext context, Exception e) {
        project.getLogger().warn("[ide-model-tasks] no {} for {}: {}", modelClassName, project.getPath(), e.toString());
        for (ModelBuilderService builder : BUILDERS) {
            if (builder.canBuild(modelClassName)) {
                try {
                    builder.reportErrorMessage(modelClassName, project, context, e);
                } catch (RuntimeException ignored) {
                    // the report itself must not break the capture
                }
                return;
            }
        }
    }

    private static Object build(String modelClassName, Project project, ModelBuilderContext context) {
        for (ModelBuilderService builder : BUILDERS) {
            if (builder.canBuild(modelClassName)) {
                if (builder instanceof ModelBuilderService.Ex) {
                    return ((ModelBuilderService.Ex) builder).buildAll(modelClassName, project, context);
                }
                return builder.buildAll(modelClassName, project);
            }
        }
        throw new IllegalStateException("no IntelliJ model builder for " + modelClassName + " on the init script classpath");
    }

    private static List<ModelBuilderService> loadBuilders() {
        List<ModelBuilderService> builders = new ArrayList<>();
        ServiceLoader.load(ModelBuilderService.class, ProjectModelCapture.class.getClassLoader()).forEach(builders::add);
        return builders;
    }
}
