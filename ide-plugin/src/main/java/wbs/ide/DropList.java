package wbs.ide;

import com.intellij.gradle.toolingExtension.impl.ideTask.PortedModels;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Prints the value of {@code -Dwbs.transport.drop}: the built-in providers of every ported model, as the patched
 * {@code GradleModelFetchAction} matches them (provider class simple name or toString()). Same mapping as the
 * harness's IdeProviders.useTaskTransport.
 */
public final class DropList {

    private static final Map<String, String> DEDICATED_PROVIDERS = Map.of(
        "GradleSourceSetModelProvider", "org.jetbrains.plugins.gradle.model.GradleSourceSetModel",
        "GradleSourceSetDependencyModelProvider", "org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel",
        "AndroidAwareGradleModelProvider", "org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel",
        "GradleExternalProjectModelProvider", "org.jetbrains.plugins.gradle.model.ExternalProject",
        "GradleTaskModelProvider", "org.jetbrains.plugins.gradle.model.GradleTaskModel",
        "GradleBuildScriptClasspathModelProvider", "org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel");

    /** {@code --keep-gradle-owned}: leave the IdeaProject and Kotlin DSL script builders to the IDE (until the resolver
     * assembles them from the payloads, design/m5-ide-consumer.md). */
    public static void main(String[] args) {
        boolean keepGradleOwned = java.util.Arrays.asList(args).contains("--keep-gradle-owned");
        Set<String> ported = new LinkedHashSet<>(PortedModels.all());
        if (keepGradleOwned) {
            ported.remove("com.intellij.gradle.toolingExtension.impl.ideTask.IdeaModulePayload");
            ported.remove("com.intellij.gradle.toolingExtension.impl.ideTask.KotlinDslScriptPayload");
        }
        Set<String> drop = new LinkedHashSet<>();
        for (Map.Entry<String, String> e : DEDICATED_PROVIDERS.entrySet()) {
            if (ported.contains(e.getValue())) {
                drop.add(e.getKey());
            }
        }
        for (String c : ported) {
            drop.add("ProjectModel(" + c + ")");
        }
        if (ported.contains("com.intellij.gradle.toolingExtension.impl.ideTask.IdeaModulePayload")) {
            drop.add("BuildModel(org.gradle.tooling.model.idea.IdeaProject)");
        }
        if (ported.contains("com.intellij.gradle.toolingExtension.impl.ideTask.KotlinDslScriptPayload")) {
            drop.add("KotlinDslScriptModelProvider");
        }
        System.out.println(String.join(",", drop));
    }
}
