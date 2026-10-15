package com.intellij.gradle.toolingExtension.impl.ideTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Bundle file format: magic, entry count, then per entry the model class name (UTF), the byte length and the
 * bytes. Readable without any IDE class; the consumer picks the serializer by class name.
 */
public final class ModelBundle {

    private static final int MAGIC = 0x1DE0_0001;

    private ModelBundle() {
    }

    public static void write(DataOutputStream out, List<ModelPayload> payloads) throws IOException {
        out.writeInt(MAGIC);
        out.writeInt(payloads.size());
        for (ModelPayload payload : payloads) {
            out.writeUTF(payload.getModelClassName());
            out.writeInt(payload.getBytes().length);
            out.write(payload.getBytes());
        }
    }

    public static List<ModelPayload> read(DataInputStream in) throws IOException {
        if (in.readInt() != MAGIC) {
            throw new IOException("not an IDE model bundle");
        }
        int count = in.readInt();
        List<ModelPayload> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String className = in.readUTF();
            byte[] bytes = new byte[in.readInt()];
            in.readFully(bytes);
            result.add(new ModelPayload(className, bytes));
        }
        return result;
    }
}
