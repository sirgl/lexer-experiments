import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.60"
}

group = "sirgl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        setUrl( "http://dl.bintray.com/kotlin/kotlinx")
    }
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.1")
//    compile("org.junit:junit-4.12")
    testCompile("org.junit.jupiter:junit-jupiter-api:5.0.3")
    testCompile("org.junit.jupiter:junit-jupiter-engine:5.0.3")
    testCompile("org.junit.platform:junit-platform-launcher:1.0.3")
    testCompile("org.junit.platform:junit-platform-runner:1.0.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}