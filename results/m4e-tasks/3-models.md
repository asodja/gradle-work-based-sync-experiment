Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4e-tasks/3-trace-log.txt`

Overall span: 23209 ms, 626828 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 5867 | 5867 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 333 | 333 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 323 | 18 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 34 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 33 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 33 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 22 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 20 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 18 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 14 | 2 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 0 ms, default: 6200 ms, project: 549 ms

## Configure project (287 projects, sum 31759 ms)

| Project | ms |
|---|---|
| :ant | 626 |
| :architecture-test | 607 |
| :base-asm | 594 |
| :kotlin-dsl | 565 |
| :base-compiler-worker | 546 |
| :api-metadata | 532 |
| :ant-api | 518 |
| :distributions-full | 495 |
| :ant-worker | 489 |
| :kotlin-dsl-integ-tests | 478 |
| :declarative-dsl-core | 459 |
| :docs | 446 |
| : | 418 |
| :internal-build-reports | 412 |
| :ant-impl | 401 |
| :antlr | 401 |
| :performance | 289 |
| :declarative-dsl-evaluator | 283 |
| :distributions-basics | 256 |
| :distributions-core | 242 |
| :tooling-api | 228 |
| :wrapper-main | 206 |
| :enterprise-plugin-performance | 202 |
| :public-api | 201 |
| :isolated-action-services | 190 |
| :build-option | 184 |
| :bean-serialization-services | 181 |
| :ivy | 178 |
| :build-cache | 173 |
| :toolchains-jvm | 170 |
| :scoped-persistent-cache | 169 |
| :isolated-ant-builder | 163 |
| :distributions-jvm | 160 |
| :internal-instrumentation-processor | 157 |
| :jacoco-workers | 156 |
| :kotlin-dsl-plugins | 154 |
| :language-jvm | 152 |
| :service-lookup | 152 |
| :distributions-native | 149 |
| :flow-services | 146 |

## Tasks (651 executed, sum 1559 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:compileKotlin | 41 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 28 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 27 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 25 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 24 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 24 |
| :build-logic:integration-testing:compileKotlin | 24 |
| :build-logic:integration-testing-kotlin:compileKotlin | 23 |
| :build-logic:kotlin-dsl:compileKotlin | 21 |
| :build-logic:documentation:compileGroovy | 20 |
| :build-logic:buildquality:compileKotlin | 19 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 19 |
| :build-logic:binary-compatibility:compileKotlin | 18 |
| :build-logic:packaging:generatePrecompiledScriptPluginAccessors | 17 |
| :build-logic:packaging:compileKotlin | 17 |
| :build-logic:integration-testing:compilePluginsBlocks | 15 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic:jvm:compileKotlin | 14 |
| :build-logic:build-update-utils:compileKotlin | 14 |
| :build-logic:performance-testing:compileGroovy | 14 |
| :build-logic-settings:version-catalogs:compileKotlin | 13 |
| :build-logic:profiling:compileKotlin | 13 |
| :build-logic:buildquality:generateExternalPluginSpecBuilders | 13 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 12 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 12 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 11 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 11 |
| :build-logic-settings:build-environment:compileKotlin | 11 |
| :build-logic:cleanup:compilePluginsBlocks | 11 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 10 |
| :build-logic:idea:compileKotlin | 10 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 10 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:buildquality:compilePluginsBlocks | 10 |
| :build-logic-commons:basics:compilePluginsBlocks | 9 |
| :build-logic:jvm:compilePluginsBlocks | 9 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 9 |
| :build-logic-commons:publishing:compilePluginsBlocks | 9 |
| :build-logic:kotlin-dsl:generateExternalPluginSpecBuilders | 9 |
