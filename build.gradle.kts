import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.nikstep"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.kafka:connect-api:3.5.1")

    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.slf4j:slf4j-simple:2.0.7")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

tasks.withType(ShadowJar::class) {
    dependencies {
        exclude(dependency("org.apache.kafka:connect-api:.*:.*"))
        exclude(dependency("org.apache.kafka:kafka-clients:.*:.*"))
        exclude(dependency("net.jpountz.lz4:.*:.*"))
        exclude(dependency("org.xerial.snappy:.*:.*"))
        exclude(dependency("org.slf4j:.*:.*"))
    }
    manifest {
        attributes(
            "Implementation-Title" to "Random Connector",
            "Implementation-Version" to 1,
        )
    }
}