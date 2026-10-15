package wbs.harness;

import org.jetbrains.plugins.gradle.model.ProjectImportModelProvider;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The model providers an IntelliJ sync registers, obtained from the IDE's own resolver extensions
 * ({@code GradleProjectResolverExtension}, EP {@code projectResolve}) found in the plugin descriptors on the
 * classpath, exactly as {@code GradleProjectResolver.doResolveProjectInfo} collects them. Resolvers whose
 * provider needs a live {@code ProjectResolverContext} are handled by name.
 */
final class IdeProviders {

    private static final Pattern PROJECT_RESOLVE = Pattern.compile("<projectResolve\\s+implementation=\"([^\"]+)\"");

    private final List<ProjectImportModelProvider> providers = new ArrayList<>();
    private final Set<Class<?>> toolingExtensionClasses = new LinkedHashSet<>();
    private final Set<Class<?>> targetTypes = new LinkedHashSet<>();
    private final List<String> extraCommandLineArgs = new ArrayList<>();
    final List<String> log = new ArrayList<>();

    List<ProjectImportModelProvider> providers() {
        return providers;
    }

    /** IntelliJ's dedicated providers and the model class each one delivers (the generic ones say so in toString). */
    private static final java.util.Map<String, String> DEDICATED_PROVIDERS = java.util.Map.of(
        "GradleSourceSetModelProvider", "org.jetbrains.plugins.gradle.model.GradleSourceSetModel",
        "GradleSourceSetDependencyModelProvider", "org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel",
        "AndroidAwareGradleModelProvider", "org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel",
        "GradleExternalProjectModelProvider", "org.jetbrains.plugins.gradle.model.ExternalProject",
        "GradleTaskModelProvider", "org.jetbrains.plugins.gradle.model.GradleTaskModel",
        "GradleBuildScriptClasspathModelProvider", "org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel");

    /** All-tasks floor (D14): no builder model at all, only the request and file providers. */
    void useTasksOnly(IdeTasksTransport transport) {
        List<String> removed = new ArrayList<>();
        providers.forEach(p -> removed.add(p.toString()));
        providers.clear();
        providers.addAll(transport.providers());
        log.add("TASKS-ONLY MODE: removed all " + removed.size() + " providers; added " + transport.providers());
    }

    /** Task mode: the file transport replaces the IntelliJ providers of every ported model. */
    void useTaskTransport(IdeTasksTransport transport) {
        java.util.Set<String> ported = new java.util.HashSet<>(com.intellij.gradle.toolingExtension.impl.ideTask.PortedModels.all());
        List<String> removed = new ArrayList<>();
        providers.removeIf(p -> {
            String dedicated = DEDICATED_PROVIDERS.get(p.getClass().getSimpleName());
            boolean remove = dedicated != null ? ported.contains(dedicated)
                : ported.stream().anyMatch(c -> p.toString().equals("ProjectModel(" + c + ")"));
            if (remove) {
                removed.add(p.toString());
            }
            return remove;
        });
        providers.addAll(transport.providers());
        log.add("TASK MODE: removed " + removed + "; added " + transport.providers());
    }

    Set<Class<?>> toolingExtensionClasses() {
        return toolingExtensionClasses;
    }

    Set<Class<?>> targetTypes() {
        return targetTypes;
    }

    List<String> extraCommandLineArgs() {
        return extraCommandLineArgs;
    }

    static IdeProviders create() throws IOException {
        IdeProviders p = new IdeProviders();
        for (String resolver : sortedByOrder(resolverClassNames())) {
            p.collect(resolver);
        }
        return p;
    }

    /** ExternalSystemApiUtil.ORDER_AWARE_COMPARATOR: by {@code @Order} value, unannotated classes keep their position. */
    private static List<String> sortedByOrder(List<String> names) {
        List<String> sorted = new ArrayList<>(names);
        sorted.sort((a, b) -> Integer.compare(orderOf(a), orderOf(b)));
        return sorted;
    }

    private static int orderOf(String className) {
        try {
            Class<?> c = Class.forName(className, false, IdeProviders.class.getClassLoader());
            for (java.lang.annotation.Annotation a : c.getAnnotations()) {
                if (a.annotationType().getSimpleName().equals("Order")) {
                    return (Integer) a.annotationType().getMethod("value").invoke(a);
                }
            }
        } catch (Throwable ignored) {
            // unannotated or not loadable: default order
        }
        return 0;
    }

