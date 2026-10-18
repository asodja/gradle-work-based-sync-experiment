package wbs.harness;

import com.intellij.gradle.toolingExtension.impl.ideTask.payload.IdeaModulePayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.KotlinDslScriptPayload;
import org.gradle.tooling.model.idea.IdeaCompilerOutput;
import org.gradle.tooling.model.idea.IdeaContentRoot;
import org.gradle.tooling.model.idea.IdeaJavaLanguageSettings;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;
import org.gradle.tooling.model.idea.IdeaSourceDirectory;
import org.gradle.tooling.model.kotlin.dsl.EditorReport;
import org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptModel;
import org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

/**
 * Writes the Gradle-owned build models and their per-project task substitutes (D14) as the same text, one file per
 * module / script under {@code <dump>/gradle-owned/}, so {@code prototype/compare-gradle-owned.sh} can diff them.
 */
final class GradleOwnedText {

    static final String IDEA_PROJECT = "org.gradle.tooling.model.idea.IdeaProject";
    static final String KOTLIN_DSL_SCRIPTS = "org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel";

    private GradleOwnedText() {
    }

    /** IntelliJ's model ids are "SimpleName<hash>". */
    static boolean handles(String classId) {
        return classId.startsWith("IdeaProject") || classId.startsWith("KotlinDslScriptsModel")
            || classId.startsWith("IdeaModulePayload") || classId.startsWith("KotlinDslScriptPayload");
    }

    private static final Map<String, java.util.List<IdeaModulePayload>> MODULES_BY_BUILD = new java.util.LinkedHashMap<>();

    /** The idea plugin's project-level values are derived from all modules of the build; written once all arrived. */
    static synchronized void finish(File dumpDir) throws IOException {
        for (Map.Entry<String, java.util.List<IdeaModulePayload>> e : MODULES_BY_BUILD.entrySet()) {
            String buildId = e.getKey();
            String maxSource = null;
            String maxTarget = null;
            IdeaModulePayload root = null;
            for (IdeaModulePayload p : e.getValue()) {
                maxSource = max(maxSource, p.sourceCompatibility);
                maxTarget = max(maxTarget, p.targetCompatibility);
                if (p.ideaProjectName != null) {
                    root = p;
                }
            }
            // IdeaPlugin: getMaxJavaModuleCompatibilityVersionFor, default 1.6 without java projects
            if (maxSource == null) {
                maxSource = "1.6";
            }
            if (maxTarget == null) {
                maxTarget = "1.6";
            }
            if (root != null) {
                writeFile(dumpDir, "ideaModule/" + buildId + "/_project.txt", "name " + root.ideaProjectName + "\njdkName " + root.currentJdkVersion
                    + "\nlanguageLevel " + maxSource + "\ntargetBytecodeVersion " + maxTarget
                    + "\njdkVersion " + root.currentJdkVersion + "\njdkHome " + root.currentJdkHome + "\n");
            }
            for (IdeaModulePayload p : e.getValue()) {
                writeFile(dumpDir, "ideaModule/" + buildId + "/" + sanitize(p.projectPath) + ".txt", moduleText(p, maxSource, maxTarget));
            }
        }
        MODULES_BY_BUILD.clear();
    }

    private static String max(String a, String b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return org.gradle.api.JavaVersion.toVersion(a).compareTo(org.gradle.api.JavaVersion.toVersion(b)) >= 0 ? a : b;
    }

    static void write(File dumpDir, String classId, String buildId, Object value) throws Exception {
        try {
            writeOrFail(dumpDir, classId, buildId, value);
        } catch (Exception e) {
            // e.g. IntelliJ's own IdeaProject Ion reader failing on a build's document; the comparison then lacks
            // that build, which the error file records.
            String kind = classId.startsWith("IdeaProject") || classId.startsWith("IdeaModulePayload") ? "ideaModule" : "kotlinDslScript";
            writeFile(dumpDir, kind + "/" + buildId + "/_error.txt", classId + ": " + e + "\n");
        }
    }

