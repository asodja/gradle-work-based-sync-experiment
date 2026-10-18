package com.intellij.gradle.toolingExtension.impl.ideTask;

import com.intellij.gradle.toolingExtension.impl.ideTask.capture.ProjectModelCapture;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.IdeaModulePayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.KotlinDslScriptPayload;

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
        // Gradle-owned IdeaProject, per project (D14): consumed by the IDE in the project model phase, where it
        // iterates IdeaProject.getModules() today.
        m.put("PROJECT_MODEL_PHASE", List.of(
            "org.jetbrains.plugins.gradle.model.ExternalProject",
            IdeaModulePayload.class.getName()));
        m.put("SOURCE_SET_MODEL_PHASE", List.of(
            "org.jetbrains.plugins.gradle.model.GradleSourceSetModel"));
        // Hard group (D13): captured with the builders too, in IntelliJ's request order: the download policy fills the
        // shared builder context for this project (no payload), then the dependency model. IntelliJ's other
        // context-only request, GradleSourceSetArtifactBuildRequest, runs for every project when it is evaluated
        // (ProjectModelCapture.indexSourceSetArtifacts), because the dependency model of one project reads the index entries
        // of the projects it depends on.
        m.put("DEPENDENCY_MODEL_PHASE", List.of(
            "com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy",
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
            "org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel",
            // M5 round 2c: the remaining small models of IntelliJ's action (null for projects without the plugin)
            "org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel",
            "org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel",
            "org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel",
            "org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel",
            "org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel",
            "org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel",
            "org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel",
            "org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel",
            // Gradle-owned KotlinDslScriptsModel, the project build script's entry per project (D14).
            KotlinDslScriptPayload.class.getName()));
        return m;
    }

    /** Requests that only fill IntelliJ's builder context for later builders; they yield no model to deliver. */
    public static final java.util.Set<String> CONTEXT_ONLY = java.util.Set.of(
        "com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy");

    /** Model class names to capture, in order, context-only requests included. */
    public static List<String> captureOrder() {
        return PROJECT_MODEL_TASK.values().stream().flatMap(List::stream).toList();
    }

    /** Model class names the bundles deliver (and the consumer stops requesting from builders). */
    public static List<String> all() {
        return captureOrder().stream().filter(n -> !CONTEXT_ONLY.contains(n)).toList();
    }
}
