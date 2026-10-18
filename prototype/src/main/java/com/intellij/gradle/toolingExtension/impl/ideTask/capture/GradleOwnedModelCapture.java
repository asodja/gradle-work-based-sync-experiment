package com.intellij.gradle.toolingExtension.impl.ideTask.capture;

import com.intellij.gradle.toolingExtension.impl.ideTask.payload.IdeaModulePayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.KotlinDslScriptPayload;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.jvm.JvmTestSuite;
import org.gradle.api.tasks.SourceSet;
import org.gradle.plugins.ide.idea.model.IdeaModel;
import org.gradle.plugins.ide.idea.model.IdeaModule;
import org.gradle.testing.base.TestingExtension;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Captures of the two Gradle-owned build models that otherwise force a model-building action (D14): the module
 * part of {@code IdeaProject} and the per-script part of {@code KotlinDslScriptsModel}, each from the project's own
 * state. Gradle's builders do the same work for the whole build at once: {@code IdeaModelBuilder} applies the idea
 * plugin to every project and reads its {@code IdeaModule}; {@code KotlinDslScriptsModelBuilder} runs the
 * single-script builder for every script of the build.
 */
public final class GradleOwnedModelCapture {

    public static final String IDEA_MODULE = IdeaModulePayload.class.getName();
    public static final String KOTLIN_DSL_SCRIPT = KotlinDslScriptPayload.class.getName();

    private GradleOwnedModelCapture() {
    }

    public static boolean handles(String modelClassName) {
        return IDEA_MODULE.equals(modelClassName) || KOTLIN_DSL_SCRIPT.equals(modelClassName);
    }

    public static Object capture(String modelClassName, Project project) throws Exception {
        if (IDEA_MODULE.equals(modelClassName)) {
            return ideaModule(project);
        }
        if (KOTLIN_DSL_SCRIPT.equals(modelClassName)) {
            return kotlinDslScript(project);
        }
        throw new IllegalArgumentException(modelClassName);
    }

    /**
     * The module as the idea plugin configures it. When the project applied the plugin itself (gradle/gradle does
     * through its build logic), the {@code idea.module} state is read, customizations included, except the language
     * levels: their conventions compare all projects of the build and are derived by the consumer from the raw
     * compatibility values instead. The plugin cannot be applied here under Isolated Projects (its apply reaches
     * the root project and all projects), so without it the payload mirrors the plugin's defaults from the java
     * plugin, and {@code plugins.withType(IdeaPlugin)} customizations of the build never run.
     */
    private static IdeaModulePayload ideaModule(Project project) {
        IdeaModulePayload p = new IdeaModulePayload();
        p.projectPath = project.getPath();
        p.projectName = project.getName();
        p.description = project.getDescription();
        p.buildRootDir = project.getRootDir();
        p.projectDir = project.getProjectDir();
        p.buildDir = project.getLayout().getBuildDirectory().get().getAsFile();
        p.buildScript = project.getBuildFile();
        JavaPluginExtension java = project.getExtensions().findByType(JavaPluginExtension.class);
        if (java != null) {
            p.sourceCompatibility = java.getSourceCompatibility().toString();
            p.targetCompatibility = java.getTargetCompatibility().toString();
        }
        Object idea = project.getExtensions().findByName("idea");
        if (idea instanceof IdeaModel && ((IdeaModel) idea).getModule() != null) {
            IdeaModule module = ((IdeaModel) idea).getModule();
            Set<File> generated = module.getGeneratedSourceDirs();
            p.ideaPluginApplied = true;
            p.moduleName = module.getName();
            p.contentRoot = module.getContentRoot();
            p.sourceDirs = flagged(module.getSourceDirs(), generated);
            p.testDirs = flagged(module.getTestSources().getFiles(), generated);
            p.resourceDirs = flagged(module.getResourceDirs(), generated);
            p.testResourceDirs = flagged(module.getTestResources().getFiles(), generated);
            p.excludeDirs = new ArrayList<>(module.getExcludeDirs());
            p.jdkName = module.getJdkName();
            p.inheritOutputDirs = Boolean.TRUE.equals(module.getInheritOutputDirs());
            p.outputDir = module.getOutputDir();
            p.testOutputDir = module.getTestOutputDir();
        } else {
            p.moduleName = project.getName();
            p.contentRoot = project.getProjectDir();
            if (java != null) {
                SourceSet main = java.getSourceSets().findByName("main");
                if (main != null) {
                    p.sourceDirs = flagged(main.getAllJava().getSrcDirs(), Set.of());
                    p.resourceDirs = flagged(main.getResources().getSrcDirs(), Set.of());
                }
                TestingExtension testing = project.getExtensions().findByType(TestingExtension.class);
                if (testing != null) {
                    for (JvmTestSuite suite : testing.getSuites().withType(JvmTestSuite.class)) {
                        p.testDirs.putAll(flagged(suite.getSources().getAllJava().getSrcDirs(), Set.of()));
                        p.testResourceDirs.putAll(flagged(suite.getSources().getResources().getSrcDirs(), Set.of()));
                    }
                }
            }
            p.excludeDirs = new ArrayList<>(List.of(project.file(".gradle"), p.buildDir));
        }
        if (project.getParent() == null) {
            p.ideaProjectName = project.getName();
            p.currentJdkVersion = JavaVersion.current().toString();
            p.currentJdkHome = new File(System.getProperty("java.home"));
        }
        return p;
    }

