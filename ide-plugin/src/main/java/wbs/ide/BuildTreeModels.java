package wbs.ide;

import com.intellij.gradle.toolingExtension.impl.ideTask.BuildTreePayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.IdeBuildTreeTask;
import com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTasks;
import com.intellij.gradle.toolingExtension.impl.ideTask.ModelBundle;
import com.intellij.gradle.toolingExtension.impl.ideTask.ModelPayload;
import org.gradle.plugins.ide.internal.tooling.model.DefaultGradleBuild;
import org.gradle.tooling.internal.adapter.ProtocolToModelAdapter;
import org.gradle.tooling.internal.gradle.DefaultBuildIdentifier;
import org.gradle.tooling.internal.gradle.DefaultProjectIdentifier;
import org.gradle.tooling.model.gradle.GradleBuild;

import java.io.DataInputStream;
import java.io.File;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The build tree from the {@code ideBuildTree} bundles instead of the {@code GradleBuild} model (D29): one payload per
 * build, the root build's under the IDE's project directory, included builds' under the directories the parent names.
 * Produces the same {@link GradleBuild} view the Tooling API returns, through its own model POJOs and adapter, so the
 * resolver's light-build conversion needs no change.
 */
final class BuildTreeModels {

    private final String rootDir;
    private final Map<String, BuildTreePayload> trees = new LinkedHashMap<>();
    private final List<String> missing = new ArrayList<>();

    BuildTreeModels(File rootDir) {
        this.rootDir = rootDir.getAbsolutePath();
    }

    static File treeFile(File buildRootDir) {
        return new File(buildRootDir, "build/" + IdeModelTasks.MODEL_DIR + "/" + IdeBuildTreeTask.NAME + ".bin");
    }

    /** A tree task has (re)written its bundle: what was read before is stale. */
    synchronized void invalidate() {
        trees.clear();
    }

    /** Loads the tree bundles that exist now; true when the root's and every referenced build's are loaded. */
    synchronized boolean loadAll() {
        missing.clear();
        return load(rootDir, new HashSet<>());
    }

    private boolean load(String dir, Set<String> visiting) {
        if (!visiting.add(dir)) {
            return true;
        }
        BuildTreePayload tree = trees.get(dir);
        if (tree == null) {
            File file = treeFile(new File(dir));
            tree = file.isFile() ? read(file) : null;
            if (tree == null) {
                missing.add(file.getPath());
                return false;
            }
            trees.put(dir, tree);
        }
        boolean complete = true;
        for (BuildTreePayload.IncludedBuild included : tree.includedBuilds) {
            complete &= load(included.rootDir, visiting);
        }
        return complete;
    }

