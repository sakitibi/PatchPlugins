plugins {
    kotlin("jvm") version "1.8.22"
    id("fabric-loom") version "1.2.8"
}

group = "patch.plugins"
version = "3.0.0.3"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.maven.apache.org/maven2") }
}

dependencies {
    minecraft("com.mojang:minecraft:1.19.4")
    mappings("net.fabricmc:yarn:1.19.4+build.1:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.21")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.87.2+1.19.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.apache.commons:commons-compress:1.26.1")
}

kotlin {
    jvmToolchain(17)
}
