package com.intellij.gradle.toolingExtension.impl.ideTask.payload;

import com.intellij.openapi.externalSystem.model.project.dependencies.ArtifactDependencyNodeImpl;
import com.intellij.openapi.externalSystem.model.project.dependencies.DependencyScopeNode;
import com.intellij.openapi.externalSystem.model.project.dependencies.ReferenceNode;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ModelFingerprintTest {
    @Test
    public void scriptClasspathOrderChangesFingerprint() {
        KotlinDslScriptPayload payload = new KotlinDslScriptPayload();
        KotlinDslScriptPayload.Script script = new KotlinDslScriptPayload.Script();
        payload.scripts.add(script);
        script.classPath = new ArrayList<>(List.of(new File("a.jar"), new File("b.jar")));
        String original = ModelText.fingerprint(payload);
        Collections.reverse(script.classPath);
        assertNotEquals(original, ModelText.fingerprint(payload));
        Collections.reverse(script.classPath);
        assertEquals(original, ModelText.fingerprint(payload));
        assertTrue(original.startsWith("v2:"));
    }

    @Test
    public void preservesOrderedSetsAndArrays() {
        assertNotEquals(ModelFingerprint.of(new LinkedHashSet<>(List.of("a", "b"))),
            ModelFingerprint.of(new LinkedHashSet<>(List.of("b", "a"))));
        assertNotEquals(ModelFingerprint.of(new int[]{1, 2}), ModelFingerprint.of(new int[]{2, 1}));
        assertNotEquals(ModelFingerprint.of(new String[]{"a", "b"}), ModelFingerprint.of(new String[]{"b", "a"}));
    }

    @Test
    public void mapOrderIsIgnoredButAssociationsArePreserved() {
        Map<String, Object> left = new LinkedHashMap<>();
        left.put("a", List.of(1, 2));
        left.put("b", List.of(3, 4));
        Map<String, Object> right = new LinkedHashMap<>();
        right.put("b", List.of(3, 4));
        right.put("a", List.of(1, 2));
        assertEquals(ModelFingerprint.of(left), ModelFingerprint.of(right));
        right.put("a", List.of(3, 4));
        right.put("b", List.of(1, 2));
        assertNotEquals(ModelFingerprint.of(left), ModelFingerprint.of(right));
    }

    @Test
    public void preservesTypesBoundariesAndOrdinaryIds() {
        assertNotEquals(ModelFingerprint.of(null), ModelFingerprint.of("null"));
        assertNotEquals(ModelFingerprint.of(1), ModelFingerprint.of("1"));
        assertNotEquals(ModelFingerprint.of(List.of("ab", "c")), ModelFingerprint.of(List.of("a", "bc")));
        assertNotEquals(ModelFingerprint.of(new Id(1)), ModelFingerprint.of(new Id(2)));
    }

    @Test
    public void renumberedGraphIdsKeepReferences() {
        assertEquals(ModelFingerprint.of(graph(10, 11)), ModelFingerprint.of(graph(100, 101)));
        assertNotEquals(ModelFingerprint.of(graph(10, 11)), ModelFingerprint.of(graph(10, 12)));
        assertNotEquals(ModelFingerprint.of(graph(10, 99)), ModelFingerprint.of(graph(10, 100)));
    }

    @Test
    public void forwardReferencesAndIndependentScopesAreNormalized() {
        DependencyScopeNode first = graph(10, 11);
        DependencyScopeNode shifted = graph(100, 101);
        Collections.rotate(first.getDependencies(), 1);
        Collections.rotate(shifted.getDependencies(), 1);
        assertEquals(ModelFingerprint.of(first), ModelFingerprint.of(shifted));
        assertEquals(ModelFingerprint.of(List.of(graph(10, 11), graph(10, 11))),
            ModelFingerprint.of(List.of(graph(100, 101), graph(200, 201))));
    }

    @Test
    public void cyclicMapsRemainIndependentOfInsertionOrder() {
        Map<String, Object> first = new LinkedHashMap<>();
        first.put("self", first);
        first.put("value", 1);
        Map<String, Object> second = new LinkedHashMap<>();
        second.put("value", 1);
        second.put("self", second);
        assertEquals(ModelFingerprint.of(first), ModelFingerprint.of(second));
    }

    @Test
    public void distinguishesCyclesAndIgnoresNonCyclicObjectSharing() {
        List<Object> self = new ArrayList<>();
        self.add(self);
        List<Object> otherSelf = new ArrayList<>();
        otherSelf.add(otherSelf);
        assertEquals(ModelFingerprint.of(self), ModelFingerprint.of(otherSelf));
        List<Object> outer = new ArrayList<>();
        List<Object> inner = new ArrayList<>();
        outer.add(inner);
        inner.add(outer);
        assertNotEquals(ModelFingerprint.of(self), ModelFingerprint.of(outer));
        List<String> shared = List.of("x");
        assertEquals(ModelFingerprint.of(List.of(shared, shared)),
            ModelFingerprint.of(List.of(new ArrayList<>(shared), new ArrayList<>(shared))));
    }

    private static DependencyScopeNode graph(long base, long target) {
        DependencyScopeNode root = new DependencyScopeNode(base, "compile", "compile", "");
        root.getDependencies().add(new ArtifactDependencyNodeImpl(base + 1, "g", "a", "1"));
        root.getDependencies().add(new ArtifactDependencyNodeImpl(base + 2, "g", "b", "1"));
        root.getDependencies().add(new ReferenceNode(target));
        return root;
    }

    private record Id(long id) {
    }
}