    /** Null while the file is still being written (the task run is in progress). */
    private static BuildTreePayload read(File file) {
        try (DataInputStream in = new DataInputStream(Files.newInputStream(file.toPath()))) {
            for (ModelPayload payload : ModelBundle.read(in)) {
                Object value = payload.readModel(BuildTreeModels.class.getClassLoader());
                if (value instanceof BuildTreePayload) {
                    return (BuildTreePayload) value;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    String missing() {
        return String.join(", ", missing);
    }

    int buildCount() {
        return trees.size();
    }

    /**
     * Project directory of a model task's project, from the task path ({@code :build-logic:x:ideProjectModel}: the
     * build's identity path plus the project path), or null while that build's tree is not loaded yet. The longest
     * identity path with a matching project wins, since an included build's path can also be a project path.
     */
    synchronized String projectDirOf(String taskPath) {
        loadAll();
        String path = taskPath.substring(0, taskPath.lastIndexOf(':'));
        if (path.isEmpty()) {
            path = ":";
        }
        String bestDir = null;
        int bestLength = -1;
        for (BuildTreePayload tree : trees.values()) {
            String identity = tree.identityPath;
            String projectPath;
            if (identity.equals(":")) {
                projectPath = path;
            } else if (path.equals(identity)) {
                projectPath = ":";
            } else if (path.startsWith(identity + ":")) {
                projectPath = path.substring(identity.length());
            } else {
                continue;
            }
            BuildTreePayload.Node node = find(tree.rootProject, projectPath);
            if (node != null && identity.length() > bestLength) {
                bestDir = node.projectDir;
                bestLength = identity.length();
            }
        }
        return bestDir;
    }

    private static BuildTreePayload.Node find(BuildTreePayload.Node node, String path) {
        if (node.path.equals(path)) {
            return node;
        }
        for (BuildTreePayload.Node child : node.children) {
            if (path.equals(child.path) || path.startsWith(child.path + ":")) {
                return find(child, path);
            }
        }
        return null;
    }

    /**
     * The {@code GradleDslBaseScriptModel} holder models from the root tree's payload: an empty map when this IDE has
     * no holder class (2025.3), null when the task did not capture the model (the IDE then requests it). The model is
     * a {@link Proxy} over the Tooling API interfaces, which the holder's serializer reads like the real proxy.
     */
    synchronized Map<com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelId, Object> dslBaseScriptModels() {
        Class<?> holderClass;
        Class<?> modelClass;
        try {
            holderClass = Class.forName("com.intellij.gradle.toolingExtension.impl.model.dslBaseScriptModel.GradleDslBaseScriptModelHolder");
            modelClass = Class.forName("org.gradle.tooling.model.dsl.GradleDslBaseScriptModel");
        } catch (ClassNotFoundException e) {
            return Map.of();
        }
        BuildTreePayload root = trees.get(rootDir);
        if (root == null || root.dslBaseScript == null) {
            return null;
        }
        try {
            BuildTreePayload.DslBaseScript d = root.dslBaseScript;
            Object kotlin = view("org.gradle.tooling.model.dsl.KotlinDslBaseScriptModel", Map.of(
                "getScriptTemplatesClassPath", files(d.kotlinScriptTemplatesClassPath), "getCompileClassPath", files(d.kotlinCompileClassPath),
                "getImplicitImports", new ArrayList<>(d.kotlinImplicitImports), "getTemplateClassNames", new ArrayList<>(d.kotlinTemplateClassNames)));
            Object groovy = view("org.gradle.tooling.model.dsl.GroovyDslBaseScriptModel", Map.of(
                "getCompileClassPath", files(d.groovyCompileClassPath), "getImplicitImports", new ArrayList<>(d.groovyImplicitImports)));
            Object model = view(modelClass.getName(), Map.of("getKotlinDslBaseScriptModel", kotlin, "getGroovyDslBaseScriptModel", groovy));
            com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelHolderState wrapped =
                (com.intellij.gradle.toolingExtension.impl.modelAction.GradleModelHolderState) holderClass.getMethod("wrap", modelClass).invoke(null, model);
            return new LinkedHashMap<>(wrapped.getModels());
        } catch (Exception e) {
            WbsLog.info("[wbs] GradleDslBaseScriptModel from the tree bundle failed: " + e);
            return null;
        }
    }

    private static List<File> files(List<String> paths) {
        List<File> result = new ArrayList<>();
        for (String path : paths) {
            result.add(new File(path));
        }
        return result;
    }

    private static Object view(String interfaceName, Map<String, Object> values) throws ClassNotFoundException {
        Class<?> type = Class.forName(interfaceName, true, BuildTreeModels.class.getClassLoader());
        return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, (proxy, method, args) -> {
            if (values.containsKey(method.getName())) {
                return values.get(method.getName());
            }
            switch (method.getName()) {
                case "toString": return interfaceName + values.keySet();
                case "hashCode": return System.identityHashCode(proxy);
                case "equals": return proxy == args[0];
                default: throw new UnsupportedOperationException(method.getName());
            }
        });
    }

    /** The root build as the Tooling API would hand it over; {@code getEditableBuilds()} reaches every nested build. */
    synchronized GradleBuild rootBuild() {
        DefaultGradleBuild pojo = pojo(trees.get(rootDir), new HashSet<>());
        return new ProtocolToModelAdapter().adapt(GradleBuild.class, pojo);
    }

    private DefaultGradleBuild pojo(BuildTreePayload tree, Set<String> visiting) {
        DefaultGradleBuild build = new DefaultGradleBuild();
        DefaultBuildIdentifier id = new DefaultBuildIdentifier(new File(tree.rootDir));
        build.setRootProject(project(tree.rootProject, null, id, build));
        // Gradle API 9.6 (IDE 2026.2) stores the identifier on the build; 9.0 (2025.3) derives it from the root project
        try {
            DefaultGradleBuild.class.getMethod("setBuildIdentifier", org.gradle.tooling.model.BuildIdentifier.class).invoke(build, id);
        } catch (NoSuchMethodException e) {
            // derived from the root project's identifier
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        visiting.add(tree.rootDir);
        List<DefaultGradleBuild> nested = new ArrayList<>();
        for (BuildTreePayload.IncludedBuild included : tree.includedBuilds) {
            BuildTreePayload child = trees.get(included.rootDir);
            if (child == null || visiting.contains(child.rootDir)) {
                continue;
            }
            DefaultGradleBuild childBuild = pojo(child, visiting);
            build.addIncludedBuild(childBuild);
            nested.add(childBuild);
        }
        build.addBuilds(nested);
        return build;
    }

    private static org.gradle.plugins.ide.internal.tooling.model.BasicGradleProject project(
        BuildTreePayload.Node node, org.gradle.plugins.ide.internal.tooling.model.BasicGradleProject parent, DefaultBuildIdentifier id, DefaultGradleBuild build) {
        org.gradle.plugins.ide.internal.tooling.model.BasicGradleProject project = new org.gradle.plugins.ide.internal.tooling.model.BasicGradleProject();
        project.setName(node.name);
        project.setProjectIdentifier(new DefaultProjectIdentifier(id, node.path));
        project.setProjectDirectory(new File(node.projectDir));
        project.setBuildTreePath(node.buildTreePath);
        if (parent != null) {
            project.setParent(parent);
            parent.addChild(project);
        }
        build.addProject(project);
        for (BuildTreePayload.Node child : node.children) {
            project(child, project, id, build);
        }
        return project;
    }
}
