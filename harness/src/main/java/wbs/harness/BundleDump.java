package wbs.harness;

import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelBundle;
import com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelPayload;

import java.io.DataInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Lists the entries of a model bundle and tries to deserialize each one: {@code BundleDump <bundle>...}. */
public final class BundleDump {

    public static void main(String[] args) throws Exception {
        // --text <ClassSimpleName> <bundle>: print the canonical reflective text of that entry instead of the listing
        if (args.length >= 3 && args[0].equals("--text")) {
            try (InputStream in = Files.newInputStream(Path.of(args[2])); DataInputStream data = new DataInputStream(in)) {
                for (ModelPayload payload : ModelBundle.read(data)) {
                    if (!payload.getModelClassName().endsWith("." + args[1])) continue;
                    Object model = payload.readModel(BundleDump.class.getClassLoader());
                    System.out.println("fingerprint=" + payload.getFingerprint());
                    System.out.println(com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelText.canonical(model));
                }
            }
            return;
        }
        // --text-all <ClassSimpleName> <out dir> <bundle>...: one canonical text file per bundle, in one JVM
        if (args.length >= 4 && args[0].equals("--text-all")) {
            Path outDir = Path.of(args[2]);
            Files.createDirectories(outDir);
            for (int i = 3; i < args.length; i++) {
                String name = args[i].replaceAll("[/\\\\:]", "_") + ".txt";
                StringBuilder text = new StringBuilder();
                try (InputStream in = Files.newInputStream(Path.of(args[i])); DataInputStream data = new DataInputStream(in)) {
                    for (ModelPayload payload : ModelBundle.read(data)) {
                        if (!payload.getModelClassName().endsWith("." + args[1])) continue;
                        Object model = payload.readModel(BundleDump.class.getClassLoader());
                        text.append("fingerprint=").append(payload.getFingerprint()).append('\n');
                        text.append(com.intellij.gradle.toolingExtension.impl.ideTask.payload.ModelText.canonical(model)).append('\n');
                    }
                } catch (Exception e) {
                    text.append("ERROR ").append(e).append('\n');
                }
                Files.writeString(outDir.resolve(name), text.toString());
            }
            return;
        }
        for (String arg : args) {
            System.out.println("== " + arg);
            try (InputStream in = Files.newInputStream(Path.of(arg)); DataInputStream data = new DataInputStream(in)) {
                for (ModelPayload payload : ModelBundle.read(data)) {
                    String status;
                    try {
                        Object model = payload.readModel(BundleDump.class.getClassLoader());
                        status = "ok " + (model == null ? "null" : model.getClass().getSimpleName());
                    } catch (Throwable t) {
                        status = "FAIL " + t;
                    }
                    System.out.printf("%-75s %-5s %8d  %s%n", payload.getModelClassName(), payload.getFormat(), payload.getBytes().length, status);
                }
            }
        }
    }
}
