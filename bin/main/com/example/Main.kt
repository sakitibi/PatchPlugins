package com.example.patch

import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.text.Text
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.net.URI
import java.awt.Desktop

object PatchPlugins : ModInitializer {
    private val client = OkHttpClient()

    override fun onInitialize() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                CommandManager.literal("update")
                    .then(
                        CommandManager.argument("filetype", StringArgumentType.word())
                            .then(
                                CommandManager.argument("version", StringArgumentType.word())
                                    .executes { context ->
                                        val filetype = StringArgumentType.getString(context, "filetype").lowercase()
                                        val version = StringArgumentType.getString(context, "version")
                                        val source = context.source

                                        val supportedTypes = listOf("zip", "gz", "xz", "bz2", "7z")
                                        if (filetype !in supportedTypes) {
                                            source.sendError(Text.of("❌ 未対応のファイルタイプ: .$filetype"))
                                            return@executes 0
                                        }

                                        val downloadUrl = "https://github.com/sakitibi/Minecraft-Werewolf_Quest-Mods-SKNewRoles/releases/download/V$version/SKNewRolesv$version.$filetype"
                                        val tempFile = File("downloads/$version.$filetype")
                                        val worldName = "SKNewRoles" // 必要なら動的に取得も可能

                                        Thread {
                                            try {
                                                // ダウンロード
                                                downloadFile(downloadUrl, tempFile)

                                                // 展開
                                                when (filetype) {
                                                    "zip" -> extractZipToTargets(tempFile, worldName)
                                                    "7z" -> open7zUrl(downloadUrl) // .7zファイルはURLを開くだけ
                                                    else -> extractCompressed(filetype, tempFile, worldName)
                                                }

                                                source.sendMessage(Text.of("✅ $version を展開しました"))
                                            } catch (e: Exception) {
                                                source.sendError(Text.of("❌ エラー: ${e.message}"))
                                            }
                                        }.start()

                                        1
                                    }
                            )
                    )
            )
        }
    }

    private fun downloadFile(url: String, targetFile: File) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("HTTP ${response.code}")

            targetFile.parentFile.mkdirs()
            response.body?.byteStream().use { input ->
                FileOutputStream(targetFile).use { output ->
                    input?.copyTo(output)
                }
            }
        }
    }

    private fun extractZipToTargets(zipFile: File, worldName: String) {
        val resourcepacksDir = File("resourcepacks")
        val datapacksDir = File("saves/$worldName/datapacks")

        ZipInputStream(FileInputStream(zipFile)).use { zip ->
            var entry: ZipEntry? = zip.nextEntry
            while (entry != null) {
                val name = entry.name

                val targetFile = when {
                    name.startsWith("resourcepack/") -> File(resourcepacksDir, name.removePrefix("resourcepack/"))
                    name.startsWith("datapack/") -> File(datapacksDir, name.removePrefix("datapack/"))
                    else -> null
                }

                if (targetFile != null && !entry.isDirectory) {
                    targetFile.parentFile.mkdirs()
                    FileOutputStream(targetFile).use { output ->
                        zip.copyTo(output)
                    }
                }

                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
    }

    private fun extractCompressed(filetype: String, inputFile: File, worldName: String) {
        val zipFile = File(inputFile.parent, inputFile.nameWithoutExtension + ".zip")

        val inputStream: InputStream = when (filetype) {
            "gz" -> GzipCompressorInputStream(inputFile.inputStream())
            "xz" -> XZCompressorInputStream(inputFile.inputStream())
            "bz2" -> BZip2CompressorInputStream(inputFile.inputStream())
            else -> throw IllegalArgumentException("Unsupported filetype: $filetype")
        }

        zipFile.outputStream().use { output ->
            inputStream.use { it.copyTo(output) }
        }

        // ZIP 解凍処理
        extractZipToTargets(zipFile, worldName)
    }

    private fun open7zUrl(url: String) {
        // .7zファイルのURLを開くだけの処理
        try {
            val uri = URI.create(url)
            if (Desktop.isDesktopSupported()) {
                val desktop = Desktop.getDesktop()
                desktop.browse(uri)
                println("Opening 7z URL in browser: $url")
            } else {
                println("Desktop is not supported on this platform")
            }
        } catch (e: Exception) {
            println("Failed to open URL: ${e.message}")
        }
    }
}
