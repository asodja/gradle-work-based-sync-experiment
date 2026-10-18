package wbs.harness;

import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelBundle;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelPayload;
import com.intellij.gradle.toolingExtension.impl.modelSerialization.ToolingSerializer;

import java.io.DataInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Writes the same model class from several bundles with ONE serializer and prints the size of each result. */
public final class RoundTrip2 {
    public static void main(String[] args) throws Exception {
        String wanted = args[0];
        ToolingSerializer reader = new ToolingSerializer();
        ToolingSerializer writer = new ToolingSerializer();
        for (int i = 1; i < args.length; i++) {
            try (InputStream in = Files.newInputStream(Path.of(args[i])); DataInputStream data = new DataInputStream(in)) {
                for (ModelPayload p : ModelBundle.read(data)) {
                    if (!p.getModelClassName().endsWith(wanted)) continue;
                    Class<?> c = Class.forName(p.getModelClassName());
                    Object model = p.readModel(RoundTrip2.class.getClassLoader());
                    byte[] again = writer.write(model);
                    System.out.printf("%-60s original=%6d rewritten=%6d  identityHash=%s%n", Path.of(args[i]).getParent().getParent().getParent().getParent().getFileName(), p.getBytes().length, again.length, Integer.toHexString(System.identityHashCode(model)));
                }
            }
        }
    }
}
