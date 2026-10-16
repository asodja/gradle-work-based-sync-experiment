Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/heapx-tasks/3-trace-log.txt`

Overall span: 15918 ms, 627069 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 7208 | 7208 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 361 | 361 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 276 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 52 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 38 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 35 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 34 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 33 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 24 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 22 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 19 | 2 | 0 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 7 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 6 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 1 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 1 ms, default: 7569 ms, project: 559 ms

## Configure project (287 projects, sum 32822 ms)

| Project | ms |
|---|---|
| :ant-api | 677 |
| :api-metadata | 668 |
| :base-compiler-worker | 631 |
| :docs | 598 |
| :declarative-dsl-evaluator | 592 |
| :ant-impl | 575 |
| :declarative-dsl-core | 569 |
| :kotlin-dsl | 529 |
| :antlr | 496 |
| :ant | 496 |
| :distributions-full | 487 |
| :kotlin-dsl-integ-tests | 479 |
| :ant-worker | 462 |
| : | 457 |
| :base-asm | 457 |
| :architecture-test | 433 |
| :distributions-basics | 365 |
| :distributions-core | 348 |
| :distributions-jvm | 283 |
| :distributions-native | 268 |
| :kotlin-dsl-plugins | 264 |
| :performance | 245 |
| :enterprise-plugin-performance | 220 |
| :kotlin-dsl-tooling-builders | 215 |
| :kotlin-dsl-tooling-models | 200 |
| :signing | 199 |
| :internal-build-reports | 196 |
| :kotlin-dsl-provider-plugins | 196 |
| :bean-serialization-services | 192 |
| :kotlin-dsl-tooling-model-impls | 189 |
| :configuration-cache | 181 |
| :language-java | 181 |
| :security | 181 |
| :service-registry-builder | 180 |
| :language-groovy | 178 |
| :service-lookup | 177 |
| :scala | 176 |
| :tooling-api | 176 |
| :guava-serialization-codecs | 172 |
| :ide-plugins | 170 |

## Tasks (651 executed, sum 1476 ms)

| Task | ms |
|---|---|
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 48 |
| :build-logic:uber-plugins:compileKotlin | 37 |
| :build-logic:integration-testing-kotlin:compileKotlin | 33 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 24 |
| :build-logic:performance-testing:compileGroovy | 24 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 20 |
| :build-logic:idea:compileKotlin | 20 |
| :build-logic:integration-testing:compileKotlin | 20 |
| :build-logic:buildquality:compileKotlin | 20 |
| :build-logic:idea:compilePluginsBlocks | 19 |
| :build-logic:kotlin-dsl:generateExternalPluginSpecBuilders | 19 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 18 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 18 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 17 |
| :build-logic:documentation:compileGroovy | 16 |
| :build-logic:binary-compatibility:compileKotlin | 15 |
| :build-logic:packaging:compileKotlin | 15 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 14 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 14 |
| :build-logic:lifecycle:compilePluginsBlocks | 14 |
| :build-logic:jvm:compileKotlin | 14 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 13 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 13 |
| :build-logic-commons:basics:compilePluginsBlocks | 13 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 13 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 13 |
| :build-logic:lifecycle:compileKotlin | 13 |
| :build-logic:cleanup:compileKotlin | 13 |
| :build-logic:profiling:compileKotlin | 13 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 13 |
| :build-logic:kotlin-dsl:compileKotlin | 13 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 12 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 12 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 12 |
| :build-logic-commons:build-platform-plugin:compilePluginsBlocks | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 12 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic:buildquality:compilePluginsBlocks | 12 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 11 |
