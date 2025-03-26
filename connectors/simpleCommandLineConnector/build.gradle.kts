plugins {
    kotlin("jvm") version "2.0.21"
}

group = "com.lignting"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":server:baseServer"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

tasks.jar {
    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "Plugins-Base-Class" to "com.lignting.test.Base"
        )
    }
}