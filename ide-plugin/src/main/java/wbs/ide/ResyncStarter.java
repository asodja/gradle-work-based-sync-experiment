package wbs.ide;

import com.intellij.ide.impl.OpenProjectTask;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.application.ApplicationStarter;
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder;
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode;
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.gradle.util.GradleConstants;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Headless re-sync of an already imported project: {@code idea.sh wbsResync <project dir> [count]}. The IDE's own
 * warm-up command imports a project only once; this opens the project and runs the Gradle sync {@code count} times,
 * synchronously, so a re-sync into an existing project model can be reproduced and timed without a display.
 */
public final class ResyncStarter implements ApplicationStarter {

    @Override
    public boolean isHeadless() {
        return true;
    }

    @Override
    public int getRequiredModality() {
        return NOT_IN_EDT;
    }

    @Override
    public void main(List<String> args) {
        int exit = 0;
        try {
            Path dir = Path.of(args.get(1)).toAbsolutePath();
            int count = args.size() > 2 ? Integer.parseInt(args.get(2)) : 2;
            Project project = ProjectUtil.openOrImport(dir, OpenProjectTask.build());
            if (project == null) {
                throw new IllegalStateException("could not open " + dir);
            }
            WbsLog.info("[wbs-resync] opened " + project.getName());
            var processing = com.intellij.openapi.application.ApplicationManager.getApplication()
                .getService(com.intellij.openapi.externalSystem.service.internal.ExternalSystemProcessingManager.class);
            // Opening the project schedules its own sync a moment later; a second sync issued meanwhile is rejected or
            // stops the first one's connection. Give it time to start, then wait for it before every explicit sync.
            Thread.sleep(5000);
            for (int i = 1; i <= count; i++) {
                while (processing.hasTaskOfTypeInProgress(com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskType.RESOLVE_PROJECT, project)) {
                    Thread.sleep(1000);
                }
                long started = System.currentTimeMillis();
                CompletableFuture<Void> done = new CompletableFuture<>();
                ExternalSystemUtil.refreshProject(dir.toString(),
                    new ImportSpecBuilder(project, GradleConstants.SYSTEM_ID).use(ProgressExecutionMode.MODAL_SYNC).withCallback(done));
                try {
                    done.get(30, TimeUnit.MINUTES);
                } catch (java.util.concurrent.ExecutionException e) {
                    // opening the project may have started its own sync; wait for it and retry this round
                    if (e.getCause() != null && String.valueOf(e.getCause().getMessage()).contains("Another 'Sync project' task")) {
                        WbsLog.info("[wbs-resync] a sync is already running, retrying in 10 s");
                        Thread.sleep(10_000);
                        i--;
                        continue;
                    }
                    throw e;
                }
                WbsLog.info("[wbs-resync] sync " + i + " finished in " + (System.currentTimeMillis() - started) + " ms");
            }
        } catch (Throwable t) {
            t.printStackTrace();
            exit = 1;
        }
        System.exit(exit);
    }
}