    private static Map<File, Boolean> flagged(Set<File> dirs, Set<File> generated) {
        Map<File, Boolean> out = new LinkedHashMap<>();
        for (File dir : dirs) {
            out.put(dir, generated.contains(dir));
        }
        return out;
    }

    /**
     * Computes the build-wide stage-1 accessors classpath once, on the root project's thread, before captures run
     * in parallel. Its lazy initializer takes the root project's model lock inside a JVM monitor; parallel captures
     * that hold their own project lock and enter that monitor deadlock against the root project's capture (observed
     * on gradle/gradle at configuration cache store time, 8 threads idle for hours). Gradle's Isolated-Projects-safe
     * KotlinDslScriptsModel builder calls the same method before it fans out.
     */
    public static void prepareForParallelCaptures(Project rootProject) {
        ClassLoader loader = GradleOwnedModelCapture.class.getClassLoader();
        try {
            Object services = rootProject.getClass().getMethod("getServices").invoke(rootProject);
            Class<?> generatorClass = Class.forName("org.gradle.kotlin.dsl.accessors.Stage1BlocksAccessorClassPathGenerator", true, loader);
            Object generator = Class.forName("org.gradle.internal.service.ServiceLookup", true, loader)
                .getMethod("get", java.lang.reflect.Type.class).invoke(services, generatorClass);
            generatorClass.getMethod("prepareForParallelAccess").invoke(generator);
        } catch (Exception e) {
            rootProject.getLogger().warn("[ide-model-tasks] stage-1 accessors not prepared for {}: {}", rootProject.getPath(), e.toString());
        }
        try {
            // The script model's source path includes the Gradle distribution sources, looked up once per build
            // invocation in a build-tree cache whose lock is held during the lookup; it goes to services.gradle.org
            // and took ~10 s on a slow connection. Resolved here, on one thread, so the parallel captures do not all
            // block on it (thread samples: 16 of 18 capture threads waiting on that lock). Gradle skips the lookup
            // when the installation has a src directory (SourcePathProvider.sourceRootsOf), so does this.
            File gradleHome = rootProject.getGradle().getGradleHomeDir();
            if (gradleHome != null && new File(gradleHome, "src").exists()) {
                return;
            }
            Class<?> resolverClass = Class.forName("org.gradle.kotlin.dsl.resolver.SourceDistributionResolver", true, loader);
            Object resolver = resolverClass.getConstructor(Project.class).newInstance(rootProject);
            resolverClass.getMethod("sourceDirs").invoke(resolver);
        } catch (Exception e) {
            rootProject.getLogger().warn("[ide-model-tasks] distribution sources not prepared for {}: {}", rootProject.getPath(), e.toString());
        }
    }

