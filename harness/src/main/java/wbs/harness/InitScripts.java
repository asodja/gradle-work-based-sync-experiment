package wbs.harness;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Assembles the three init scripts IntelliJ passes to a sync, from the same resources the IDE uses
 * (GradleInitScriptUtil.kt in the IDE, resources under org/jetbrains/plugins/gradle/tooling/internal/init).
 * Order on the command line: mapper, idea plugin configurator, main.
 */
final class InitScripts {

    private static final String RES = "org/jetbrains/plugins/gradle/tooling/internal/init/";

    /** GradleInitScriptUtil.GRADLE_TOOLING_EXTENSION_CLASSES: jars every IDE init script puts on the daemon classpath. */
    static final List<String> BASE_TOOLING_EXTENSION_CLASSES = List.of(
        "com.intellij.openapi.util.SystemInfoRt",
        "com.intellij.platform.externalSystem.rt.ExternalSystemRtClass",
        "com.intellij.gradle.toolingExtension.GradleToolingExtensionClass",
        "com.intellij.gradle.toolingExtension.impl.GradleToolingExtensionImplClass",
        "kotlin.Unit",
        "com.google.gson.GsonBuilder",
        "com.amazon.ion.IonType",
        "com.google.common.collect.Multimap",
        "org.apache.commons.lang3.StringUtils",
        "gnu.trove.TObjectHash",
        "io.opentelemetry.api.trace.Span",
        "io.opentelemetry.context.ImplicitContextKeyed"
    );

    final Path mapper;
    final Path ideaPluginConfigurator;
    final Path main;

    private InitScripts(Path mapper, Path ideaPluginConfigurator, Path main) {
        this.mapper = mapper;
        this.ideaPluginConfigurator = ideaPluginConfigurator;
        this.main = main;
    }

    /** Extra script in task mode, registering the model tasks; null otherwise. */
    Path modelTasks;

    List<Path> inOrder() {
        List<Path> scripts = new ArrayList<>(List.of(mapper, ideaPluginConfigurator, main));
        if (modelTasks != null) {
            scripts.add(modelTasks);
        }
        return scripts;
    }

    static InitScripts write(Path dir, Collection<Class<?>> resolverToolingExtensionClasses, List<Path> extraJars, boolean tasks) throws IOException {
        Set<Class<?>> base = new LinkedHashSet<>();
        for (String name : BASE_TOOLING_EXTENSION_CLASSES) {
            base.add(load(name));
        }
        Set<Class<?>> all = new LinkedHashSet<>(base);
        all.addAll(resolverToolingExtensionClasses);
        Set<Path> baseJars = jarsOf(base);
        Set<Path> allJars = jarsOf(all);
        allJars.addAll(extraJars);

        Path mapper = write(dir, "ijMapper.gradle", resource("MapperInit.gradle"));
        Path configurator = write(dir, "ijIdeaPluginConfigurator.gradle", String.join(System.lineSeparator(),
            classpathScript(baseJars),
            resource("IdeaPluginConfigurator.gradle")));
        Path main = write(dir, "ijInit.gradle", String.join(System.lineSeparator(),
            classpathScript(allJars),
            resource("RegistryProcessor.gradle"),
            resource("JetGradlePlugin.gradle"),
            resource("Init.gradle").replaceFirst("IS_BUILD_SCR_PROJECT", "false")));
        InitScripts result = new InitScripts(mapper, configurator, main);
        if (tasks) {
            result.modelTasks = write(dir, "ijModelTasks.gradle", String.join(System.lineSeparator(),
                classpathScript(allJars),
                "com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTasks.apply(gradle)"));
        }
        return result;
    }

    static Set<Path> jarsOf(Collection<Class<?>> classes) {
        Set<Path> jars = new LinkedHashSet<>();
        for (Class<?> c : classes) {
            jars.add(jarOf(c));
        }
        return jars;
    }

    static Path jarOf(Class<?> c) {
        try {
            return Path.of(c.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("no jar for " + c, e);
        }
    }

    private static String classpathScript(Collection<Path> jars) throws IOException {
        String list = jars.stream()
            .map(p -> "mapPath(" + groovyString(p.toString()) + ")")
            .collect(Collectors.joining(", ", "[", "]"));
        return resource("ClassPathExtensionInitScript.gradle").replaceFirst("EXTENSIONS_JARS_PATH", Matcher.quoteReplacement(list));
    }

    static String groovyString(String s) {
        return "'" + s.replace("\\", "\\\\").replace("'", "\\'") + "'";
    }

    private static Class<?> load(String name) {
        try {
            return Class.forName(name, false, InitScripts.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("IDE class not on the harness classpath (pass -PideaHome): " + name, e);
        }
    }

    static String resource(String name) throws IOException {
        try (InputStream in = InitScripts.class.getClassLoader().getResourceAsStream(RES + name)) {
            if (in == null) {
                throw new IOException("init script resource not found: " + RES + name);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static Path write(Path dir, String name, String content) throws IOException {
        Path p = dir.resolve(name);
        Files.writeString(p, content);
        return p;
    }
}
