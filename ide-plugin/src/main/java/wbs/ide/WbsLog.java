package wbs.ide;

import com.intellij.openapi.diagnostic.Logger;

/** Diagnostics to stderr (headless runs capture it) and to idea.log (the IDE and gradle-profiler sandboxes do not). */
final class WbsLog {
    private static final Logger LOG = Logger.getInstance("#wbs");

    private WbsLog() {
    }

    static void info(String message) {
        System.err.println(message);
        LOG.info(message);
    }
}
