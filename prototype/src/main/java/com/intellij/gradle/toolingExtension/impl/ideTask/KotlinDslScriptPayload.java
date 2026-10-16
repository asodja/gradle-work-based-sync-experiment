package com.intellij.gradle.toolingExtension.impl.ideTask;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Per-project substitute for the entries of Gradle's {@code KotlinDslScriptsModel} that belong to one project (D14):
 * its build script and its precompiled script plugins; for the root project also the settings script and the init
 * scripts of the invocation. Each entry is built by Gradle's single-script builder at capture time.
 */
public final class KotlinDslScriptPayload implements Serializable {
    private static final long serialVersionUID = 2L;

    public List<Script> scripts = new ArrayList<>();

    public static final class Script implements Serializable {
        private static final long serialVersionUID = 1L;

        public File scriptFile;
        public List<File> classPath = new ArrayList<>();
        public List<File> sourcePath = new ArrayList<>();
        public List<String> implicitImports = new ArrayList<>();
        /** "SEVERITY message @line:column" per editor report */
        public List<String> editorReports = new ArrayList<>();
        public List<String> exceptions = new ArrayList<>();
    }
}
