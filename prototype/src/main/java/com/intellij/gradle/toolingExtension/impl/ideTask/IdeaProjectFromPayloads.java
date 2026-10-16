package com.intellij.gradle.toolingExtension.impl.ideTask;

import org.gradle.api.JavaVersion;
import org.gradle.tooling.model.BuildIdentifier;
import org.gradle.tooling.model.DomainObjectSet;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.GradleTask;
import org.gradle.tooling.model.ProjectIdentifier;
import org.gradle.tooling.model.gradle.GradleScript;
import org.gradle.tooling.model.idea.IdeaCompilerOutput;
import org.gradle.tooling.model.idea.IdeaContentRoot;
import org.gradle.tooling.model.idea.IdeaDependency;
import org.gradle.tooling.model.idea.IdeaJavaLanguageSettings;
import org.gradle.tooling.model.idea.IdeaLanguageLevel;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;
import org.gradle.tooling.model.idea.IdeaSourceDirectory;
import org.gradle.tooling.model.internal.ImmutableDomainObjectSet;
import org.gradle.tooling.model.java.InstalledJdk;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The {@code IdeaProject} of one build assembled from its projects' {@link IdeaModulePayload}s, the way Gradle's
 * {@code IdeaModelBuilder} and the idea plugin derive it (D17, design/m5-ide-consumer.md): project language level
 * and bytecode version are the maximum over the modules, a module carries its own only where it differs; module
 * dependencies are empty (the IDE reads them from the source set models in the default mode); the Gradle project
 * tree carries no tasks (the IDE reads {@code GradleTaskModel}).
 */
public final class IdeaProjectFromPayloads {

    private IdeaProjectFromPayloads() {
    }

    /** @param buildTreePathPrefix "" for the root build, ":&lt;included build name&gt;" for an included build */
    public static IdeaProject assemble(List<IdeaModulePayload> payloadsInAnyOrder, String buildTreePathPrefix) {
        // Gradle's project order (depth, then path), the order IdeaModelBuilder emits modules in and the IDE
        // processes them: the first module that brings a library fixes the project-level library's roots.
        List<IdeaModulePayload> payloads = new ArrayList<>(payloadsInAnyOrder);
        payloads.sort((a, b) -> {
            int depth = Integer.compare(a.projectPath.split(":").length, b.projectPath.split(":").length);
            return depth != 0 ? depth : a.projectPath.compareTo(b.projectPath);
        });
        IdeaModulePayload root = null;
        String maxSource = null;
        String maxTarget = null;
        for (IdeaModulePayload p : payloads) {
            if (p.ideaProjectName != null) {
                root = p;
            }
            maxSource = max(maxSource, p.sourceCompatibility);
            maxTarget = max(maxTarget, p.targetCompatibility);
        }
        if (root == null) {
            throw new IllegalStateException("no root project payload among " + payloads.size() + " modules");
        }
        if (maxSource == null) {
            maxSource = "1.6";
        }
        if (maxTarget == null) {
            maxTarget = "1.6";
        }
        Map<String, Project> projectsByPath = new LinkedHashMap<>();
        for (IdeaModulePayload p : payloads) {
            projectsByPath.put(p.projectPath, new Project(p, root.buildRootDir, buildTreePathPrefix));
        }
        for (Project project : projectsByPath.values()) {
            String path = project.path;
            if (!path.equals(":")) {
                int cut = path.lastIndexOf(':');
                Project parent = projectsByPath.get(cut == 0 ? ":" : path.substring(0, cut));
                if (parent != null) {
                    project.parent = parent;
                    parent.children.add(project);
                }
            }
        }
        List<IdeaModule> modules = new ArrayList<>();
        Model model = new Model();
        for (IdeaModulePayload p : payloads) {
            modules.add(new Module(model, p, projectsByPath.get(p.projectPath), maxSource, maxTarget));
        }
        model.name = root.ideaProjectName;
        model.jdkName = root.currentJdkVersion;
        model.languageLevel = new Level("JDK_" + maxSource.replace('.', '_'));
        model.javaLanguageSettings = new JavaSettings(JavaVersion.toVersion(maxSource), JavaVersion.toVersion(maxTarget),
            new Jdk(JavaVersion.toVersion(root.currentJdkVersion), root.currentJdkHome));
        model.modules = ImmutableDomainObjectSet.of(modules);
        return model;
    }

    private static String max(String a, String b) {
        if (b == null) {
            return a;
        }
        if (a == null) {
            return b;
        }
        return JavaVersion.toVersion(a).compareTo(JavaVersion.toVersion(b)) >= 0 ? a : b;
    }

