package com.intellij.gradle.toolingExtension.impl.ideTask;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The models the task version produces instead of IntelliJ's builders, by phase group (DECISIONS D9). Shared with
 * the consumer (harness, later the IDE), which stops requesting these models from builders and reads them from
 * the bundles instead. Model class names, not classes: the request interface must stay loadable without them.
 */
public final class PortedModels {

    /** IntelliJ fetch phase name (GradleModelFetchPhase.getName()) -> model class names delivered in that phase. */
    public static final Map<String, List<String>> PROJECT_MODEL_TASK = projectModelTask();

    private PortedModels() {
    }

    private static Map<String, List<String>> projectModelTask() {
        Map<String, List<String>> m = new LinkedHashMap<>();
        m.put("PROJECT_MODEL_PHASE", List.of(
            "org.jetbrains.plugins.gradle.model.ExternalProject"));
        m.put("SOURCE_SET_MODEL_PHASE", List.of(
            "org.jetbrains.plugins.gradle.model.GradleSourceSetModel"));
        // The mechanical group (design/plan.md M3), all served by GradleClassProjectModelProvider or a dedicated
        // provider in IntelliJ's ADDITIONAL phase. Not here: the hard group (GradleSourceSetDependencyModel,
        // KotlinGradleModel, ProjectDependencies), the out-of-scope Kotlin compiler-plugin and MPP models, and
        // GradleBuildScriptClasspathModel, whose builder needs the parent project's model from the shared builder
        // context (LOG 2026-10-15); it stays a builder until the consumer concatenates parent chains itself.
        m.put("ADDITIONAL_MODEL_PHASE", List.of(
            "org.jetbrains.plugins.gradle.model.GradleTaskModel",
            "org.jetbrains.plugins.gradle.model.GradleExtensions",
            "org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel",
            "org.jetbrains.plugins.gradle.model.IntelliJProjectSettings",
            "org.jetbrains.plugins.gradle.model.IntelliJSettings",
            "org.jetbrains.plugins.gradle.model.DependencyAccessorsModel",
            "org.jetbrains.plugins.gradle.model.VersionCatalogsModel",
            "org.jetbrains.plugins.gradle.model.AnnotationProcessingModel",
            "org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel",
            "org.jetbrains.plugins.gradle.model.RepositoryModels",
            "com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel",
            "org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel"));
        return m;
    }

    public static List<String> all() {
        return PROJECT_MODEL_TASK.values().stream().flatMap(List::stream).toList();
    }
}
