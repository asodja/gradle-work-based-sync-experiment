package wbs.harness;

import com.intellij.gradle.toolingExtension.impl.ideTask.ModelText;
import com.intellij.gradle.toolingExtension.impl.modelSerialization.ToolingSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Deserializes every dumped model of one class in the dump's consumption order with ONE serializer (as the IDE's
 * GradleIdeaModelHolder.convertModelsWithType does) and writes each model's canonical reflective text next to it as
 * <file>.txt. Needed for models whose Ion documents reference objects of earlier documents (dependency models).
 * Usage: OrderedModelText <dump dir> <model class simple name> <model class name>
 */
public final class OrderedModelText {

    public static void main(String[] args) throws Exception {
        Path dump = Path.of(args[0]);
        String dirPrefix = args[1];
        Class<?> modelClass = Class.forName(args[2]);
        ToolingSerializer serializer = new ToolingSerializer();
        List<String> order = Files.readAllLines(dump.resolve("order.txt"));
        int n = 0;
        for (String rel : order) {
            if (!rel.startsWith(dirPrefix)) {
                continue;
            }
            Path file = dump.resolve(rel);
            if (!Files.isRegularFile(file)) {
                continue;
            }
            byte[] bytes = Files.readAllBytes(file);
            Object model;
            try {
                model = isIon(bytes) ? serializer.read(bytes, modelClass) : IdeTasksTransport.readJava(bytes);
            } catch (Exception e) {
                model = "UNREADABLE: " + e;
            }
            Files.writeString(Path.of(file + ".txt"), ModelText.canonical(model));
            n++;
        }
        System.out.println(n + " models written as text under " + dump.resolve(dirPrefix));
    }

    private static boolean isIon(byte[] b) {
        return b.length >= 4 && (b[0] & 0xff) == 0xE0 && b[1] == 0x01 && b[2] == 0x00 && (b[3] & 0xff) == 0xEA;
    }
}