    private void collect(String className) {
        Object resolver;
        try {
            Class<?> c = Class.forName(className, true, IdeProviders.class.getClassLoader());
            Constructor<?> ctor = c.getDeclaredConstructor();
            ctor.setAccessible(true);
            resolver = ctor.newInstance();
        } catch (Throwable t) {
            log.add("SKIP " + className + ": cannot instantiate: " + t);
            return;
        }
        List<ProjectImportModelProvider> found = new ArrayList<>();
        try {
            found.addAll(call(resolver, "getModelProviders"));
        } catch (Throwable t) {
            ProjectImportModelProvider fallback = fallbackProvider(className);
            if (fallback == null) {
                log.add("SKIP " + className + ": getModelProviders failed: " + unwrap(t));
            } else {
                found.add(fallback);
                log.add("FALLBACK " + className + ": " + fallback + " (" + unwrap(t) + ")");
            }
        }
        providers.addAll(found);
        try {
            toolingExtensionClasses.addAll(call(resolver, "getToolingExtensionsClasses"));
        } catch (Throwable t) {
            log.add("WARN " + className + ": getToolingExtensionsClasses failed: " + unwrap(t));
        }
        try {
            targetTypes.addAll(call(resolver, "getTargetTypes"));
        } catch (Throwable t) {
            log.add("WARN " + className + ": getTargetTypes failed: " + unwrap(t));
        }
        try {
            extraCommandLineArgs.addAll(call(resolver, "getExtraCommandLineArgs"));
        } catch (Throwable t) {
            log.add("WARN " + className + ": getExtraCommandLineArgs failed: " + unwrap(t));
        }
        log.add("OK " + className + " -> " + found);
    }

    /** Providers of resolvers whose {@code getModelProvider()} reads the resolver context. */
    private static ProjectImportModelProvider fallbackProvider(String resolverClassName) {
        if (resolverClassName.endsWith(".KotlinGradleProjectResolverExtension")) {
            // AndroidAwareGradleModelProvider(isAndroidPluginRequestingKotlinGradleModel = false)
            try {
                Class<?> c = Class.forName("org.jetbrains.kotlin.idea.gradleTooling.AndroidAwareGradleModelProvider");
                return (ProjectImportModelProvider) c.getConstructor(boolean.class).newInstance(false);
            } catch (Throwable t) {
                throw new IllegalStateException("AndroidAwareGradleModelProvider not constructible", t);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> Collection<T> call(Object target, String method) throws Exception {
        Method m = target.getClass().getMethod(method);
        m.setAccessible(true);
        Object r = m.invoke(target);
        return r == null ? List.of() : (Collection<T>) r;
    }

    private static Throwable unwrap(Throwable t) {
        while (t.getCause() != null && (t instanceof java.lang.reflect.InvocationTargetException
            || t instanceof ExceptionInInitializerError)) {
            t = t.getCause();
        }
        return t;
    }

    /** All {@code <projectResolve implementation="..."/>} registrations in plugin descriptors on the classpath. */
    static List<String> resolverClassNames() throws IOException {
        Set<String> names = new LinkedHashSet<>();
        String cp = System.getProperty("java.class.path");
        for (String entry : cp.split(java.io.File.pathSeparator)) {
            Path path = Path.of(entry);
            if (!entry.endsWith(".jar") || !Files.isRegularFile(path)) {
                continue;
            }
            try (JarFile jar = new JarFile(path.toFile())) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry e = entries.nextElement();
                    String n = e.getName();
                    if (!n.endsWith(".xml") || n.contains("/") && !n.startsWith("META-INF/")) {
                        continue;
                    }
                    try (InputStream in = jar.getInputStream(e)) {
                        String xml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                        if (!xml.contains("projectResolve")) {
                            continue;
                        }
                        Matcher m = PROJECT_RESOLVE.matcher(xml);
                        while (m.find()) {
                            names.add(m.group(1));
                        }
                    }
                }
            }
        }
        return new ArrayList<>(names);
    }
}
