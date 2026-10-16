Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-tasks/3-trace-log.txt`

Overall span: 18965 ms, 631773 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 5049 | 5049 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 4616 | 4585 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 374 | 24 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 350 | 350 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 122 | 105 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 50 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 29 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 28 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 21 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 18 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 16 | 2 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 6 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 0 | 0 | 287 |

Totals by scope: build: 122 ms, default: 5399 ms, project: 5235 ms

## Configure project (287 projects, sum 27727 ms)

| Project | ms |
|---|---|
| :architecture-test | 767 |
| :docs | 589 |
| :base-compiler-worker | 502 |
| :declarative-dsl-core | 470 |
| :ant-worker | 437 |
| : | 419 |
| :ant-impl | 418 |
| :ant-api | 380 |
| :kotlin-dsl | 370 |
| :ant | 356 |
| :api-metadata | 339 |
| :base-asm | 336 |
| :antlr | 332 |
| :public-api | 328 |
| :declarative-dsl-evaluator | 283 |
| :kotlin-dsl-integ-tests | 283 |
| :distributions-basics | 222 |
| :bean-serialization-services | 211 |
| :enterprise-plugin-performance | 210 |
| :internal-build-reports | 204 |
| :performance | 200 |
| :distributions-full | 195 |
| :graph-serialization | 186 |
| :guava-serialization-codecs | 162 |
| :distributions-core | 161 |
| :toolchains-jvm | 158 |
| :build-cache-base | 157 |
| :build-cache | 156 |
| :kotlin-dsl-plugins | 156 |
| :tooling-api | 154 |
| :distributions-jvm | 144 |
| :hashing | 141 |
| :ide-native | 141 |
| :build-cache-http | 138 |
| :configuration-cache | 138 |
| :build-discovery-impl | 137 |
| :toolchains-jvm-shared | 133 |
| :plugins-test-report-aggregation | 132 |
| :declarative-dsl-tooling-builders | 131 |
| :resources-sftp | 130 |

## Tasks (651 executed, sum 1378 ms)

| Task | ms |
|---|---|
| :build-logic:integration-testing-kotlin:compileKotlin | 64 |
| :build-logic:uber-plugins:compileKotlin | 38 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 31 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 23 |
| :build-logic:integration-testing:compileKotlin | 19 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 18 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 18 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 17 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 17 |
| :build-logic:documentation:compileGroovy | 17 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 16 |
| :build-logic:buildquality:compileKotlin | 16 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 16 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 15 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 14 |
| :build-logic:packaging:compileKotlin | 14 |
| :build-logic-settings:architecture-docs:generateExternalPluginSpecBuilders | 13 |
| :build-logic-commons:gradle-plugin:compileKotlin | 12 |
| :build-logic:lifecycle:compilePluginsBlocks | 12 |
| :build-logic:jvm:compileKotlin | 12 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:profiling:compileKotlin | 11 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 11 |
| :build-logic-commons:publishing:compileKotlin | 11 |
| :build-logic:kotlin-dsl:compileKotlin | 11 |
| :build-logic:build-update-utils:compileKotlin | 10 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 10 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 9 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:integration-testing:compilePluginsBlocks | 9 |
| :build-logic:root-build:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:root-build:compileKotlin | 9 |
| :build-logic:binary-compatibility:compileKotlin | 9 |
| :build-logic:packaging:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 8 |
| :build-logic-settings:version-catalogs:compileKotlin | 8 |
