# Model inventory

Owner "IJ" = IntelliJ Community (`plugins/gradle/tooling-extension-impl` unless noted), "Gradle" = Gradle
distribution. Phase = IntelliJ fetch phase. Plan: `task` (port in M3), `task-hard` (M2/M3 hard group),
`keep` (stays a model), `skip` (not exercised by gradle/gradle or out of scope).

| Model | Owner, builder | Scope / phase | Plan | Notes |
|---|---|---|---|---|
| GradleDslBaseScriptModel | Gradle, `GradleDslBaseScriptModelBuilder` | default / base script | keep | pure function of the distribution; 2.9 s was warm-up, verify |
| GradleBuild | Gradle, `GradleBuildBuilder` | default / projects loaded | keep | settings evaluation; the single biggest cost on small builds |
| BuildEnvironment | Gradle | before the action | keep | |
| GradleDaemonClassLoaderModel | IJ (serializer bootstrap) | default | keep | internal plumbing |
| GradleTurnOffDefaultTasksRequest | IJ, `GradleTurnOffDefaultTasksModelBuilder` | default / projects loaded | keep | side effect only; with tasks requested it is moot |
| KotlinDslScriptAdditionalTask | IJ Kotlin, `KotlinDslScriptTaskModelBuilder` | build / projects loaded | keep | adds a prepare task; precedent for transport |
| PrepareKotlinIdeImportTaskModel | IJ Kotlin, `PrepareKotlinIdeaImportTaskModelBuilder` | project / projects loaded | keep | same |
| ExternalProject | IJ, `GradleExternalProjectModelBuilder` | project / PROJECT_MODEL | task-hard | main project tree model |
| GradleTaskWarmUpRequest | IJ, `GradleTaskWarmUpService` | project / SOURCE_SET | task | realizes tasks; may become unnecessary |
| GradleSourceSetModel | IJ, `GradleSourceSetModelBuilder` (~880 lines) | project / SOURCE_SET | task-hard (M2) | shares artifact index with dependency model |
| GradleDependencyDownloadPolicy | IJ, `GradleDependencyDownloadPolicyBuilder` | project / DEPENDENCY | task | sources/javadoc policy |
| GradleSourceSetArtifactBuildRequest | IJ, `GradleSourceSetArtifactIndexBuilder` | project / DEPENDENCY | task-hard | writes the shared index |
| GradleSourceSetDependencyModel | IJ, `GradleSourceSetDependencyModelBuilder` | project / DEPENDENCY | task-hard | resolves configurations; biggest per-project cost on real builds |
| KotlinDslScriptsModel | Gradle | build / SCRIPT | keep | script classpaths; already task-assisted on Gradle side |
| GradleBuildScriptClasspathModel | IJ, `GradleBuildScriptClasspathModelBuilder` | project / SCRIPT | task | |
| KotlinMPPGradleModel | IJ Kotlin, `KotlinMPPGradleModelBuilder` | project / ADDITIONAL | skip | no MPP in gradle/gradle |
| KotlinGradleModel | IJ Kotlin, `KotlinGradleModelBuilder` | project / ADDITIONAL | task-hard | compiler args per source set; Android-aware provider |
| DeclarativeSchemaModel | Gradle | build / ADDITIONAL | keep | |
| ProjectDependencies | IJ, `GradleDependencyGraphModelBuilder` | project / ADDITIONAL | task-hard | dependency graph for the IDE tool window |
| AnnotationProcessingModel | IJ, `AnnotationProcessingModelBuilder` | project / ADDITIONAL | task | |
| RepositoryModels | IJ, `MavenRepositoriesModelBuilder` | project / ADDITIONAL | task | |
| JavaGradleManifestModel | IJ, `JavaGradleManifestModelBuilder` | project / ADDITIONAL | task | |
| ProjectRepositoriesModel | IJ, `ProjectRepositoriesModelBuilder` | project / ADDITIONAL | task | |
| EarConfiguration, WebConfiguration, KtorApplicationGradleModel, MnApplicationGradleModel | Ultimate | project / ADDITIONAL | skip | not in Community |
| KaptGradleModel | IJ Kotlin, `KaptModelBuilderService` | project / ADDITIONAL | task | |
| ComposeResourcesModel | IJ Compose | project / ADDITIONAL | skip | not exercised |
| GradleExtensions | IJ, `ProjectExtensionsDataBuilderImpl` | project / ADDITIONAL | task | extensions for script completion |
| ExternalTestsModel | IJ, `ExternalTestsModelBuilderImpl` | project / ADDITIONAL | task | |
| IntelliJProjectSettings, IntelliJSettings | IJ, `IntelliJ*SettingsBuilder` | project / ADDITIONAL | task | idea plugin settings |
| DependencyAccessorsModel | IJ, `DependencyAccessorsModelBuilder` | project / ADDITIONAL | task | |
| VersionCatalogsModel | IJ, `VersionCatalogsModelBuilder` | project / ADDITIONAL | task | |
| IdeaProject | Gradle, `IdeaModelBuilder` | build / ADDITIONAL | keep | ~90 ms; IntelliJ iterates its modules as the backbone |
| GradleTaskModel | IJ, `GradleTaskModelBuilder` | project / ADDITIONAL | task | |

Source links (IntelliJ master): builders under
`plugins/gradle/tooling-extension-impl/src/com/intellij/gradle/toolingExtension/impl/model/*/` and
`plugins/gradle/tooling-extension-impl/src/org/jetbrains/plugins/gradle/tooling/builder/`; Kotlin ones under
`plugins/kotlin/gradle/gradle-tooling/impl/src/org/jetbrains/kotlin/idea/gradleTooling/`. Registration files:
`.../tooling-extension-impl/resources/META-INF/services/org.jetbrains.plugins.gradle.tooling.ModelBuilderService`
and the Kotlin equivalent. Requesters: `CommonGradleProjectResolverExtension.java`,
`plugins/gradle/java/src/service/project/JavaGradleProjectResolver.java` (+ manifest, repositories
resolvers), `plugins/kotlin/gradle/gradle-java/src/org/jetbrains/kotlin/idea/gradleJava/configuration/*Resolver*.kt`,
`plugins/kotlin/gradle/scripting/src/org/jetbrains/kotlin/gradle/scripting/importing/KotlinDslScriptModelResolver.kt`.

Update the M0 cost columns here once the gradle/gradle baseline exists.
