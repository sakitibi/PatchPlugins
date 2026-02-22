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

    // Kotlin 用の公式アダプター（これを入れることで stdlib を自前で include する必要がなくなります）
    modImplementation("net.fabricmc:fabric-language-kotlin:1.9.5+kotlin.1.8.22")

    // OkHttp 本体
    modImplementation("com.squareup.okhttp3:okhttp:4.11.0")
    include("com.squareup.okhttp3:okhttp:4.11.0")
    // OkHttp の動作に必要な okio も明示的に include する
    include("com.squareup.okio:okio-jvm:3.2.0")

    // Commons Compress
    implementation("org.apache.commons:commons-compress:1.26.1")
    include("org.apache.commons:commons-compress:1.26.1")
}

kotlin {
    jvmToolchain(17)
}