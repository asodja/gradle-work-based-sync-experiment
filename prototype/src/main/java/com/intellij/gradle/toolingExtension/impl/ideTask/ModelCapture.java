package com.intellij.gradle.toolingExtension.impl.ideTask;

import com.intellij.gradle.toolingExtension.impl.modelBuilder.DefaultModelBuilderContext;
import com.intellij.gradle.toolingExtension.impl.modelSerialization.ToolingSerializer;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.jetbrains.plugins.gradle.tooling.ModelBuilderContext;
import org.jetbrains.plugins.gradle.tooling.ModelBuilderService;

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
                Object model = build(modelClassName, project, context);
                if (model != null) {
                    result.add(new ModelPayload(modelClassName, SERIALIZER.write(model)));
                }
            }
            return result;
        });
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
