Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-tasks/7-trace-log.txt`

Overall span: 19979 ms, 621173 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 6847 | 6815 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 4519 | 4519 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 473 | 473 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 331 | 24 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 101 | 83 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 39 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 34 | 5 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 19 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 18 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 15 | 2 | 0 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 0 | 0 | 287 |

Totals by scope: build: 101 ms, default: 4992 ms, project: 7411 ms

## Configure project (287 projects, sum 24957 ms)

| Project | ms |
|---|---|
| :docs | 747 |
| :base-compiler-worker | 539 |
| :antlr | 535 |
| :ant-impl | 455 |
| :architecture-test | 437 |
| :base-asm | 367 |
| :ant | 355 |
| : | 328 |
| :ant-api | 327 |
| :declarative-dsl-core | 327 |
| :file-operations | 318 |
| :file-temp | 314 |
| :api-metadata | 300 |
| :flow-services | 285 |
| :functional | 280 |
| :file-watching | 275 |
| :ant-worker | 270 |
| :files | 270 |
| :bean-serialization-services | 210 |
| :performance | 196 |
| :collections | 169 |
| :scaladoc-worker | 155 |
| :toolchains-jvm | 152 |
| :client-services | 151 |
| :build-profile | 150 |
| :code-quality | 148 |
| :build-configuration | 146 |
| :configuration-cache | 141 |
| :distributions-full | 138 |
| :resources-sftp | 138 |
| :scala | 138 |
| :tooling-api-provider | 135 |
| :build-cache-base | 133 |
| :tooling-api | 133 |
| :plugins-java | 132 |
| :plugins-java-library | 131 |
| :platform-jvm | 128 |
| :scoped-persistent-cache | 128 |
| :build-logic | 126 |
| :tooling-api-builders | 125 |

## Tasks (651 executed, sum 1682 ms)

| Task | ms |
|---|---|
| :build-logic:idea:generateExternalPluginSpecBuilders | 45 |
| :build-logic:uber-plugins:compileKotlin | 34 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 26 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 25 |
| :build-logic:integration-testing-kotlin:compileJava | 24 |
| :jacoco-workers:ideProjectModel | 24 |
| :javadoc:ideProjectModel | 24 |
| :build-logic:lifecycle:generateExternalPluginSpecBuilders | 23 |
| :gradle-cli:ideProjectModel | 23 |
| :java-platform:ideProjectModel | 23 |
| :jvm-compiler-worker:ideProjectModel | 23 |
| :build-logic-settings:version-catalogs:generatePrecompiledScriptPluginAccessors | 21 |
| :build-logic:integration-testing:extractPrecompiledScriptPluginPlugins | 21 |
| :build-logic:performance-testing:compileGroovy | 21 |
| :instrumentation-agent-services:ideProjectModel | 20 |
| :jvm-services:ideProjectModel | 20 |
| :build-logic:documentation:compileGroovy | 19 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 19 |
| :groovy-compiler-worker:ideProjectModel | 19 |
| :build-logic-settings:configuration-cache-compatibility:generatePrecompiledScriptPluginAccessors | 18 |
| :build-logic:integration-testing:compileKotlin | 18 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 18 |
| :build-logic:integration-testing-kotlin:compileKotlin | 18 |
| :build-logic-settings:default-settings-plugins:generateScriptPluginAdapters | 17 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 16 |
| :build-logic:documentation:pluginDescriptors | 16 |
| :build-logic:buildquality:compileKotlin | 16 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic-commons:publishing:compileKotlin | 15 |
| :build-logic-commons:basics:compileKotlin | 14 |
| :build-logic:packaging:compileKotlin | 14 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 13 |
| :build-logic:build-update-utils:compileKotlin | 13 |
| :build-logic:binary-compatibility:compileKotlin | 13 |
| :build-logic:kotlin-dsl:compileKotlin | 13 |
| :build-logic:packaging:compilePluginsBlocks | 13 |
| :build-logic:profiling:compileKotlin | 12 |
| :build-logic-commons:basics:compilePluginsBlocks | 11 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:lifecycle:compileKotlin | 11 |
