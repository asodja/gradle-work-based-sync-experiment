plugins {
    `java-library`
}

// The IDE whose tooling-extension jars the tasks compile against (same as the harness).
val ideaHome = providers.gradleProperty("ideaHome")
    .orElse(providers.environmentVariable("IDEA_HOME"))
    .map { file(it) }

repositories {
    mavenCentral()
}

dependencies {
    // Runs inside the Gradle daemon: the Gradle API of the measured distribution (this build's wrapper version).
    compileOnly(gradleApi())
    // IntelliJ's tooling extension (model classes, serializers, builders) and its runtime libraries.
    compileOnly(ideJars())
    testImplementation(gradleApi())
    testImplementation(ideJars())
    testImplementation("junit:junit:4.13.2")
}

fun ideJars() = ideaHome.map { home ->
    files(
        fileTree("$home/plugins/gradle/lib") { include("gradle-tooling-extension-api.jar", "gradle-tooling-extension-impl.jar") },
        fileTree("$home/plugins/Kotlin/lib") { include("kotlin-gradle-tooling.jar") },
        fileTree("$home/lib") { include("*.jar") },
    )
}.orElse(files())

java {
    // The tooling extension jars are Java 8 bytecode so they load in old daemons; the tasks only need to run in
    // Gradle 9.x daemons (JDK 17+), but stay at 17 to keep the option of older daemons.
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:-options")
}
