import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Replaces class files in a jar: JarOverlay <jar> <class dir> <class file path>...
 * <p>
 * The IDE's jars carry a lookup index (an {@code __index__} entry plus a marker in the archive comment) that
 * IntelliJ's own zip reader trusts for entry offsets. Info-ZIP's {@code zip -u} keeps both while moving the entries,
 * so the IDE then reads a patched class at a stale offset and fails to load it. This rewrite drops the index and the
 * comment (the reader falls back to the central directory, as with jars written by the JDK's jar tool) and stores
 * entries uncompressed as the originals are. Run with the IDE's bundled JBR: {@code java JarOverlay.java ...}
 */
public class JarOverlay {
    public static void main(String[] args) throws IOException {
        File jar = new File(args[0]);
        Path classDir = Path.of(args[1]);
        Map<String, byte[]> replacements = new LinkedHashMap<>();
        for (int i = 2; i < args.length; i++) {
            replacements.put(args[i], Files.readAllBytes(classDir.resolve(args[i])));
        }
        File tmp = new File(jar.getPath() + ".tmp");
        List<String> replaced = new ArrayList<>();
        try (ZipInputStream in = new ZipInputStream(new FileInputStream(jar));
             ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tmp))) {
            out.setMethod(ZipOutputStream.STORED);
            ZipEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                if (entry.getName().equals("__index__")) {
                    continue;
                }
                byte[] data = replacements.containsKey(entry.getName()) ? replacements.get(entry.getName()) : readAll(in);
                if (replacements.containsKey(entry.getName())) {
                    replaced.add(entry.getName());
                }
                write(out, entry.getName(), data, entry.isDirectory());
            }
            for (Map.Entry<String, byte[]> e : replacements.entrySet()) {
                if (!replaced.contains(e.getKey())) {
                    write(out, e.getKey(), e.getValue(), false);
                    replaced.add(e.getKey());
                }
            }
        }
        if (!tmp.renameTo(jar)) {
            Files.move(tmp.toPath(), jar.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        System.out.println("patched " + jar.getName() + ": " + replaced.size() + " classes");
    }

    private static void write(ZipOutputStream out, String name, byte[] data, boolean directory) throws IOException {
        ZipEntry entry = new ZipEntry(name);
        entry.setMethod(ZipEntry.STORED);
        entry.setSize(data.length);
        entry.setCompressedSize(data.length);
        CRC32 crc = new CRC32();
        crc.update(data);
        entry.setCrc(crc.getValue());
        out.putNextEntry(entry);
        if (!directory) {
            out.write(data);
        }
        out.closeEntry();
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[65536];
        int n;
        while ((n = in.read(buffer)) > 0) {
            bytes.write(buffer, 0, n);
        }
        return bytes.toByteArray();
    }
}
