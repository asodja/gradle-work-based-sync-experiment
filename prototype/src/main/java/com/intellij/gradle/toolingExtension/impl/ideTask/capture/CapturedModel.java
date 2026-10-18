package com.intellij.gradle.toolingExtension.impl.ideTask.capture;

import com.intellij.gradle.toolingExtension.impl.ideTask.payload.CompactDependenciesPayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelPayload;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelText;

import java.io.Serializable;

/**
 * One captured model in the task's state. {@code model} is the state form the configuration cache stores (D25, D26):
 * a {@link ModelPayload} (the model serialized at capture time, deflated), a {@link CompactDependenciesPayload}, or the
 * model object itself when its codecs store it well (lists of paths). {@link ProjectModelCapture#toPayloads} turns each
 * into the bundle entry. The fingerprint is the content hash ({@link ModelText}) that drives the up-to-date check.
 */
public final class CapturedModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String modelClassName;
    private final String fingerprint;
    private final Object model;

    public CapturedModel(String modelClassName, String fingerprint, Object model) {
        this.modelClassName = modelClassName;
        this.fingerprint = fingerprint;
        this.model = model;
    }

    public String getModelClassName() {
        return modelClassName;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public Object getModel() {
        return model;
    }
}
