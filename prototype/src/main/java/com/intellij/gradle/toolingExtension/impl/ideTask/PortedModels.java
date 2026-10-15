package com.intellij.gradle.toolingExtension.impl.ideTask;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The models the task version produces instead of IntelliJ's builders, by phase group (DECISIONS D9). Shared with
 * the consumer (harness, later the IDE), which stops requesting these models from builders and reads them from
 * the bundles instead. Model class names, not classes: the request interface must stay loadable without them.
 */
public final class PortedModels {

    /** IntelliJ fetch phase name (GradleModelFetchPhase.getName()) -> model class names delivered in that phase. */
    public static final Map<String, List<String>> PROJECT_MODEL_TASK = projectModelTask();

    private PortedModels() {
    }

    private static Map<String, List<String>> projectModelTask() {
        Map<String, List<String>> m = new LinkedHashMap<>();
        m.put("SOURCE_SET_MODEL_PHASE", List.of(
            "org.jetbrains.plugins.gradle.model.GradleSourceSetModel"));
        return m;
    }

    public static List<String> all() {
        return PROJECT_MODEL_TASK.values().stream().flatMap(List::stream).toList();
    }
}
