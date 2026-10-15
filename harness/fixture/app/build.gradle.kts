plugins { application }
repositories { mavenCentral() }
dependencies {
    implementation(project(":lib"))
    implementation(project(":util"))
    testImplementation("junit:junit:4.13.2")
}
application { mainClass = "app.Main" }
