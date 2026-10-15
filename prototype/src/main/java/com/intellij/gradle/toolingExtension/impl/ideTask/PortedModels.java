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
        // Hard group (D13): captured with the builders too, in IntelliJ's request order: the download policy and the
        // artifact-index request fill the shared per-capture context first (the index then holds this project's
        // own jar -> source set entries; the request itself yields no payload), then the dependency model.
        m.put("DEPENDENCY_MODEL_PHASE", List.of(
            "com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy",
            "com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest",
            "org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel"));
        // KotlinGradleModel and ProjectDependencies (hard group) plus the mechanical group (design/plan.md M3), all
        // served by GradleClassProjectModelProvider or a dedicated provider in IntelliJ's ADDITIONAL phase. Not here:
        // the out-of-scope Kotlin compiler-plugin and MPP models, and GradleBuildScriptClasspathModel, whose builder
        // needs the parent project's model from the shared builder context (LOG 2026-10-15); it stays a builder until
        // the consumer concatenates parent chains itself.
        m.put("ADDITIONAL_MODEL_PHASE", List.of(
            "org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel",
            "com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies",
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

    /** Requests that only fill IntelliJ's builder context for later builders; they yield no model to deliver. */
    public static final java.util.Set<String> CONTEXT_ONLY = java.util.Set.of(
        "com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy",
        "com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest");

    /** Model class names to capture, in order, context-only requests included. */
    public static List<String> captureOrder() {
        return PROJECT_MODEL_TASK.values().stream().flatMap(List::stream).toList();
    }

    /** Model class names the bundles deliver (and the consumer stops requesting from builders). */
    public static List<String> all() {
        return captureOrder().stream().filter(n -> !CONTEXT_ONLY.contains(n)).toList();
    }
}
