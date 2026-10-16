package com.intellij.gradle.toolingExtension.impl.ideTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Bundle file format: magic, entry count, then per entry the model class name (UTF), the format (UTF), the
 * fingerprint (UTF), the inflated length, the deflated length and the deflated bytes (D19). Readable without any IDE
 * class; the consumer picks the serializer by class name.
 */
public final class ModelBundle {

    private static final int MAGIC = 0x1DE0_0004;

    private ModelBundle() {
    }

    public static void write(DataOutputStream out, List<ModelPayload> payloads) throws IOException {
        out.writeInt(MAGIC);
        out.writeInt(payloads.size());
        for (ModelPayload payload : payloads) {
            out.writeUTF(payload.getModelClassName());
            out.writeUTF(payload.getFormat());
            out.writeUTF(payload.getFingerprint());
            out.writeInt(payload.getLength());
            out.writeInt(payload.getDeflated().length);
            out.write(payload.getDeflated());
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
            String format = in.readUTF();
            String fingerprint = in.readUTF();
            int length = in.readInt();
            byte[] deflated = new byte[in.readInt()];
            in.readFully(deflated);
            result.add(ModelPayload.ofDeflated(className, format, fingerprint, length, deflated));
        }
        return result;
    }
}
