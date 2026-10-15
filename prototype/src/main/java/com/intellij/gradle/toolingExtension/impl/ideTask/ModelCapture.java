package com.intellij.gradle.toolingExtension.impl.ideTask;

import com.intellij.gradle.toolingExtension.impl.modelBuilder.DefaultModelBuilderContext;
import com.intellij.gradle.toolingExtension.impl.modelSerialization.SerializationServiceNotFoundException;
import com.intellij.gradle.toolingExtension.impl.modelSerialization.ToolingSerializer;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.jetbrains.plugins.gradle.tooling.ModelBuilderContext;
import org.jetbrains.plugins.gradle.tooling.ModelBuilderService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Captures a model at task configuration time by running IntelliJ's own builder for it (DECISIONS D8) and
 * serializing the result with IntelliJ's serializer for the class (IntelliJ's default Java-serialization
 * service when the model has no Ion serializer).
 */
final class ModelCapture {

    private static final List<ModelBuilderService> BUILDERS = loadBuilders();
    private static final ToolingSerializer SERIALIZER = new ToolingSerializer();

    private ModelCapture() {
    }

    static Provider<List<ModelPayload>> payloads(Project project, List<String> modelClassNames) {
        return project.provider(() -> {
            ModelBuilderContext context = new DefaultModelBuilderContext(project.getGradle());
            List<ModelPayload> result = new ArrayList<>();
            for (String modelClassName : modelClassNames) {
                // Like ExtraModelBuilder.buildServiceModel: a failing builder reports and yields no model, it does
                // not fail the sync (or here: the task's input evaluation).
                Object model;
                try {
                    model = build(modelClassName, project, context);
                } catch (Exception e) {
                    report(modelClassName, project, context, e);
                    continue;
                }
                if (model != null) {
                    result.add(serialize(modelClassName, model));
                }
            }
            return result;
        });
    }

    private static ModelPayload serialize(String modelClassName, Object model) throws IOException {
        try {
            return new ModelPayload(modelClassName, ModelPayload.ION, SERIALIZER.write(model));
        } catch (SerializationServiceNotFoundException e) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
                out.writeObject(model);
            }
            return new ModelPayload(modelClassName, ModelPayload.JAVA, bytes.toByteArray());
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
        ServiceLoader.load(ModelBuilderService.class, ModelCapture.class.getClassLoader()).forEach(builders::add);
        return builders;
    }
}
