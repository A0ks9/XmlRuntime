package com.dynamic.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.Incremental
import java.io.File
import java.nio.file.Files
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.EnumSet
import java.util.regex.Pattern

@CacheableTask
abstract class GenerateResourcesTask : DefaultTask() {

    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:IgnoreEmptyDirectories
    @get:Incremental
    @get:InputFiles
    abstract val valuesFiles: ConfigurableFileCollection

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    private val hashFile by lazy {
        project.layout.buildDirectory.file("generated/kt-resources/com/dynamic/resources/ResourcesBridge.hash")
            .get().asFile
    }

    companion object {
        private val RESOURCE_TYPES =
            EnumSet.of(ResourceType.COLOR, ResourceType.STRING, ResourceType.STYLE)
        private val COLOR_PATTERN = Pattern.compile("""<color name="(.*?)">""")
        private val STRING_PATTERN = Pattern.compile("""<string name="(.*?)">""")
        private val STYLE_PATTERN = Pattern.compile("""<style name="(.*?)">""")
        private const val HEX_FORMAT = "%02x"
        private const val EMPTY_STRING = ""
    }

    private enum class ResourceType {
        COLOR, STRING, STYLE
    }

    @TaskAction
    fun generateBridge() {
        if (valuesFiles.files.isEmpty()) {
            println("No resource files specified.")
            return
        }

        if (isUpToDate()) {
            println("ResourcesBridge is up-to-date.")
            return
        }

        val resources = extractAllResources()
        generateKotlinCode(resources)
        println("ResourcesBridge generated.")
    }

    private fun isUpToDate(): Boolean {
        if (!outputFile.get().asFile.exists() || !hashFile.exists()) return false
        return hashFile.readText() == calculateHash()
    }

    private fun calculateHash(): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(8192)
        valuesFiles.files.filter(File::exists).forEach { file ->
            Files.newInputStream(file.toPath()).use { fis ->
                DigestInputStream(fis, digest).use { dis ->
                    while (dis.read(buffer) != -1) { /* DigestInputStream updates digest */
                    }
                }
            }
        }
        return digest.digest().joinToString("") { HEX_FORMAT.format(it) }
    }

    private fun extractAllResources(): Map<ResourceType, Set<String>> {
        val resourcesMap = mutableMapOf<ResourceType, MutableSet<String>>()
        RESOURCE_TYPES.forEach { resourcesMap[it] = HashSet() }

        val colorSet = resourcesMap[ResourceType.COLOR]!!
        val stringSet = resourcesMap[ResourceType.STRING]!!
        val styleSet = resourcesMap[ResourceType.STYLE]!!

        val matcherColor = COLOR_PATTERN.matcher(EMPTY_STRING)
        val matcherString = STRING_PATTERN.matcher(EMPTY_STRING)
        val matcherStyle = STYLE_PATTERN.matcher(EMPTY_STRING)

        valuesFiles.files.forEach { file ->
            if (!file.exists()) return@forEach
            val content = Files.readString(file.toPath())

            matcherColor.reset(content)
            matcherColor.results().forEach { colorSet.add(it.group(1)) }

            matcherString.reset(content)
            matcherString.results().forEach { stringSet.add(it.group(1)) }

            matcherStyle.reset(content)
            matcherStyle.results().forEach { styleSet.add(it.group(1)) }
        }
        return resourcesMap
    }

    private fun generateKotlinCode(resources: Map<ResourceType, Set<String>>) {
        val colors = resources[ResourceType.COLOR] ?: emptySet()
        val strings = resources[ResourceType.STRING] ?: emptySet()
        val styles = resources[ResourceType.STYLE] ?: emptySet()

        val kotlinCode =
            buildString(500 + colors.size * 50 + strings.size * 50 + styles.size * 50) {
                appendLine("@file:JvmName(\"ResourcesBridge\")")
                appendLine("package com.dynamic.resources")
                appendLine()
                appendLine("import android.content.Context")
                appendLine("import androidx.core.content.ContextCompat")
                appendLine("import ${packageName.get()}.R")
                appendLine()
                appendLine("fun getColor(context: Context, name: String): Int = when (name) {")
                colors.optimizedForEach { name -> appendLine("    \"$name\" -> ContextCompat.getColor(context, R.color.$name)") }
                appendLine("    else -> 0")
                appendLine("}")
                appendLine()
                appendLine("fun getString(context: Context, name: String): String = when (name) {")
                strings.optimizedForEach { name -> appendLine("    \"$name\" -> context.getString(context, R.string.$name)") }
                appendLine("    else -> \"\"")
                appendLine("}")
                appendLine()
                appendLine("fun getStyle(name: String): Int = when (name) {")
                styles.optimizedForEach { name -> appendLine("    \"$name\" -> R.style.$name") }
                appendLine("    else -> 0")
                appendLine("}")
            }

        outputFile.get().asFile.parentFile?.mkdirs()
        Files.writeString(outputFile.get().asFile.toPath(), kotlinCode)
        Files.writeString(hashFile.toPath(), calculateHash())
    }

    private inline fun <T> Set<T>.optimizedForEach(action: (T) -> Unit) {
        if (this is HashSet) {
            val iterator = this.iterator()
            while (iterator.hasNext()) {
                action(iterator.next())
            }
        } else {
            forEach(action)
        }
    }
}