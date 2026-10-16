package com.intellij.gradle.toolingExtension.impl.ideTask;

import com.intellij.gradle.toolingExtension.impl.model.sourceSetDependencyModel.DefaultGradleSourceSetDependencyModel;
import org.jetbrains.plugins.gradle.ExternalDependencyId;
import org.jetbrains.plugins.gradle.model.DefaultExternalLibraryDependency;
import org.jetbrains.plugins.gradle.model.DefaultExternalProjectDependency;
import org.jetbrains.plugins.gradle.model.DefaultFileCollectionDependency;
import org.jetbrains.plugins.gradle.model.DefaultUnresolvedExternalDependency;
import org.jetbrains.plugins.gradle.model.AbstractExternalDependency;
import org.jetbrains.plugins.gradle.model.ExternalDependency;
import org.jetbrains.plugins.gradle.model.ExternalLibraryDependency;
import org.jetbrains.plugins.gradle.model.ExternalProjectDependency;
import org.jetbrains.plugins.gradle.model.FileCollectionDependency;
import org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel;
import org.jetbrains.plugins.gradle.model.UnresolvedExternalDependency;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code GradleSourceSetDependencyModel} as a string table plus one varint record per dependency (DECISIONS D26).
 * The strings are a {@code List<String>} so that the configuration cache stores them through its entry-wide string
 * table (the same jar paths recur in most projects); the records are a {@code byte[]}, which it stores raw. The
 * bundle carries the same two parts ({@link #toBytes()}); the consumer expands them back into IntelliJ's model
 * objects with {@link #expand()}.
 */
public final class CompactDependencies implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int LIBRARY = 1;
    private static final int PROJECT = 2;
    private static final int FILES = 3;
    private static final int UNRESOLVED = 4;

    private final List<String> strings;
    private final byte[] records;

    private CompactDependencies(List<String> strings, byte[] records) {
        this.strings = strings;
        this.records = records;
    }

    /** @throws IllegalArgumentException for a dependency type this format does not know */
    public static CompactDependencies of(GradleSourceSetDependencyModel model) {
        Writer w = new Writer();
        Map<String, ? extends Collection<? extends ExternalDependency>> bySourceSet = model.getDependencies();
        if (bySourceSet == null) {
            w.count(null);
        } else {
            w.count(bySourceSet.size());
            for (Map.Entry<String, ? extends Collection<? extends ExternalDependency>> e : bySourceSet.entrySet()) {
                w.ref(e.getKey());
                w.dependencies(e.getValue());
            }
        }
        return new CompactDependencies(w.strings, w.out.toByteArray());
    }

    public GradleSourceSetDependencyModel expand() {
        Reader r = new Reader(strings, records);
        DefaultGradleSourceSetDependencyModel model = new DefaultGradleSourceSetDependencyModel();
        int sourceSets = r.count();
        if (sourceSets < 0) {
            model.setDependencies(null);
            return model;
        }
        Map<String, Collection<ExternalDependency>> bySourceSet = new LinkedHashMap<>();
        for (int i = 0; i < sourceSets; i++) {
            String name = r.ref();
            bySourceSet.put(name, r.dependencies());
        }
        model.setDependencies(bySourceSet);
        return model;
    }

    public byte[] toBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream(records.length + strings.size() * 64);
        writeVarint(out, strings.size());
        for (String s : strings) {
            byte[] utf = s.getBytes(StandardCharsets.UTF_8);
            writeVarint(out, utf.length);
            out.write(utf, 0, utf.length);
        }
        writeVarint(out, records.length);
        out.write(records, 0, records.length);
        return out.toByteArray();
    }

    public static CompactDependencies fromBytes(byte[] bytes) {
        int[] pos = {0};
        int count = readVarint(bytes, pos);
        List<String> strings = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int length = readVarint(bytes, pos);
            strings.add(new String(bytes, pos[0], length, StandardCharsets.UTF_8));
            pos[0] += length;
        }
        int length = readVarint(bytes, pos);
        byte[] records = new byte[length];
        System.arraycopy(bytes, pos[0], records, 0, length);
        return new CompactDependencies(strings, records);
    }

    public int stringCount() {
        return strings.size();
    }

    public int recordBytes() {
        return records.length;
    }

    private static void writeVarint(ByteArrayOutputStream out, int value) {
        while ((value & ~0x7F) != 0) {
            out.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.write(value);
    }

    private static int readVarint(byte[] bytes, int[] pos) {
        int result = 0;
        int shift = 0;
        while (true) {
            int b = bytes[pos[0]++];
            result |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
            shift += 7;
        }
    }

    private static final class Writer {
        final List<String> strings = new ArrayList<>();
        final Map<String, Integer> index = new HashMap<>();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        void varint(int value) {
            writeVarint(out, value);
        }

        /** Counts are stored plus one so that a null collection is 0. */
        void count(Integer size) {
            varint(size == null ? 0 : size + 1);
        }

        /** Strings are stored as index plus one so that null is 0. */
        void ref(String s) {
            if (s == null) {
                varint(0);
                return;
            }
            Integer i = index.get(s);
            if (i == null) {
                strings.add(s);
                i = strings.size();
                index.put(s, i);
            }
            varint(i);
        }

        void file(File f) {
            ref(f == null ? null : f.getPath());
        }

        void files(Collection<File> files) {
            if (files == null) {
                count(null);
                return;
            }
            count(files.size());
            for (File f : files) {
                file(f);
            }
        }

        void dependencies(Collection<? extends ExternalDependency> dependencies) {
            if (dependencies == null) {
                count(null);
                return;
            }
            count(dependencies.size());
            for (ExternalDependency d : dependencies) {
                dependency(d);
            }
        }

        void dependency(ExternalDependency d) {
            int kind;
            if (d instanceof ExternalLibraryDependency) {
                kind = LIBRARY;
            } else if (d instanceof ExternalProjectDependency) {
                kind = PROJECT;
            } else if (d instanceof FileCollectionDependency) {
                kind = FILES;
            } else if (d instanceof UnresolvedExternalDependency) {
                kind = UNRESOLVED;
            } else {
                throw new IllegalArgumentException("unknown dependency type " + d.getClass().getName());
            }
            varint(kind);
            ExternalDependencyId id = d.getId();
            varint(id == null ? 0 : 1);
            if (id != null) {
                ref(id.getGroup());
                ref(id.getName());
                ref(id.getVersion());
                ref(id.getPackaging());
                ref(id.getClassifier());
            }
            ref(d.getScope());
            ref(d.getSelectionReason());
            // zigzag: the order is a small non-negative int, but nothing forbids -1
            varint((d.getClasspathOrder() << 1) ^ (d.getClasspathOrder() >> 31));
            varint(d.getExported() ? 1 : 0);
            switch (kind) {
                case LIBRARY: {
                    ExternalLibraryDependency lib = (ExternalLibraryDependency) d;
                    file(lib.getFile());
                    file(lib.getSource());
                    file(lib.getJavadoc());
                    break;
                }
                case PROJECT: {
                    ExternalProjectDependency project = (ExternalProjectDependency) d;
                    ref(project.getProjectPath());
                    ref(project.getConfigurationName());
                    files(project.getProjectDependencyArtifacts());
                    files(project.getProjectDependencyArtifactsSources());
                    break;
                }
                case FILES: {
                    FileCollectionDependency files = (FileCollectionDependency) d;
                    files(files.getFiles());
                    varint(files.isExcludedFromIndexing() ? 1 : 0);
                    break;
                }
                default: {
                    ref(((UnresolvedExternalDependency) d).getFailureMessage());
                    break;
                }
            }
            dependencies(d.getDependencies());
        }
    }

    private static final class Reader {
        private final List<String> strings;
        private final byte[] bytes;
        private final int[] pos = {0};

        Reader(List<String> strings, byte[] bytes) {
            this.strings = strings;
            this.bytes = bytes;
        }

        int varint() {
            return readVarint(bytes, pos);
        }

        /** -1 for a null collection. */
        int count() {
            return varint() - 1;
        }

        String ref() {
            int i = varint();
            return i == 0 ? null : strings.get(i - 1);
        }

        File file() {
            String path = ref();
            return path == null ? null : new File(path);
        }

        Collection<File> files() {
            int n = count();
            if (n < 0) {
                return null;
            }
            List<File> result = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                result.add(file());
            }
            return result;
        }

        Collection<ExternalDependency> dependencies() {
            int n = count();
            if (n < 0) {
                return null;
            }
            List<ExternalDependency> result = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                result.add(dependency());
            }
            return result;
        }

        ExternalDependency dependency() {
            int kind = varint();
            boolean hasId = varint() == 1;
            String group = null, name = null, version = null, packaging = null, classifier = null;
            if (hasId) {
                group = ref();
                name = ref();
                version = ref();
                packaging = ref();
                classifier = ref();
            }
            String scope = ref();
            String selectionReason = ref();
            int zigzag = varint();
            int classpathOrder = (zigzag >>> 1) ^ -(zigzag & 1);
            boolean exported = varint() == 1;
            AbstractExternalDependency d;
            switch (kind) {
                case LIBRARY: {
                    DefaultExternalLibraryDependency lib = new DefaultExternalLibraryDependency();
                    lib.setFile(file());
                    lib.setSource(file());
                    lib.setJavadoc(file());
                    d = lib;
                    break;
                }
                case PROJECT: {
                    DefaultExternalProjectDependency project = new DefaultExternalProjectDependency();
                    project.setProjectPath(ref());
                    project.setConfigurationName(ref());
                    project.setProjectDependencyArtifacts(files());
                    project.setProjectDependencyArtifactsSources(files());
                    d = project;
                    break;
                }
                case FILES: {
                    Collection<File> files = files();
                    DefaultFileCollectionDependency fc = new DefaultFileCollectionDependency(files == null ? new ArrayList<>() : files);
                    fc.setExcludedFromIndexing(varint() == 1);
                    d = fc;
                    break;
                }
                case UNRESOLVED: {
                    DefaultUnresolvedExternalDependency unresolved = new DefaultUnresolvedExternalDependency();
                    unresolved.setFailureMessage(ref());
                    d = unresolved;
                    break;
                }
                default:
                    throw new IllegalStateException("corrupt compact dependency record: kind " + kind);
            }
            if (hasId) {
                d.setGroup(group);
                d.setName(name);
                d.setVersion(version);
                d.setPackaging(packaging);
                d.setClassifier(classifier);
            }
            d.setScope(scope);
            d.setSelectionReason(selectionReason);
            d.setClasspathOrder(classpathOrder);
            d.setExported(exported);
            Collection<ExternalDependency> nested = dependencies();
            if (nested != null) {
                d.setDependencies(nested);
            }
            return d;
        }
    }
}