    static final class Model implements IdeaProject, Serializable {
        String name;
        String jdkName;
        IdeaLanguageLevel languageLevel;
        IdeaJavaLanguageSettings javaLanguageSettings;
        DomainObjectSet<? extends IdeaModule> modules;

        @Override public String getName() { return name; }
        @Override public String getDescription() { return null; }
        @Override public String getJdkName() { return jdkName; }
        @Override public IdeaLanguageLevel getLanguageLevel() { return languageLevel; }
        @Override public IdeaJavaLanguageSettings getJavaLanguageSettings() { return javaLanguageSettings; }
        @Override public DomainObjectSet<? extends IdeaModule> getModules() { return modules; }
        @Override public DomainObjectSet<? extends IdeaModule> getChildren() { return modules; }
        @Override public IdeaProject getParent() { return null; }
    }

    static final class Module implements IdeaModule, Serializable {
        private final Model project;
        private final String name;
        private final String description;
        private final String jdkName;
        private final IdeaJavaLanguageSettings javaLanguageSettings;
        private final GradleProject gradleProject;
        private final DomainObjectSet<IdeaContentRoot> contentRoots;
        private final IdeaCompilerOutput compilerOutput;

        Module(Model project, IdeaModulePayload p, Project gradleProject, String maxSource, String maxTarget) {
            this.project = project;
            this.name = p.moduleName;
            this.description = p.description;
            this.jdkName = p.jdkName;
            this.gradleProject = gradleProject;
            boolean differs = p.sourceCompatibility != null
                && (!p.sourceCompatibility.equals(maxSource) || !p.targetCompatibility.equals(maxTarget));
            this.javaLanguageSettings = p.sourceCompatibility == null ? null : new JavaSettings(
                p.sourceCompatibility.equals(maxSource) ? null : JavaVersion.toVersion(p.sourceCompatibility),
                p.targetCompatibility.equals(maxTarget) ? null : JavaVersion.toVersion(p.targetCompatibility), null);
            this.contentRoots = ImmutableDomainObjectSet.of(List.of(new ContentRoot(p)));
            this.compilerOutput = new CompilerOutput(p.inheritOutputDirs, p.outputDir, p.testOutputDir);
        }

        @Override public String getName() { return name; }
        @Override public String getDescription() { return description; }
        @Override public String getJdkName() { return jdkName; }
        @Override public IdeaJavaLanguageSettings getJavaLanguageSettings() { return javaLanguageSettings; }
        @Override public GradleProject getGradleProject() { return gradleProject; }
        @Override public ProjectIdentifier getProjectIdentifier() { return gradleProject.getProjectIdentifier(); }
        @Override public IdeaProject getParent() { return project; }
        @Override public IdeaProject getProject() { return project; }
        @Override public DomainObjectSet<? extends IdeaContentRoot> getContentRoots() { return contentRoots; }
        @Override public IdeaCompilerOutput getCompilerOutput() { return compilerOutput; }
        @Override public DomainObjectSet<? extends IdeaDependency> getDependencies() { return ImmutableDomainObjectSet.of(List.of()); }
        @Override public DomainObjectSet<? extends org.gradle.tooling.model.HierarchicalElement> getChildren() { return ImmutableDomainObjectSet.of(List.of()); }
    }

    static final class ContentRoot implements IdeaContentRoot, Serializable {
        private final File rootDirectory;
        private final DomainObjectSet<IdeaSourceDirectory> sourceDirectories;
        private final DomainObjectSet<IdeaSourceDirectory> testDirectories;
        private final DomainObjectSet<IdeaSourceDirectory> resourceDirectories;
        private final DomainObjectSet<IdeaSourceDirectory> testResourceDirectories;
        private final Set<File> excludeDirectories;

        ContentRoot(IdeaModulePayload p) {
            rootDirectory = p.contentRoot;
            sourceDirectories = dirs(p.sourceDirs);
            testDirectories = dirs(p.testDirs);
            resourceDirectories = dirs(p.resourceDirs);
            testResourceDirectories = dirs(p.testResourceDirs);
            excludeDirectories = new LinkedHashSet<>(p.excludeDirs);
        }

        private static DomainObjectSet<IdeaSourceDirectory> dirs(Map<File, Boolean> dirs) {
            List<IdeaSourceDirectory> out = new ArrayList<>();
            for (Map.Entry<File, Boolean> e : dirs.entrySet()) {
                out.add(new SourceDirectory(e.getKey(), e.getValue()));
            }
            return ImmutableDomainObjectSet.of(out);
        }