    /**
     * Gradle's {@code KotlinBuildScriptModelBuilder.kotlinBuildScriptModelFor(project, parameter)} for every script
     * that belongs to this project, the same files the {@code KotlinDslScriptsModel} builder discovers per project:
     * the build script and the precompiled script plugins of a {@code kotlin-dsl} project; the settings script and
     * the init scripts are attached to the root project. Internal Kotlin API, called reflectively (internal members
     * carry a mangled name).
     */
    private static KotlinDslScriptPayload kotlinDslScript(Project project) throws Exception {
        List<File> scripts = new ArrayList<>();
        if (isKotlinDsl(project.getBuildFile())) {
            scripts.add(project.getBuildFile());
        }
        if (project.getPlugins().hasPlugin("org.gradle.kotlin.kotlin-dsl")) {
            scripts.addAll(precompiledScriptPlugins(project));
        }
        if (project.getParent() == null) {
            File settings = settingsScript(project);
            if (settings != null && isKotlinDsl(settings)) {
                scripts.add(settings);
            }
            for (File init : project.getGradle().getStartParameter().getAllInitScripts()) {
                if (isKotlinDsl(init)) {
                    scripts.add(init);
                }
            }
        }
        if (scripts.isEmpty()) {
            return null;
        }
        ClassLoader loader = GradleOwnedModelCapture.class.getClassLoader();
        Class<?> builderClass = Class.forName("org.gradle.kotlin.dsl.tooling.builders.KotlinBuildScriptModelBuilder", true, loader);
        Object builder = builderClass.getField("INSTANCE").get(null);
        Class<?> parameterClass = Class.forName("org.gradle.kotlin.dsl.tooling.builders.KotlinBuildScriptModelParameter", true, loader);
        Constructor<?> parameterCtor = parameterClass.getConstructor(File.class, String.class);
        Object correlationId = project.findProperty("org.gradle.kotlin.dsl.provider.cid");
        Method build = null;
        for (Method m : builderClass.getMethods()) {
            if (m.getName().startsWith("kotlinBuildScriptModelFor") && m.getParameterCount() == 2) {
                build = m;
            }
        }
        if (build == null) {
            throw new IllegalStateException("KotlinBuildScriptModelBuilder.kotlinBuildScriptModelFor(Project, parameter) not found");
        }
        KotlinDslScriptPayload payload = new KotlinDslScriptPayload();
        for (File script : scripts) {
            KotlinDslScriptPayload.Script entry = new KotlinDslScriptPayload.Script();
            entry.scriptFile = script;
            payload.scripts.add(entry);
            try {
                Object parameter = parameterCtor.newInstance(script.getCanonicalFile(), correlationId == null ? null : correlationId.toString());
                fill(entry, build.invoke(builder, project, parameter));
            } catch (Exception e) {
                // Like the scripts model builder: one script's failure degrades its entry, not the whole model.
                Throwable cause = e instanceof java.lang.reflect.InvocationTargetException && e.getCause() != null ? e.getCause() : e;
                entry.exceptions.add(cause.toString());
                project.getLogger().warn("[ide-model-tasks] no Kotlin DSL script model for {}: {}", script, cause.toString());
            }
        }
        return payload;
    }

    private static boolean isKotlinDsl(File script) {
        return script != null && script.isFile() && script.getName().endsWith(".gradle.kts");
    }

    @SuppressWarnings("unchecked")
    private static List<File> precompiledScriptPlugins(Project project) throws Exception {
        ClassLoader loader = GradleOwnedModelCapture.class.getClassLoader();
        Object services = project.getClass().getMethod("getServices").invoke(project);
        Class<?> supportClass = Class.forName("org.gradle.kotlin.dsl.provider.PrecompiledScriptPluginsSupport", true, loader);
        Object support = Class.forName("org.gradle.internal.service.ServiceLookup", true, loader)
            .getMethod("get", java.lang.reflect.Type.class).invoke(services, supportClass);
        return (List<File>) supportClass.getMethod("collectScriptPluginFilesOf", Project.class).invoke(support, project);
    }

    /** {@code GradleInternal.getSettings().getSettingsScript().getFileName()}; the root project's own directory otherwise. */
    private static File settingsScript(Project rootProject) {
        try {
            Object gradle = rootProject.getGradle();
            Object settings = gradle.getClass().getMethod("getSettings").invoke(gradle);
            Object source = settings.getClass().getMethod("getSettingsScript").invoke(settings);
            Object fileName = source.getClass().getMethod("getFileName").invoke(source);
            return fileName == null ? null : new File(fileName.toString());
        } catch (Exception e) {
            File candidate = new File(rootProject.getProjectDir(), "settings.gradle.kts");
            return candidate.isFile() ? candidate : null;
        }
    }

    private static void fill(KotlinDslScriptPayload.Script entry, Object model) throws Exception {
        entry.classPath = files(model, "getClassPath");
        entry.sourcePath = files(model, "getSourcePath");
        entry.implicitImports = strings(model, "getImplicitImports");
        for (Object report : (List<?>) model.getClass().getMethod("getEditorReports").invoke(model)) {
            Object position = report.getClass().getMethod("getPosition").invoke(report);
            entry.editorReports.add(report.getClass().getMethod("getSeverity").invoke(report) + " "
                + report.getClass().getMethod("getMessage").invoke(report)
                + (position == null ? "" : " @" + position.getClass().getMethod("getLine").invoke(position)
                    + ":" + position.getClass().getMethod("getColumn").invoke(position)));
        }
        entry.exceptions = strings(model, "getExceptions");
    }

    @SuppressWarnings("unchecked")
    private static List<File> files(Object model, String getter) throws Exception {
        return new ArrayList<>((List<File>) model.getClass().getMethod(getter).invoke(model));
    }

    @SuppressWarnings("unchecked")
    private static List<String> strings(Object model, String getter) throws Exception {
        return new ArrayList<>((List<String>) model.getClass().getMethod(getter).invoke(model));
    }
}
