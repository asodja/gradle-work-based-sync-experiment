package com.intellij.gradle.toolingExtension.impl.ideTask;

import org.gradle.api.tasks.Input;

import java.io.Serializable;

/** One serialized model of the bundle: the model class name and IntelliJ's Ion bytes for it. */
public final class ModelPayload implements Serializable {

    private final String modelClassName;
    private final byte[] bytes;

    public ModelPayload(String modelClassName, byte[] bytes) {
        this.modelClassName = modelClassName;
        this.bytes = bytes;
    }

    @Input
    public String getModelClassName() {
        return modelClassName;
    }

    @Input
    public byte[] getBytes() {
        return bytes;
    }
}
