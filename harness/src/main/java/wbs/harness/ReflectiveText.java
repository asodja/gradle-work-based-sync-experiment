package wbs.harness;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Canonical text of an object graph by reflection: fields sorted by name, collections sorted by their element
 * text, maps sorted by key text. For comparing Java-serialized IntelliJ models that have no Ion serializer.
 */
final class ReflectiveText {

    private ReflectiveText() {
    }

    static String canonical(Object root) {
        StringBuilder out = new StringBuilder();
        write(root, out, 0, new IdentityHashMap<>());
        return out.toString();
    }

    private static void write(Object o, StringBuilder out, int depth, IdentityHashMap<Object, Boolean> seen) {
        if (o == null) {
            out.append("null");
            return;
        }
        if (o instanceof CharSequence || o instanceof Number || o instanceof Boolean || o instanceof Character
            || o instanceof Enum || o instanceof File || o instanceof Class) {
            out.append(o instanceof CharSequence ? "\"" + o + "\"" : String.valueOf(o));
            return;
        }
        if (seen.containsKey(o)) {
            out.append("<cycle>");
            return;
        }
        seen.put(o, Boolean.TRUE);
        String indent = "  ".repeat(depth + 1);
        if (o instanceof Map) {
            TreeMap<String, String> entries = new TreeMap<>();
            for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
                StringBuilder k = new StringBuilder();
                write(e.getKey(), k, depth + 1, seen);
                StringBuilder v = new StringBuilder();
                write(e.getValue(), v, depth + 1, seen);
                entries.put(k.toString(), v.toString());
            }
            out.append("{\n");
            entries.forEach((k, v) -> out.append(indent).append(k).append(": ").append(v).append("\n"));
            out.append("  ".repeat(depth)).append("}");
        } else if (o instanceof Collection || o.getClass().isArray()) {
            List<String> items = new ArrayList<>();
            Iterable<?> iterable = o instanceof Collection ? (Collection<?>) o : java.util.Arrays.asList((Object[]) o);
            for (Object item : iterable) {
                StringBuilder b = new StringBuilder();
                write(item, b, depth + 1, seen);
                items.add(b.toString());
            }
            java.util.Collections.sort(items);
            out.append("[\n");
            items.forEach(i -> out.append(indent).append(i).append("\n"));
            out.append("  ".repeat(depth)).append("]");
        } else {
            out.append(o.getClass().getSimpleName()).append(" {\n");
            TreeMap<String, Object> fields = new TreeMap<>();
            for (Class<?> c = o.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                for (Field f : c.getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers()) || Modifier.isTransient(f.getModifiers())) {
                        continue;
                    }
                    try {
                        f.setAccessible(true);
                        fields.put(f.getName(), f.get(o));
                    } catch (ReflectiveOperationException | RuntimeException e) {
                        fields.put(f.getName(), "<inaccessible>");
                    }
                }
            }
            for (Map.Entry<String, Object> f : fields.entrySet()) {
                out.append(indent).append(f.getKey()).append(": ");
                write(f.getValue(), out, depth + 1, seen);
                out.append("\n");
            }
            out.append("  ".repeat(depth)).append("}");
        }
        seen.remove(o);
    }
}