    private static void writeOrFail(File dumpDir, String classId, String buildId, Object value) throws Exception {
        if (value instanceof byte[]) {
            byte[] bytes = (byte[]) value;
            boolean ion = bytes.length > 4 && (bytes[0] & 0xff) == 0xE0 && bytes[1] == 1 && bytes[2] == 0 && (bytes[3] & 0xff) == 0xEA;
            if (ion) {
                // IntelliJ has Ion serialization services for both Gradle-owned models.
                Class<?> modelClass = classId.startsWith("IdeaProject") ? IdeaProject.class : KotlinDslScriptsModel.class;
                value = new com.intellij.gradle.toolingExtension.impl.modelSerialization.ToolingSerializer().read(bytes, modelClass);
            } else {
                value = JavaSerialized.read(bytes);
            }
        }
        if (value instanceof IdeaProject) {
            IdeaProject project = (IdeaProject) value;
            writeFile(dumpDir, "ideaModule/" + buildId + "/_project.txt", projectText(project.getName(), project.getJdkName(),
                project.getJavaLanguageSettings()));
            for (IdeaModule module : project.getModules()) {
                writeFile(dumpDir, "ideaModule/" + buildId + "/" + sanitize(module.getGradleProject().getPath()) + ".txt", moduleText(module));
            }
        } else if (value instanceof IdeaModulePayload) {
            synchronized (GradleOwnedText.class) {
                MODULES_BY_BUILD.computeIfAbsent(buildId, k -> new java.util.ArrayList<>()).add((IdeaModulePayload) value);
            }
        } else if (value instanceof KotlinDslScriptsModel) {
            for (Map.Entry<File, KotlinDslScriptModel> e : ((KotlinDslScriptsModel) value).getScriptModels().entrySet()) {
                KotlinDslScriptModel m = e.getValue();
                StringBuilder reports = new StringBuilder();
                for (EditorReport r : m.getEditorReports()) {
                    reports.append("  ").append(r.getSeverity()).append(' ').append(r.getMessage());
                    if (r.getPosition() != null) {
                        reports.append(" @").append(r.getPosition().getLine()).append(':').append(r.getPosition().getColumn());
                    }
                    reports.append('\n');
                }
                writeFile(dumpDir, "kotlinDslScript/" + buildId + "/" + sanitize(e.getKey().getPath()) + ".txt",
                    scriptText(m.getClassPath(), m.getSourcePath(), m.getImplicitImports(), reports.toString(), m.getExceptions()));
            }
        } else if (value instanceof KotlinDslScriptPayload) {
            for (KotlinDslScriptPayload.Script script : ((KotlinDslScriptPayload) value).scripts) {
                StringBuilder reports = new StringBuilder();
                for (String r : script.editorReports) {
                    reports.append("  ").append(r).append('\n');
                }
                writeFile(dumpDir, "kotlinDslScript/" + buildId + "/" + sanitize(script.scriptFile.getCanonicalPath()) + ".txt",
                    scriptText(script.classPath, script.sourcePath, script.implicitImports, reports.toString(), script.exceptions));
            }
        }
    }

    private static String projectText(String name, String jdkName, IdeaJavaLanguageSettings settings) {
        return "name " + name + "\njdkName " + jdkName + "\nlanguageLevel " + (settings == null ? null : settings.getLanguageLevel())
            + "\ntargetBytecodeVersion " + (settings == null ? null : settings.getTargetBytecodeVersion())
            + "\njdkVersion " + (settings == null || settings.getJdk() == null ? null : settings.getJdk().getJavaVersion())
            + "\njdkHome " + (settings == null || settings.getJdk() == null ? null : settings.getJdk().getJavaHome()) + "\n";
    }

    private static String moduleText(IdeaModule module) {
        StringBuilder out = new StringBuilder();
        out.append("module ").append(module.getName()).append('\n');
        out.append("path ").append(module.getGradleProject().getPath()).append('\n');
        out.append("name ").append(module.getGradleProject().getName()).append('\n');
        out.append("description ").append(module.getGradleProject().getDescription()).append('\n');
        out.append("buildRootDir ").append(module.getGradleProject().getProjectIdentifier().getBuildIdentifier().getRootDir()).append('\n');
        out.append("projectDir ").append(module.getGradleProject().getProjectDirectory()).append('\n');
        out.append("buildDir ").append(module.getGradleProject().getBuildDirectory()).append('\n');
        out.append("buildScript ").append(module.getGradleProject().getBuildScript().getSourceFile()).append('\n');
        out.append("jdkName ").append(module.getJdkName()).append('\n');
        IdeaJavaLanguageSettings java = module.getJavaLanguageSettings();
        out.append("sourceLanguageLevel ").append(java == null ? null : java.getLanguageLevel()).append('\n');
        out.append("targetBytecodeVersion ").append(java == null ? null : java.getTargetBytecodeVersion()).append('\n');
        for (IdeaContentRoot root : module.getContentRoots()) {
            out.append("contentRoot ").append(root.getRootDirectory()).append('\n');
            dirs(out, "source", root.getSourceDirectories());
            dirs(out, "test", root.getTestDirectories());
            dirs(out, "resource", root.getResourceDirectories());
            dirs(out, "testResource", root.getTestResourceDirectories());
            for (String f : new java.util.TreeSet<>(root.getExcludeDirectories().stream().map(File::toString).toList())) {
                out.append("exclude ").append(f).append('\n');
            }
        }
        IdeaCompilerOutput compiler = module.getCompilerOutput();
        out.append("inheritOutputDirs ").append(compiler.getInheritOutputDirs()).append('\n');
        out.append("outputDir ").append(compiler.getOutputDir()).append('\n');
        out.append("testOutputDir ").append(compiler.getTestOutputDir()).append('\n');
        return out.toString();
    }

