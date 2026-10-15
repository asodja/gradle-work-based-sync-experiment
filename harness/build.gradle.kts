plugins {
    java
    application
}

// Gradle version of the Tooling API client library. Must match or exceed the measured distribution.
val toolingApiVersion = providers.gradleProperty("toolingApiVersion").getOrElse("9.9.0-milestone-2")

// Installed IntelliJ IDEA whose Gradle and Kotlin plugin jars provide GradleModelFetchAction, the
// providers, the model classes and the serializers.
val ideaHome = providers.gradleProperty("ideaHome")
    .orElse(providers.environmentVariable("IDEA_HOME"))
    .map { file(it) }

repositories {
    maven("https://repo.gradle.org/gradle/libs-releases")
    mavenCentral()
}

dependencies {
    implementation("org.gradle:gradle-tooling-api:$toolingApiVersion")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.17")
    // IDE jars: the Gradle plugin, the Kotlin plugin and the platform libraries they depend on.
    // Order matters: plugin jars first so their classes win over anything in lib/.
    compileOnly(ideaJars())
    runtimeOnly(ideaJars())
}

fun ideaJars() = ideaHome.map { home ->
    files(
        fileTree("$home/plugins/gradle/lib") { include("*.jar") },
        fileTree("$home/plugins/gradle-java/lib") { include("*.jar") },
        fileTree("$home/plugins/Kotlin/lib") { include("*.jar") },
        fileTree("$home/lib") { include("*.jar") },
    )
}.orElse(files())

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "wbs.harness.SyncHarness"
    applicationDefaultJvmArgs = listOf("-Xmx2g")
}
