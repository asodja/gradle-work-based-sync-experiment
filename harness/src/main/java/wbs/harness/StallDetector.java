package wbs.harness;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Stream;

/**
 * Detects whole-machine stalls during a run: idle Gradle daemons log a "periodic daemon health check" every 10 s,
 * so a gap of a minute between two such lines in an idle daemon's log means every JVM on the machine stood still
 * (observed three times on 2026-10-15, LOG.md). Reports the largest gap over all daemon logs of the user home
 * within the run window.
 */
final class StallDetector {

    private static final String HEALTH_CHECK = "Starting periodic daemon health check";

    /** Largest gap in seconds between consecutive health checks of any daemon log, within [start, end]; -1 if no data. */
    static long maxGapSeconds(Path gradleUserHome, long startMillis, long endMillis) {
        Path daemonDir = gradleUserHome.resolve("daemon");
        if (!Files.isDirectory(daemonDir)) {
            return -1;
        }
        long max = -1;
        try (Stream<Path> logs = Files.walk(daemonDir, 2)) {
            for (Path log : (Iterable<Path>) logs.filter(p -> p.toString().endsWith(".out.log"))::iterator) {
                long gap = maxGapIn(log, startMillis, endMillis);
                max = Math.max(max, gap);
            }
        } catch (IOException e) {
            return -1;
        }
        return max;
    }

    private static long maxGapIn(Path log, long startMillis, long endMillis) {
        long max = -1;
        long previous = -1;
        try {
            List<String> lines = Files.readAllLines(log);
            for (String line : lines) {
                if (!line.contains(HEALTH_CHECK) || line.length() < 29) {
                    continue;
                }
                long t;
                try {
                    t = OffsetDateTime.parse(line.substring(0, 28)).toInstant().toEpochMilli();
                } catch (DateTimeParseException e) {
                    continue;
                }
                if (t < startMillis - 15_000 || t > endMillis + 15_000) {
                    previous = t >= startMillis - 15_000 ? previous : -1;
                    continue;
                }
                if (previous > 0) {
                    max = Math.max(max, (t - previous) / 1000);
                }
                previous = t;
            }
        } catch (IOException | java.io.UncheckedIOException e) {
            return -1;
        }
        return max;
    }
}