    private static void dirs(StringBuilder out, String kind, Collection<? extends IdeaSourceDirectory> dirs) {
        java.util.TreeSet<String> lines = new java.util.TreeSet<>();
        for (IdeaSourceDirectory d : dirs) {
            lines.add(kind + " " + d.getDirectory() + " generated=" + d.isGenerated());
        }
        for (String line : lines) {
            out.append(line).append('\n');
        }
    }

    private static String moduleText(IdeaModulePayload p, String projectSource, String projectTarget) {
        StringBuilder out = new StringBuilder();
        out.append("module ").append(p.moduleName).append('\n');
        out.append("path ").append(p.projectPath).append('\n');
        out.append("name ").append(p.projectName).append('\n');
        out.append("description ").append(p.description).append('\n');
        out.append("buildRootDir ").append(p.buildRootDir).append('\n');
        out.append("projectDir ").append(p.projectDir).append('\n');
        out.append("buildDir ").append(p.buildDir).append('\n');
        out.append("buildScript ").append(p.buildScript).append('\n');
        out.append("jdkName ").append(p.jdkName).append('\n');
        // IdeaPlugin.includeModule*Override: the module carries a level only when it differs from the project's
        out.append("sourceLanguageLevel ").append(p.sourceCompatibility == null ? null
            : p.sourceCompatibility.equals(projectSource) ? null : p.sourceCompatibility).append('\n');
        out.append("targetBytecodeVersion ").append(p.targetCompatibility == null ? null
            : p.targetCompatibility.equals(projectTarget) ? null : p.targetCompatibility).append('\n');
        out.append("contentRoot ").append(p.contentRoot).append('\n');
        dirs(out, "source", p.sourceDirs);
        dirs(out, "test", p.testDirs);
        dirs(out, "resource", p.resourceDirs);
        dirs(out, "testResource", p.testResourceDirs);
        for (String f : new java.util.TreeSet<>(p.excludeDirs.stream().map(File::toString).toList())) {
            out.append("exclude ").append(f).append('\n');
        }
        out.append("inheritOutputDirs ").append(p.inheritOutputDirs).append('\n');
        out.append("outputDir ").append(p.outputDir).append('\n');
        out.append("testOutputDir ").append(p.testOutputDir).append('\n');
        return out.toString();
    }

    private static void dirs(StringBuilder out, String kind, Map<File, Boolean> dirs) {
        java.util.TreeSet<String> lines = new java.util.TreeSet<>();
        for (Map.Entry<File, Boolean> d : dirs.entrySet()) {
            lines.add(kind + " " + d.getKey() + " generated=" + d.getValue());
        }
        for (String line : lines) {
            out.append(line).append('\n');
        }
    }

    private static String scriptText(Collection<File> classPath, Collection<File> sourcePath, Collection<String> imports, String reports,
                                     Collection<String> exceptions) {
        StringBuilder out = new StringBuilder();
        out.append("classPath\n");
        for (File f : classPath) {
            out.append("  ").append(f).append('\n');
        }
        out.append("sourcePath\n");
        for (File f : sourcePath) {
            out.append("  ").append(f).append('\n');
        }
        out.append("implicitImports\n");
        for (String s : imports) {
            out.append("  ").append(s).append('\n');
        }
        out.append("editorReports\n").append(reports);
        out.append("exceptions ").append(exceptions.size()).append('\n');
        for (String e : exceptions) {
            out.append("  ").append(e.lines().findFirst().orElse("")).append('\n');
        }
        return out.toString();
    }

    private static void writeFile(File dumpDir, String relative, String text) throws IOException {
        File file = new File(dumpDir, "gradle-owned/" + relative);
        file.getParentFile().mkdirs();
        Files.writeString(file.toPath(), text, StandardCharsets.UTF_8);
    }

    private static String sanitize(String s) {
        return s.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