        @Override public File getRootDirectory() { return rootDirectory; }
        @Override public DomainObjectSet<? extends IdeaSourceDirectory> getSourceDirectories() { return sourceDirectories; }
        @Override public DomainObjectSet<? extends IdeaSourceDirectory> getTestDirectories() { return testDirectories; }
        @Override public DomainObjectSet<? extends IdeaSourceDirectory> getResourceDirectories() { return resourceDirectories; }
        @Override public DomainObjectSet<? extends IdeaSourceDirectory> getTestResourceDirectories() { return testResourceDirectories; }
        @Override public Set<File> getExcludeDirectories() { return excludeDirectories; }
    }

    record SourceDirectory(File directory, boolean generated) implements IdeaSourceDirectory, Serializable {
        @Override public File getDirectory() { return directory; }
        @Override public boolean isGenerated() { return generated; }
    }

    record CompilerOutput(boolean inheritOutputDirs, File outputDir, File testOutputDir) implements IdeaCompilerOutput, Serializable {
        @Override public boolean getInheritOutputDirs() { return inheritOutputDirs; }
        @Override public File getOutputDir() { return outputDir; }
        @Override public File getTestOutputDir() { return testOutputDir; }
    }

    record JavaSettings(JavaVersion languageLevel, JavaVersion targetBytecodeVersion, InstalledJdk jdk) implements IdeaJavaLanguageSettings, Serializable {
        @Override public JavaVersion getLanguageLevel() { return languageLevel; }
        @Override public JavaVersion getTargetBytecodeVersion() { return targetBytecodeVersion; }
        @Override public InstalledJdk getJdk() { return jdk; }
    }

    record Jdk(JavaVersion javaVersion, File javaHome) implements InstalledJdk, Serializable {
        @Override public JavaVersion getJavaVersion() { return javaVersion; }
        @Override public File getJavaHome() { return javaHome; }
    }

    record Level(String level) implements IdeaLanguageLevel, Serializable {
        @Override public String getLevel() { return level; }
    }

    static final class Project implements GradleProject, Serializable {
        final String path;
        private final String name;
        private final String description;
        private final File projectDirectory;
        private final File buildDirectory;
        private final GradleScript buildScript;
        private final ProjectIdentifier identifier;
        private final String buildTreePath;
        Project parent;
        final List<Project> children = new ArrayList<>();

        Project(IdeaModulePayload p, File buildRootDir, String buildTreePathPrefix) {
            path = p.projectPath;
            buildTreePath = buildTreePathPrefix + (p.projectPath.equals(":") ? (buildTreePathPrefix.isEmpty() ? ":" : "") : p.projectPath);
            name = p.projectName;
            description = p.description;
            projectDirectory = p.projectDir;
            buildDirectory = p.buildDir;
            buildScript = new Script(p.buildScript);
            identifier = new Identifier(p.projectPath, new Build(buildRootDir));
        }

        @Override public String getName() { return name; }
        @Override public String getDescription() { return description; }
        @Override public String getPath() { return path; }
        @Override public File getProjectDirectory() { return projectDirectory; }
        @Override public File getBuildDirectory() { return buildDirectory; }
        @Override public GradleScript getBuildScript() { return buildScript; }
        @Override public ProjectIdentifier getProjectIdentifier() { return identifier; }
        @Override public String getBuildTreePath() { return buildTreePath; }
        @Override public GradleProject getParent() { return parent; }
        @Override public DomainObjectSet<? extends GradleProject> getChildren() { return ImmutableDomainObjectSet.of(children); }
        @Override public DomainObjectSet<? extends GradleTask> getTasks() { return ImmutableDomainObjectSet.of(List.of()); }
        @Override public GradleProject findByPath(String path) {
            if (this.path.equals(path)) {
                return this;
            }
            for (Project child : children) {
                GradleProject found = child.findByPath(path);
                if (found != null) {
                    return found;
                }
            }
            return null;
        }
    }

    record Script(File sourceFile) implements GradleScript, Serializable {
        @Override public File getSourceFile() { return sourceFile; }
    }

    record Identifier(String projectPath, BuildIdentifier buildIdentifier) implements ProjectIdentifier, Serializable {
        @Override public String getProjectPath() { return projectPath; }
        @Override public BuildIdentifier getBuildIdentifier() { return buildIdentifier; }
    }

    record Build(File rootDir) implements BuildIdentifier, Serializable {
        @Override public File getRootDir() { return rootDir; }
    }
}
