Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4h-tasks/3-trace-log.txt`

Overall span: 15504 ms, 627002 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 5360 | 5360 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 298 | 298 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 297 | 16 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 42 | 9 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 38 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 29 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 28 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 22 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 22 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 13 | 2 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 6 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 6 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 0 ms, default: 5658 ms, project: 547 ms

## Configure project (287 projects, sum 27479 ms)

| Project | ms |
|---|---|
| :architecture-test | 651 |
| :kotlin-dsl | 514 |
| :base-asm | 482 |
| :kotlin-dsl-integ-tests | 481 |
| :ant | 445 |
| :api-metadata | 445 |
| :ant-worker | 402 |
| : | 397 |
| :declarative-dsl-core | 366 |
| :docs | 364 |
| :antlr | 339 |
| :ant-impl | 339 |
| :ant-api | 339 |
| :base-compiler-worker | 339 |
| :declarative-dsl-evaluator | 266 |
| :performance | 255 |
| :distributions-full | 247 |
| :tooling-api | 240 |
| :bean-serialization-services | 219 |
| :public-api | 210 |
| :internal-build-reports | 208 |
| :distributions-core | 186 |
| :enterprise-plugin-performance | 184 |
| :build-cache | 177 |
| :distributions-basics | 177 |
| :configuration-cache-base | 176 |
| :code-quality-workers | 171 |
| :wrapper-main | 170 |
| :code-quality | 157 |
| :service-registry-impl | 157 |
| :soak | 155 |
| :build-cache-base | 150 |
| :collections | 149 |
| :composite-builds | 146 |
| :distributions-jvm | 145 |
| :configuration-cache | 144 |
| :java-api-extractor | 144 |
| :signing | 143 |
| :configuration-problems-base | 142 |
| :declarative-dsl-tooling-builders | 141 |

## Tasks (651 executed, sum 1567 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 41 |
| :build-logic:integration-testing:compilePluginsBlocks | 38 |
| :build-logic:uber-plugins:compileKotlin | 31 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 30 |
| :build-logic:build-update-utils:generatePrecompiledScriptPluginAccessors | 30 |
| :build-logic:idea:compilePluginsBlocks | 20 |
| :build-logic:dependency-modules:compilePluginsBlocks | 17 |
| :build-logic:integration-testing:compileKotlin | 16 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 15 |
| :build-logic:buildquality:compileKotlin | 14 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 13 |
| :build-logic:packaging:compileKotlin | 13 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 12 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic:lifecycle:compileKotlin | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:jvm:compileKotlin | 11 |
| :build-logic:documentation:compileGroovy | 11 |
| :build-logic:profiling:compileKotlin | 11 |
| :build-logic:buildquality:compilePluginsBlocks | 11 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 10 |
| :build-logic:kotlin-dsl:compileKotlin | 10 |
| :build-logic:integration-testing-kotlin:compileKotlin | 10 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 9 |
| :build-logic:idea:generateExternalPluginSpecBuilders | 9 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 9 |
| :build-logic:build-update-utils:compileKotlin | 9 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:uber-plugins:compilePluginsBlocks | 9 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 8 |
| :build-logic-commons:build-platform-plugin:compilePluginsBlocks | 8 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 8 |
| :build-logic:cleanup:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:idea:compileKotlin | 8 |
| :build-logic:jvm:generateExternalPluginSpecBuilders | 8 |
| :build-logic:cleanup:compileJava | 8 |
| :build-logic:jvm:compilePluginsBlocks | 8 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:root-build:compileKotlin | 8 |
| :build-logic:buildquality:generateExternalPluginSpecBuilders | 8 |
