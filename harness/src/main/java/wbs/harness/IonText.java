package wbs.harness;

import com.amazon.ion.IonContainer;
import com.amazon.ion.IonList;
import com.amazon.ion.IonReader;
import com.amazon.ion.IonSexp;
import com.amazon.ion.IonStruct;
import com.amazon.ion.IonSystem;
import com.amazon.ion.IonValue;
import com.amazon.ion.IonWriter;
import com.amazon.ion.system.IonReaderBuilder;
import com.amazon.ion.system.IonSystemBuilder;
import com.amazon.ion.system.IonTextWriterBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Prints IntelliJ's binary Ion model files as text, for diffing: {@code IonText [--canonical] <file>...}.
 * {@code --canonical} sorts struct fields by name and sorts lists whose elements are {@code {key, value}} entries
 * (IntelliJ serializes maps that way, in hash order) by their key, so two semantically equal models print equal.
 */
public final class IonText {

    public static void main(String[] args) throws IOException {
        boolean canonical = false;
        List<String> files = new ArrayList<>();
        for (String arg : args) {
            if (arg.equals("--canonical")) {
                canonical = true;
            } else {
                files.add(arg);
            }
        }
        IonSystem system = IonSystemBuilder.standard().build();
        // The Ion writer closes its stream; never close System.out.
        OutputStream out = new java.io.FilterOutputStream(System.out) {
            @Override
            public void close() throws IOException {
                flush();
            }
        };
        for (String file : files) {
            byte[] bytes = Files.readAllBytes(Path.of(file));
            if (canonical) {
                for (IonValue value : system.getLoader().load(bytes)) {
                    IonValue sorted = canonicalize(system, value);
                    try (IonWriter writer = IonTextWriterBuilder.pretty().build(out)) {
                        sorted.writeTo(writer);
                    }
                    System.out.println();
                }
            } else {
                try (IonReader reader = IonReaderBuilder.standard().build(bytes);
                     IonWriter writer = IonTextWriterBuilder.pretty().build(out)) {
                    writer.writeValues(reader);
                }
                System.out.println();
            }
        }
    }

    private static IonValue canonicalize(IonSystem system, IonValue value) {
        if (value instanceof IonStruct) {
            IonStruct struct = (IonStruct) value;
            List<Map.Entry<String, IonValue>> fields = new ArrayList<>();
            for (IonValue field : struct) {
                fields.add(Map.entry(field.getFieldName(), canonicalize(system, field)));
            }
            fields.sort(Map.Entry.comparingByKey());
            IonStruct result = system.newEmptyStruct();
            for (Map.Entry<String, IonValue> field : fields) {
                result.add(field.getKey(), field.getValue());
            }
            return result;
        }
        if (value instanceof IonList || value instanceof IonSexp) {
            List<IonValue> elements = new ArrayList<>();
            boolean entries = !((IonContainer) value).isEmpty();
            for (IonValue element : (IonContainer) value) {
                IonValue c = canonicalize(system, element);
                elements.add(c);
                entries &= c instanceof IonStruct && ((IonStruct) c).containsKey("key") && ((IonStruct) c).size() == 2;
            }
            if (entries) {
                elements.sort((a, b) -> ((IonStruct) a).get("key").toString().compareTo(((IonStruct) b).get("key").toString()));
            }
            IonList result = system.newEmptyList();
            for (IonValue element : elements) {
                result.add(element);
            }
            return result;
        }
        return value.clone();
    }
}
