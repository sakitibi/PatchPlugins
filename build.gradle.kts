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
    // Minecraft / Fabric
    minecraft("com.mojang:minecraft:1.19.4")
    mappings("net.fabricmc:yarn:1.19.4+build.1:v2")
    modImplementation("net.fabricmc:fabric-loader:0.18.4")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.87.2+1.19.4")

    // OkHttp は必ず modImplementation
    modImplementation("com.squareup.okhttp3:okhttp:4.11.0")

    // Kotlin / Commons Compress
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.apache.commons:commons-compress:1.26.1")
}

kotlin {
    jvmToolchain(17)
}

tasks {
    // ShadowJar: FatJar 作成
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("") // 通常 Jar として出力
        mergeServiceFiles()

        // runtimeClasspath 全部を展開
        from(project.configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

        // Loom の remapJar に依存
        finalizedBy("remapJar")
    }

    // 標準 jar は shadowJar に依存
    named("jar") {
        dependsOn("shadowJar")
    }

    // remapJar は shadowJar を元に Fabric 用に変換
    named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
        dependsOn("shadowJar")
        // ShadowJar の出力を remapJar に入力として設定
        input.set(tasks.named("shadowJar").get().archiveFile)

        doFirst {
            println("Remapping FatJar for Fabric…")
        }
    }
}