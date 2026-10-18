package com.intellij.gradle.toolingExtension.impl.ideTask.capture;

import com.intellij.gradle.toolingExtension.impl.modelBuilder.DefaultModelBuilderContext;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.jetbrains.plugins.gradle.tooling.ModelBuilderContext;

/**
 * Holds the one builder context of a build invocation, as IntelliJ's ExtraModelBuilder does: the builders share
 * state through it, most importantly the source set artifact index, which maps every project's jar to its source
 * set and is read when another project's dependency model is built. A shared build service of the root build,
 * because under Isolated Projects every project sees its own {@code Gradle} wrapper, so the Gradle object cannot
 * be the key; the service is discarded with the build, so the context (which reaches every project) is not kept
 * alive in the daemon.
 */
public abstract class BuilderContextService implements BuildService<BuildServiceParameters.None>, AutoCloseable {

    static final String NAME = "ideModelBuilderContext";

    private ModelBuilderContext context;

    static ModelBuilderContext of(Gradle gradle) {
        Gradle root = gradle;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return root.getSharedServices().registerIfAbsent(NAME, BuilderContextService.class, spec -> { }).get().contextFor(root);
    }

    private synchronized ModelBuilderContext contextFor(Gradle rootGradle) {
        if (context == null) {
            context = new DefaultModelBuilderContext(rootGradle);
        }
        return context;
    }

    @Override
    public synchronized void close() {
        context = null;
    }
}
