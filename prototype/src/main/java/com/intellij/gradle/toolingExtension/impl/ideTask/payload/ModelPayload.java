package com.intellij.gradle.toolingExtension.impl.ideTask.payload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * (Plain value; the task's up-to-date input is the combined fingerprint, see IdeModelTask.)
 * One serialized model of the bundle: the model class name, the format and the bytes. Format {@link #ION} is
 * IntelliJ's SerializationService for the class; {@link #JAVA} is plain Java serialization, which is what
 * IntelliJ itself falls back to on the Tooling API wire for models without an Ion serializer.
 * <p>
 * The bytes are held deflated (D19): the models are mostly file paths that repeat across models and projects
 * (151,501 class and source path entries over 809 distinct paths in the Kotlin DSL script payloads of gradle/gradle),
 * and the payloads live in the configuration cache entry, the bundle files and the wire. Inflated on read.
 */
public final class ModelPayload implements Serializable {

    private static final long serialVersionUID = 2L;

    public static final String ION = "ion";
    public static final String JAVA = "java";
    /** {@link CompactDependenciesPayload#toBytes()} (D26). */
    public static final String COMPACT_DEPENDENCIES = "compact-deps";

    private final String modelClassName;
    private final String format;
    private final String fingerprint;
    private final int length;
    private final byte[] deflated;

    public ModelPayload(String modelClassName, String format, String fingerprint, byte[] bytes) {
        this(modelClassName, format, fingerprint, bytes.length, deflate(bytes));
    }

    private ModelPayload(String modelClassName, String format, String fingerprint, int length, byte[] deflated) {
        this.modelClassName = modelClassName;
        this.format = format;
        this.fingerprint = fingerprint;
        this.length = length;
        this.deflated = deflated;
    }

    public static ModelPayload ofDeflated(String modelClassName, String format, String fingerprint, int length, byte[] deflated) {
        return new ModelPayload(modelClassName, format, fingerprint, length, deflated);
    }

    /**
     * Fingerprint of the model's content ({@link ModelFingerprint}), the up-to-date input. The serialized bytes are not an input:
     * IntelliJ's Ion serializers number objects per writer and iterate hash collections, so the bytes differ
     * between two captures of the same model.
     */
    public String getFingerprint() {
        return fingerprint;
    }

    public String getModelClassName() {
        return modelClassName;
    }

    public String getFormat() {
        return format;
    }

    /** The serialized model, inflated. */
    public byte[] getBytes() {
        Inflater inflater = new Inflater();
        try {
            inflater.setInput(deflated);
            byte[] out = new byte[length];
            int done = 0;
            while (done < length) {
                int n = inflater.inflate(out, done, length - done);
                if (n == 0 && (inflater.finished() || inflater.needsInput())) {
                    break;
                }
                done += n;
            }
            if (done != length) {
                throw new IllegalStateException("payload of " + modelClassName + ": inflated " + done + " of " + length + " bytes");
            }
            return out;
        } catch (DataFormatException e) {
            throw new IllegalStateException("payload of " + modelClassName + " is corrupt", e);
        } finally {
            inflater.end();
        }
    }

    public int getLength() {
        return length;
    }

    /**
     * The model object: expanded from the compact form, read with IntelliJ's serializer for the class, or Java
     * deserialized. A fresh serializer per read: IntelliJ's Ion readers keep an object table across read() calls of
     * one instance and return the previous document's objects for repeated ids.
     */
    public Object readModel(ClassLoader loader) throws IOException, ClassNotFoundException {
        byte[] bytes = getBytes();
        switch (format) {
            case COMPACT_DEPENDENCIES:
                return CompactDependenciesPayload.fromBytes(bytes).expand();
            case JAVA:
                try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes)) {
                    @Override
                    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                        return Class.forName(desc.getName(), false, loader);
                    }
                }) {
                    return in.readObject();
                }
            default:
                Class<?> modelClass = Class.forName(modelClassName, true, loader);
                try {
                    return new com.intellij.gradle.toolingExtension.impl.modelSerialization.ToolingSerializer().read(bytes, modelClass);
                } catch (com.intellij.gradle.toolingExtension.impl.modelSerialization.SerializationServiceNotFoundException e) {
                    throw new IOException("no serializer for " + modelClassName, e);
                }
        }
    }

    public byte[] getDeflated() {
        return deflated;
    }

    private static byte[] deflate(byte[] bytes) {
        Deflater deflater = new Deflater(Deflater.BEST_SPEED);
        try {
            deflater.setInput(bytes);
            deflater.finish();
            ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(64, bytes.length / 8));
            byte[] buffer = new byte[64 * 1024];
            while (!deflater.finished()) {
                out.write(buffer, 0, deflater.deflate(buffer));
            }
            return out.toByteArray();
        } finally {
            deflater.end();
        }
    }
}
