plugins {
    `java-library`
}

// The IDE whose plugin and platform jars the plugin compiles against (same as the harness and the prototype).
val ideaHome = providers.gradleProperty("ideaHome")
    .orElse(providers.environmentVariable("IDEA_HOME"))
    .map { file(it) }

val prototypeJar = files("../prototype/build/libs/ide-model-tasks.jar")

// plugins/gradle/lib up to 2025.3, plugins/gradle-plugin/lib from 2026.2
fun ideJars() = ideaHome.map { home ->
    files(
        fileTree("$home/plugins/gradle/lib") { include("*.jar") },
        fileTree("$home/plugins/gradle-plugin/lib") { include("*.jar") },
        fileTree("$home/lib") { include("*.jar") },
    )
}.orElse(files())

// The IDE classes replaced in the installed copy (DECISIONS D21, D23, D24): compiled alone against the IDE jars.
// One source dir per IDE branch: src/patched/java for 253 (2025.3), src/patched-<branch>/java otherwise
// (-PideaBranch=262 for 2026.2.2, the Mac kit).
val ideaBranch = providers.gradleProperty("ideaBranch").orElse("253")
val patched by sourceSets.creating {
    java.setSrcDirs(listOf(ideaBranch.map { if (it == "253") "src/patched/java" else "src/patched-$it/java" }))
}

dependencies {
    compileOnly(ideJars())
    compileOnly(prototypeJar)
    "patchedCompileOnly"(ideJars())
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
    options.compilerArgs.add("-Xlint:-options")
}

// The IDE's own sources use the language level of its runtime: JBR 21 for 253, JBR 25 for 262.
tasks.named<JavaCompile>("compilePatchedJava") {
    options.release = ideaBranch.map { if (it == "253") 21 else 25 }
}

// build/plugin/wbs-task-transport/lib: the plugin jar and the prototype jar (a separate jar, so the IDE puts it on
// the Gradle init script classpath by PathManager.getJarPathForClass).
val preparePlugin by tasks.registering(Sync::class) {
    from(tasks.jar)
    from(prototypeJar)
    into(layout.buildDirectory.dir("plugin/wbs-task-transport/lib"))
}

tasks.register<Copy>("copyPatchedClasses") {
    from(patched.output.classesDirs)
    into(layout.buildDirectory.dir("patched-classes"))
}

tasks.named("assemble") {
    dependsOn(preparePlugin, "copyPatchedClasses")
}
