package com.intellij.gradle.toolingExtension.impl.ideTask.payload;

import com.intellij.openapi.externalSystem.model.project.dependencies.AbstractDependencyNode;
import com.intellij.openapi.externalSystem.model.project.dependencies.DependencyNode;
import com.intellij.openapi.externalSystem.model.project.dependencies.DependencyScopeNode;
import com.intellij.openapi.externalSystem.model.project.dependencies.ReferenceNode;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class ModelFingerprint {
    private record Layout(List<Field> fields, byte[] signature) {
    }

    private static final ClassValue<Layout> FIELDS = new ClassValue<>() {
        @Override
        protected Layout computeValue(Class<?> type) {
            List<Field> fields = new ArrayList<>();
            for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
                for (Field field : c.getDeclaredFields()) {
                    if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                        field.setAccessible(true);
                        fields.add(field);
                    }
                }
            }
            fields.sort(Comparator.comparing((Field f) -> f.getDeclaringClass().getName()).thenComparing(Field::getName));
            try {
                Writer schema = new Writer(new IdentityHashMap<>(), null);
                schema.text(type.getName());
                schema.out.writeInt(fields.size());
                for (Field field : fields) {
                    schema.text(field.getDeclaringClass().getName());
                    schema.text(field.getName());
                    schema.text(field.getType().getName());
                }
                return new Layout(List.copyOf(fields), schema.finish());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    };

    private ModelFingerprint() {
    }

    public static String of(Object model) {
        try {
            Writer writer = new Writer(new IdentityHashMap<>(), null);
            writer.write(model);
            return "v2:" + HexFormat.of().formatHex(writer.finish());
        } catch (IOException | ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot fingerprint " + (model == null ? "null" : model.getClass().getName()), e);
        }
    }

    private static final class Writer {
        private final MessageDigest digest;
        private final DataOutputStream out;
        private final IdentityHashMap<Object, Integer> ancestors;
        private Map<Long, Integer> nodeIds;

        private Writer(IdentityHashMap<Object, Integer> ancestors, Map<Long, Integer> nodeIds) {
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
            out = new DataOutputStream(new BufferedOutputStream(new DigestOutputStream(OutputStream.nullOutputStream(), digest)));
            this.ancestors = ancestors;
            this.nodeIds = nodeIds;
        }

        private byte[] finish() throws IOException {
            out.flush();
            return digest.digest();
        }

        private void text(String text) throws IOException {
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            out.writeInt(bytes.length);
            out.write(bytes);
        }

        private void write(Object value) throws IOException, ReflectiveOperationException {
            if (value == null) {
                out.writeByte(0);
            } else if (value instanceof CharSequence) {
                out.writeByte(1);
                text(value.toString());
            } else if (value instanceof Number || value instanceof Boolean || value instanceof Character) {
                out.writeByte(2);
                text(value.getClass().getName());
                text(value.toString());
            } else if (value instanceof File) {
                out.writeByte(3);
                text(((File) value).getPath());
            } else if (value instanceof Enum<?>) {
                out.writeByte(4);
                text(((Enum<?>) value).getDeclaringClass().getName());
                text(((Enum<?>) value).name());
            } else if (value instanceof Class<?>) {
                out.writeByte(5);
                text(((Class<?>) value).getName());
            } else if (ancestors.containsKey(value)) {
                out.writeByte(6);
                out.writeInt(ancestors.size() - ancestors.get(value));
            } else {
                ancestors.put(value, ancestors.size());
                Map<Long, Integer> previousIds = nodeIds;
                // IDs are invocation-local, but reference nodes must still point at the same graph nodes.
                if (value instanceof DependencyScopeNode) {
                    nodeIds = new HashMap<>();
                    indexNodes((DependencyScopeNode) value, new IdentityHashMap<>());
                }
                try {
                    writeComposite(value);
                } finally {
                    nodeIds = previousIds;
                    ancestors.remove(value);
                }
            }
        }

        private void indexNodes(DependencyNode node, IdentityHashMap<DependencyNode, Boolean> visited) {
            if (node instanceof ReferenceNode || visited.put(node, Boolean.TRUE) != null) {
                return;
            }
            nodeIds.computeIfAbsent(node.getId(), ignored -> nodeIds.size());
            for (DependencyNode child : node.getDependencies()) {
                indexNodes(child, visited);
            }
        }

        private void writeComposite(Object value) throws IOException, ReflectiveOperationException {
            if (value instanceof Map<?, ?>) {
                out.writeByte(7);
                Map<?, ?> map = (Map<?, ?>) value;
                List<byte[]> entries = new ArrayList<>(map.size());
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    Writer child = new Writer(ancestors, nodeIds);
                    child.write(entry.getKey());
                    child.write(entry.getValue());
                    entries.add(child.finish());
                }
                entries.sort(Arrays::compareUnsigned);
                out.writeInt(entries.size());
                for (byte[] entry : entries) {
                    out.write(entry);
                }
            } else if (value instanceof Collection<?>) {
                out.writeByte(8);
                Collection<?> collection = (Collection<?>) value;
                out.writeInt(collection.size());
                // Some classpaths use LinkedHashSet. Preserve encounter order even when the API says Collection/Set.
                for (Object item : collection) {
                    write(item);
                }
            } else if (value.getClass().isArray()) {
                out.writeByte(9);
                text(value.getClass().getComponentType().getName());
                int length = Array.getLength(value);
                out.writeInt(length);
                for (int i = 0; i < length; i++) {
                    write(Array.get(value, i));
                }
            } else {
                out.writeByte(10);
                Layout layout = FIELDS.get(value.getClass());
                out.write(layout.signature());
                for (Field field : layout.fields()) {
                    if (nodeIds != null && field.getName().equals("id")
                        && (field.getDeclaringClass() == AbstractDependencyNode.class || field.getDeclaringClass() == ReferenceNode.class)) {
                        long id = field.getLong(value);
                        Integer normalized = nodeIds.get(id);
                        if (normalized != null) {
                            write(normalized);
                        } else {
                            write(id);
                        }
                    } else {
                        write(field.get(value));
                    }
                }
            }
        }
    }
}
