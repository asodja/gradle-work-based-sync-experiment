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
    private final byte[] bytes;

    public ModelPayload(String modelClassName, String format, byte[] bytes) {
        this.modelClassName = modelClassName;
        this.format = format;
        this.bytes = bytes;
    }

    @Input
    public String getModelClassName() {
        return modelClassName;
    }

    @Input
    public String getFormat() {
        return format;
    }

    @Input
    public byte[] getBytes() {
        return bytes;
    }
}
