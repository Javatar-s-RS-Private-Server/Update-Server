plugins {
    kotlin("jvm") version "1.4.32"
}

group = "com.daemonheim"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.runelite.net")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.displee:rs-cache-library:6.8")
    implementation("com.displee:disio:2.2")
    implementation("net.runelite:cache:1.7.4")
    implementation("io.netty:netty-all:4.1.63.Final")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "15"
    }
}