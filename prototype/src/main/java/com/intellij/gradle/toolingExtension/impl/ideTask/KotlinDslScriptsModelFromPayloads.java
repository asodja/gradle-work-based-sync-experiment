package com.intellij.gradle.toolingExtension.impl.ideTask;

import org.gradle.tooling.model.kotlin.dsl.EditorPosition;
import org.gradle.tooling.model.kotlin.dsl.EditorReport;
import org.gradle.tooling.model.kotlin.dsl.EditorReportSeverity;
import org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptModel;
import org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The {@code KotlinDslScriptsModel} of one build assembled from its projects' {@link KotlinDslScriptPayload}s (D17). */
public final class KotlinDslScriptsModelFromPayloads {

    private KotlinDslScriptsModelFromPayloads() {
    }

    public static KotlinDslScriptsModel assemble(List<KotlinDslScriptPayload> payloads) {
        Map<File, KotlinDslScriptModel> models = new LinkedHashMap<>();
        for (KotlinDslScriptPayload payload : payloads) {
            for (KotlinDslScriptPayload.Script script : payload.scripts) {
                List<EditorReport> reports = new ArrayList<>();
                for (String text : script.editorReports) {
                    reports.add(parseReport(text));
                }
                models.put(script.scriptFile, new Script(new ArrayList<>(script.classPath), new ArrayList<>(script.sourcePath),
                    new ArrayList<>(script.implicitImports), reports, new ArrayList<>(script.exceptions)));
            }
        }
        return new Model(models);
    }

    /** "SEVERITY message @line:column", as GradleOwnedModels writes it. */
    private static EditorReport parseReport(String text) {
        int space = text.indexOf(' ');
        EditorReportSeverity severity = EditorReportSeverity.valueOf(text.substring(0, space));
        String rest = text.substring(space + 1);
        Position position = null;
        int at = rest.lastIndexOf(" @");
        if (at >= 0 && rest.substring(at + 2).matches("\\d+:\\d+")) {
            String[] parts = rest.substring(at + 2).split(":");
            position = new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            rest = rest.substring(0, at);
        }
        return new Report(severity, rest, position);
    }

    record Model(Map<File, KotlinDslScriptModel> scriptModels) implements KotlinDslScriptsModel, Serializable {
        @Override public Map<File, KotlinDslScriptModel> getScriptModels() { return scriptModels; }
    }

    record Script(List<File> classPath, List<File> sourcePath, List<String> implicitImports, List<EditorReport> editorReports,
                  List<String> exceptions) implements KotlinDslScriptModel, Serializable {
        @Override public List<File> getClassPath() { return classPath; }
        @Override public List<File> getSourcePath() { return sourcePath; }
        @Override public List<String> getImplicitImports() { return implicitImports; }
        @Override public List<EditorReport> getEditorReports() { return editorReports; }
        @Override public List<String> getExceptions() { return exceptions; }
    }

    record Report(EditorReportSeverity severity, String message, EditorPosition position) implements EditorReport, Serializable {
        @Override public EditorReportSeverity getSeverity() { return severity; }
        @Override public String getMessage() { return message; }
        @Override public EditorPosition getPosition() { return position; }
    }

    record Position(int line, int column) implements EditorPosition, Serializable {
        @Override public int getLine() { return line; }
        @Override public int getColumn() { return column; }
    }
}
