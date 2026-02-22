import net.fabricmc.loom.task.RemapJarTask

plugins {
    kotlin("jvm") version "1.8.22"
    id("fabric-loom") version "1.2.8"
}

group = "patch.plugins"
version = "3.2.0.2"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.maven.apache.org/maven2") }
}

dependencies {
    minecraft("com.mojang:minecraft:1.19.4")
    mappings("net.fabricmc:yarn:1.19.4+build.1:v2")
    modImplementation("net.fabricmc:fabric-loader:0.18.4")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.87.2+1.19.4")

    // 読み込みつつ、Jarにも同梱（include）する
    modImplementation("com.squareup.okhttp3:okhttp:4.11.0")
    include("com.squareup.okhttp3:okhttp:4.11.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    include("org.jetbrains.kotlin:kotlin-stdlib")

    implementation("org.apache.commons:commons-compress:1.26.1")
    include("org.apache.commons:commons-compress:1.26.1")
}

kotlin {
    jvmToolchain(17)
}