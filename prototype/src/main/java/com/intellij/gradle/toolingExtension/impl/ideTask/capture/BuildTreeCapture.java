package com.intellij.gradle.toolingExtension.impl.ideTask.capture;

import com.intellij.gradle.toolingExtension.impl.ideTask.payload.BuildTreePayload;
import org.gradle.api.Project;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.GradleInternal;
import org.gradle.api.invocation.Gradle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** The per-build payload of {@code ideBuildTree} (D29): project structure from the settings, base script model from Gradle's builder. */
public final class BuildTreeCapture {

    private BuildTreeCapture() {
    }

    /**
     * The build structure captured when its settings were evaluated, per build of the tree (D29), keyed by the build's
     * identity path: under Isolated Projects the {@code Gradle} object a project sees is not the settings' instance.
     */
    private static final Map<String, BuildTreePayload> TREES = Collections.synchronizedMap(new java.util.HashMap<>());

    /** {@code gradle.settingsEvaluated}: the build structure of this build, before any project is configured. */
    public static void capture(Settings settings) {
        Gradle gradle = settings.getGradle();
        BuildTreePayload tree = new BuildTreePayload();
        tree.identityPath = ((GradleInternal) gradle).getIdentityPath().getPath();
        tree.rootDir = settings.getRootDir().getAbsolutePath();
        tree.gradleVersion = gradle.getGradleVersion();
        tree.rootProject = node(settings.getRootProject(), tree.identityPath);
        TREES.put(tree.identityPath, tree);
    }

    /** The tree captured for this build, once; null when the settings hook did not run. */
    public static BuildTreePayload take(Gradle gradle) {
        return TREES.remove(((GradleInternal) gradle).getIdentityPath().getPath());
    }

    private static BuildTreePayload.Node node(ProjectDescriptor descriptor, String identityPath) {
        BuildTreePayload.Node node = new BuildTreePayload.Node();
        node.name = descriptor.getName();
        node.path = descriptor.getPath();
        node.projectDir = descriptor.getProjectDir().getAbsolutePath();
        // as GradleBuildBuilder: the build's identity path plus the project path, ":" stays ":" in the root build
        node.buildTreePath = identityPath.equals(":") ? node.path : node.path.equals(":") ? identityPath : identityPath + node.path;
        List<ProjectDescriptor> children = new ArrayList<>(descriptor.getChildren());
        children.sort(Comparator.comparing(ProjectDescriptor::getPath));
        for (ProjectDescriptor child : children) {
            node.children.add(node(child, identityPath));
        }
        return node;
    }

    /**
     * Gradle's own builder for {@code GradleDslBaseScriptModel} (2026.2's Kotlin plugin needs the model; the stock sync
     * requests it first thing in its action), run here so that the IDE needs no request for it. Internal API, by
     * reflection: a missing class or method yields no payload and the IDE requests the model instead.
     */
    @SuppressWarnings("unchecked")
    public static BuildTreePayload.DslBaseScript dslBaseScript(Gradle gradle, Project project) {
        try {
            Object builder = Class.forName("org.gradle.plugins.ide.internal.tooling.GradleDslBaseScriptModelBuilder").getConstructor().newInstance();
            Object buildState = GradleInternal.class.getMethod("getOwner").invoke(gradle);
            Object model = builder.getClass().getMethod("create", Class.forName("org.gradle.internal.build.BuildState")).invoke(builder, buildState);
            Object kotlin = call(model, "getKotlinDslBaseScriptModel");
            Object groovy = call(model, "getGroovyDslBaseScriptModel");
            BuildTreePayload.DslBaseScript result = new BuildTreePayload.DslBaseScript();
            result.kotlinScriptTemplatesClassPath = paths(kotlin, "getScriptTemplatesClassPath");
            result.kotlinCompileClassPath = paths(kotlin, "getCompileClassPath");
            result.kotlinImplicitImports = new ArrayList<>((List<String>) call(kotlin, "getImplicitImports"));
            try {
                result.kotlinTemplateClassNames = new ArrayList<>((List<String>) call(kotlin, "getTemplateClassNames"));
            } catch (NoSuchMethodException e) {
                // older model without template class names
            }
            result.groovyCompileClassPath = paths(groovy, "getCompileClassPath");
            result.groovyImplicitImports = new ArrayList<>((List<String>) call(groovy, "getImplicitImports"));
            return result;
        } catch (ReflectiveOperationException | RuntimeException e) {
            project.getLogger().warn("[ide-model-tasks] GradleDslBaseScriptModel not captured ({}); the IDE requests it", e.toString());
            return null;
        }
    }

    /** The model classes are package-private; their public getters need the accessibility flag. */
    private static Object call(Object target, String getter) throws ReflectiveOperationException {
        java.lang.reflect.Method method = target.getClass().getMethod(getter);
        method.setAccessible(true);
        return method.invoke(target);
    }

    private static List<String> paths(Object model, String getter) throws ReflectiveOperationException {
        List<String> result = new ArrayList<>();
        for (Object file : (List<?>) call(model, getter)) {
            result.add(((java.io.File) file).getPath());
        }
        return result;
    }
}
