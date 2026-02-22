import net.fabricmc.loom.task.RemapJarTask
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.8.22"
    id("fabric-loom") version "1.2.8"
    id("com.github.johnrengelman.shadow") version "8.1.1"
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

    modImplementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.apache.commons:commons-compress:1.26.1")
}

kotlin {
    jvmToolchain(17)
}

tasks {
    // ShadowJar: FatJar 作成
    withType<ShadowJar>().configureEach {
        archiveClassifier.set("")
        mergeServiceFiles()
        from(project.configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        finalizedBy("remapJar")
    }

    // 標準 jar は shadowJar に依存
    named("jar") {
        dependsOn("shadowJar")
    }

    // remapJar は ShadowJar を元に Fabric 用に変換
    withType<RemapJarTask>().configureEach {
        dependsOn("shadowJar")
        // ShadowJar の archiveFile を型安全に参照
        input.set(tasks.withType<ShadowJar>().map { it.archiveFile.get() })

        doFirst {
            println("Remapping FatJar for Fabric…")
        }
    }
}