package wbs.harness;

import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelBundle;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelPayload;
import com.intellij.gradle.toolingExtension.impl.modelSerialization.ToolingSerializer;

import java.io.DataInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Reads a bundle entry with one ToolingSerializer, writes it with another, prints both as Ion text. */
public final class RoundTrip {
    public static void main(String[] args) throws Exception {
        Path bundle = Path.of(args[0]);
        String wanted = args[1];
        ToolingSerializer reader = new ToolingSerializer();
        ToolingSerializer writer = new ToolingSerializer();
        try (InputStream in = Files.newInputStream(bundle); DataInputStream data = new DataInputStream(in)) {
            for (ModelPayload p : ModelBundle.read(data)) {
                if (!p.getModelClassName().endsWith(wanted)) continue;
                Class<?> c = Class.forName(p.getModelClassName());
                Object model = p.readModel(RoundTrip.class.getClassLoader());
                System.out.println("read: " + model.getClass().getName());
                byte[] again = writer.write(model);
                byte[] twice = writer.write(model);
                Path t1 = Files.createTempFile("rt1", ".bin"), t2 = Files.createTempFile("rt2", ".bin"), t0 = Files.createTempFile("rt0", ".bin");
                Files.write(t0, p.getBytes()); Files.write(t1, again); Files.write(t2, twice);
                System.out.println("--- original"); IonText.main(new String[]{"--canonical", t0.toString()});
                System.out.println("--- rewritten (fresh writer, first write)"); IonText.main(new String[]{"--canonical", t1.toString()});
                System.out.println("--- rewritten (same writer, second write)"); IonText.main(new String[]{"--canonical", t2.toString()});
            }
        }
    }
}
