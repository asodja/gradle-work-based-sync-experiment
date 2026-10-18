package com.intellij.gradle.toolingExtension.impl.ideTask.payload;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Per-project substitute for the module part of Gradle's {@code IdeaProject} (D14): what IntelliJ reads from an
 * {@code IdeaModule} and its {@code GradleProject}, as plain values. The root project's payload also carries the
 * project-level part ({@code IdeaProject} name, jdk, language settings). Dependencies are not included: IntelliJ
 * resolves them from the source set models ({@code idea.resolveSourceSetDependencies=true}).
 */
public final class IdeaModulePayload implements Serializable {
    private static final long serialVersionUID = 1L;

    /** true when the project applied the idea plugin itself and the module state below is the plugin's */
    public boolean ideaPluginApplied;
    public String moduleName;
    public String projectPath;
    public String projectName;
    public String description;
    public File buildRootDir;
    public File projectDir;
    public File buildDir;
    public File buildScript;
    public String jdkName;
    /** JavaPluginExtension source/target compatibility; null without the java plugin. The build-level IdeaProject
     *  values (the maximum over all modules) and the per-module overrides are derived by the consumer, as the idea
     *  plugin derives them from the root project today. */
    public String sourceCompatibility;
    public String targetCompatibility;
    public File contentRoot;
    /** directory -> generated flag, in the idea model's order */
    public Map<File, Boolean> sourceDirs = new LinkedHashMap<>();
    public Map<File, Boolean> testDirs = new LinkedHashMap<>();
    public Map<File, Boolean> resourceDirs = new LinkedHashMap<>();
    public Map<File, Boolean> testResourceDirs = new LinkedHashMap<>();
    public List<File> excludeDirs;
    public boolean inheritOutputDirs;
    public File outputDir;
    public File testOutputDir;

    /** Root project of a build only. */
    public String ideaProjectName;
    public String currentJdkVersion;
    public File currentJdkHome;
}
