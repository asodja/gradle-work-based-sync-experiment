package com.intellij.gradle.toolingExtension.impl.ideTask.payload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The structure of one build of the build tree, as Gradle's {@code GradleBuild} model carries it (D29): written by the
 * root project's {@code ideBuildTree} task from the settings' project descriptors, read by the IDE instead of
 * requesting the model. Included builds are referenced by name and root directory; each writes its own payload.
 */
public final class BuildTreePayload implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ":" for the root build, ":build-logic" for an included build (its path in the build tree). */
    public String identityPath;
    public String rootDir;
    public String gradleVersion;
    public Node rootProject;
    public List<IncludedBuild> includedBuilds = new ArrayList<>();
    /** Gradle's {@code GradleDslBaseScriptModel} (a function of the distribution), root build only; null if not captured. */
    public DslBaseScript dslBaseScript;

    public static final class DslBaseScript implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<String> kotlinScriptTemplatesClassPath = new ArrayList<>();
        public List<String> kotlinCompileClassPath = new ArrayList<>();
        public List<String> kotlinImplicitImports = new ArrayList<>();
        public List<String> kotlinTemplateClassNames = new ArrayList<>();
        public List<String> groovyCompileClassPath = new ArrayList<>();
        public List<String> groovyImplicitImports = new ArrayList<>();
    }

    public static final class Node implements Serializable {
        private static final long serialVersionUID = 1L;
        public String name;
        public String path;
        public String projectDir;
        public String buildTreePath;
        public List<Node> children = new ArrayList<>();
    }

    public static final class IncludedBuild implements Serializable {
        private static final long serialVersionUID = 1L;
        public String name;
        public String rootDir;
    }
}
