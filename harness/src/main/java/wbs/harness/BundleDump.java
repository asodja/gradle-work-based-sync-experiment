package wbs.harness;

import com.intellij.gradle.toolingExtension.impl.ideTask.ModelBundle;
import com.intellij.gradle.toolingExtension.impl.ideTask.ModelPayload;
import com.intellij.gradle.toolingExtension.impl.modelSerialization.ToolingSerializer;

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
                    Class<?> modelClass = Class.forName(payload.getModelClassName());
                    Object model = ModelPayload.JAVA.equals(payload.getFormat())
                        ? IdeTasksTransport.readJava(payload.getBytes())
                        : new ToolingSerializer().read(payload.getBytes(), modelClass);
                    System.out.println("fingerprint=" + payload.getFingerprint());
                    System.out.println(com.intellij.gradle.toolingExtension.impl.ideTask.ModelText.canonical(model));
                }
            }
            return;
        }
        for (String arg : args) {
            System.out.println("== " + arg);
            try (InputStream in = Files.newInputStream(Path.of(arg)); DataInputStream data = new DataInputStream(in)) {
                for (ModelPayload payload : ModelBundle.read(data)) {
                    String status;
                    try {
                        Class<?> modelClass = Class.forName(payload.getModelClassName());
                        Object model = ModelPayload.JAVA.equals(payload.getFormat())
                            ? IdeTasksTransport.readJava(payload.getBytes())
                            : new ToolingSerializer().read(payload.getBytes(), modelClass);
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
