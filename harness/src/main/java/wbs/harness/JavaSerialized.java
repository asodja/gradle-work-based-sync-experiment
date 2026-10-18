package wbs.harness;

import java.io.IOException;

/** Java-deserialized Tooling API model bytes (the dump comparisons). */
final class JavaSerialized {
    private JavaSerialized() {
    }

    static Object read(byte[] bytes) throws IOException, ClassNotFoundException {
        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bytes)) {
            @Override
            protected Class<?> resolveClass(java.io.ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                return Class.forName(desc.getName(), false, JavaSerialized.class.getClassLoader());
            }
        }) {
            return in.readObject();
        }
    }
}
