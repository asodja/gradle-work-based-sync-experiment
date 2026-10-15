package com.intellij.gradle.toolingExtension.impl.ideTask;

import org.gradle.api.tasks.Input;

import java.io.Serializable;

/**
 * One serialized model of the bundle: the model class name, the format and the bytes. Format {@link #ION} is
 * IntelliJ's SerializationService for the class; {@link #JAVA} is plain Java serialization, which is what
 * IntelliJ itself falls back to on the Tooling API wire for models without an Ion serializer.
 */
public final class ModelPayload implements Serializable {

    public static final String ION = "ion";
    public static final String JAVA = "java";

    private final String modelClassName;
    private final String format;
    private final String fingerprint;
    private final byte[] bytes;

    public ModelPayload(String modelClassName, String format, String fingerprint, byte[] bytes) {
        this.modelClassName = modelClassName;
        this.format = format;
        this.fingerprint = fingerprint;
        this.bytes = bytes;
    }

    /**
     * Fingerprint of the model's content (ModelText), the up-to-date input. The serialized bytes are not an input:
     * IntelliJ's Ion serializers number objects per writer and iterate hash collections, so the bytes differ
     * between two captures of the same model.
     */
    @Input
    public String getFingerprint() {
        return fingerprint;
    }

    @Input
    public String getModelClassName() {
        return modelClassName;
    }

    @Input
    public String getFormat() {
        return format;
    }

    @org.gradle.api.tasks.Internal
    public byte[] getBytes() {
        return bytes;
    }
}
