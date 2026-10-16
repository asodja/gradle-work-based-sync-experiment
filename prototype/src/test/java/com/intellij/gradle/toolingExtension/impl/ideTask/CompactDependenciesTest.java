package com.intellij.gradle.toolingExtension.impl.ideTask;

import com.intellij.gradle.toolingExtension.impl.model.sourceSetDependencyModel.DefaultGradleSourceSetDependencyModel;
import org.jetbrains.plugins.gradle.model.DefaultExternalLibraryDependency;
import org.jetbrains.plugins.gradle.model.DefaultExternalProjectDependency;
import org.jetbrains.plugins.gradle.model.DefaultFileCollectionDependency;
import org.jetbrains.plugins.gradle.model.DefaultUnresolvedExternalDependency;
import org.jetbrains.plugins.gradle.model.ExternalDependency;
import org.jetbrains.plugins.gradle.model.ExternalLibraryDependency;
import org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CompactDependenciesTest {

    @Test
    public void roundTripKeepsContentAndOrder() throws Exception {
        GradleSourceSetDependencyModel model = sample();
        CompactDependencies compact = CompactDependencies.of(model);
        GradleSourceSetDependencyModel expanded = CompactDependencies.fromBytes(compact.toBytes()).expand();

        assertEquals(ModelText.canonical(model), ModelText.canonical(expanded));
        assertEquals(ModelText.fingerprint(model), ModelText.fingerprint(expanded));
        // order is not part of the canonical text; the consumer iterates the collections as the builder filled them
        assertEquals(new ArrayList<>(model.getDependencies().keySet()), new ArrayList<>(expanded.getDependencies().keySet()));
        List<String> names = new ArrayList<>();
        for (ExternalDependency d : expanded.getDependencies().get("main")) {
            names.add(d.getName());
        }
        assertEquals(Arrays.asList("guava", "core", "files", "missing"), names);
        ExternalDependency first = expanded.getDependencies().get("main").iterator().next();
        assertTrue(first instanceof ExternalLibraryDependency);
        assertNull(((ExternalLibraryDependency) first).getJavadoc());
        assertEquals(-1, expanded.getDependencies().get("test").iterator().next().getClasspathOrder());
        assertNull(expanded.getDependencies().get("empty").iterator().hasNext() ? "not empty" : null);
    }

    @Test
    public void payloadRoundTrip() throws Exception {
        GradleSourceSetDependencyModel model = sample();
        ModelPayload payload = new ModelPayload("x", ModelPayload.COMPACT_DEPENDENCIES, "fp", CompactDependencies.of(model).toBytes());
        Object read = payload.readModel(getClass().getClassLoader());
        assertEquals(ModelText.canonical(model), ModelText.canonical(read));
    }

    @Test
    public void sharesStringsAcrossRecords() {
        CompactDependencies compact = CompactDependencies.of(sample());
        // the same jar in main and test is one string
        assertTrue(compact.stringCount() < 40);
        assertTrue(compact.recordBytes() < 200);
    }

    private static GradleSourceSetDependencyModel sample() {
        DefaultExternalLibraryDependency guava = new DefaultExternalLibraryDependency();
        guava.setGroup("com.google.guava");
        guava.setName("guava");
        guava.setVersion("33.0");
        guava.setPackaging("jar");
        guava.setScope("COMPILE");
        guava.setClasspathOrder(1);
        guava.setFile(new File("/repo/guava-33.0.jar"));
        guava.setSource(new File("/repo/guava-33.0-sources.jar"));
        guava.setDependencies(new ArrayList<>());

        DefaultExternalProjectDependency core = new DefaultExternalProjectDependency();
        core.setGroup("org.example");
        core.setName("core");
        core.setVersion("1.0");
        core.setPackaging("jar");
        core.setClassifier("apiElements");
        core.setScope("COMPILE");
        core.setClasspathOrder(2);
        core.setExported(true);
        core.setProjectPath(":core");
        core.setConfigurationName("apiElements");
        core.setProjectDependencyArtifacts(new ArrayList<>(Arrays.asList(new File("/w/core/build/libs/core.jar"))));
        core.setProjectDependencyArtifactsSources(new ArrayList<>(Arrays.asList(new File("/w/core/src/main/java"), new File("/w/core/src/main/kotlin"))));
        core.setDependencies(new ArrayList<>(Arrays.asList(guava)));

        DefaultFileCollectionDependency files = new DefaultFileCollectionDependency(new ArrayList<>(Arrays.asList(new File("/w/libs/a.jar"), new File("/w/libs/b.jar"))));
        files.setName("files");
        files.setScope("RUNTIME");
        files.setClasspathOrder(3);
        files.setExcludedFromIndexing(true);
        files.setDependencies(new ArrayList<>());

        DefaultUnresolvedExternalDependency missing = new DefaultUnresolvedExternalDependency();
        missing.setGroup("org.nowhere");
        missing.setName("missing");
        missing.setVersion("0.1");
        missing.setScope("COMPILE");
        missing.setClasspathOrder(4);
        missing.setSelectionReason("requested");
        missing.setFailureMessage("Could not find org.nowhere:missing:0.1");
        missing.setDependencies(new ArrayList<>());

        DefaultExternalLibraryDependency guavaTest = new DefaultExternalLibraryDependency();
        guavaTest.setGroup("com.google.guava");
        guavaTest.setName("guava");
        guavaTest.setVersion("33.0");
        guavaTest.setPackaging("jar");
        guavaTest.setScope("TEST");
        guavaTest.setClasspathOrder(-1);
        guavaTest.setFile(new File("/repo/guava-33.0.jar"));
        guavaTest.setSource(new File("/repo/guava-33.0-sources.jar"));
        guavaTest.setJavadoc(new File("/repo/guava-33.0-javadoc.jar"));

        Map<String, Collection<ExternalDependency>> bySourceSet = new LinkedHashMap<>();
        bySourceSet.put("main", new ArrayList<>(Arrays.asList(guava, core, files, missing)));
        bySourceSet.put("test", new ArrayList<>(Arrays.asList(guavaTest)));
        bySourceSet.put("empty", new ArrayList<>());
        DefaultGradleSourceSetDependencyModel model = new DefaultGradleSourceSetDependencyModel();
        model.setDependencies(bySourceSet);
        return model;
    }
}
