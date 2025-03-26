/**
 * High-performance Gradle task for generating resource bridges from Android resource files.
 *
 * This task efficiently processes Android resource files and generates optimized Kotlin code
 * for resource access. It supports incremental builds and caching for better performance.
 *
 * Key features:
 * - Incremental resource processing
 * - Efficient file handling
 * - Optimized code generation
 * - Memory-efficient processing
 * - Comprehensive error handling
 *
 * Performance optimizations:
 * - Efficient file reading
 * - Optimized string operations
 * - Minimized object creation
 * - Safe resource handling
 * - Fast code generation
 *
 * Usage example:
 * ```kotlin
 * tasks.register<GenerateResourcesTask>("generateResources") {
 *     valuesFiles.from(fileTree("src/main/res/values"))
 *     pluginVersion.set("1.0.0")
 *     packageName.set("com.example.app")
 *     outputDir.set(layout.buildDirectory.dir("generated/kt-resources"))
 * }
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.plugin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File
import java.nio.file.Files
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.EnumMap
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Task for generating resource bridges from Android resource files.
 */
@CacheableTask
abstract class GenerateResourcesTask @Inject constructor() : DefaultTask() {

    companion object {
        private const val BUFFER_SIZE = 8192
        private const val HEX_FORMAT = "%02x"
        private const val HASH_ALGORITHM = "SHA-256"
        private const val GENERATED_FILE_NAME = "ResourcesBridge.kt"
        private const val GENERATED_PACKAGE = "com.voyager.resources"

        private val LOGGER: Logger = Logging.getLogger(GenerateResourcesTask::class.java)
        private val json = Json { prettyPrint = true }

        // Pre-compiled patterns for better performance
        private val NAME_PATTERNS =
            ResourceType.entries.filterNot { it.isFileBased }.associateWith { type ->
                    Pattern.compile(
                        """<${type.tag}[^>]+?name\s*=\s*["']([^"']+)["']""", Pattern.DOTALL
                    )
                }
    }

    /**
     * Collection of resource files to process.
     */
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:IgnoreEmptyDirectories
    @get:Incremental
    @get:InputFiles
    abstract val valuesFiles: ConfigurableFileCollection

    /**
     * Version of the plugin for cache invalidation.
     */
    @get:Input
    abstract val pluginVersion: Property<String>

    /**
     * Package name for the generated code.
     */
    @get:Input
    abstract val packageName: Property<String>

    /**
     * Output directory for generated files.
     */
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    /**
     * File for storing resource hashes.
     */
    private val hashFile: File by lazy {
        project.layout.buildDirectory.file("generated/kt-resources/ResourcesBridge.hash")
            .get().asFile.also { it.parentFile.mkdirs() }
    }

    /**
     * File for storing resource metadata.
     */
    private val metadataFile: File by lazy {
        project.layout.buildDirectory.file("generated/kt-resources/resources-metadata.json")
            .get().asFile.also { it.parentFile.mkdirs() }
    }

    /**
     * Enumeration of supported resource types.
     */
    private enum class ResourceType(val tag: String) {
        COLOR("color"), STRING("string"), STYLE("style"), DIMEN("dimen"), ANIM("anim"), DRAWABLE("drawable"), BOOL(
            "bool"
        ),
        INTEGER("integer"), ARRAY("array"), PLURALS("plurals"), MENU("menu"), TRANSITION("transition"), FONT(
            "font"
        ),
        RAW("raw"), XML("xml"), INTERPOLATOR("interpolator"), MIPMAP("mipmap"), ATTR("attr");

        val isFileBased: Boolean
            get() = this in listOf(ANIM, DRAWABLE, RAW, XML, INTERPOLATOR, MIPMAP)
    }

    /**
     * Main task action for resource generation.
     */
    @TaskAction
    fun generateBridge(inputChanges: InputChanges) {
        LOGGER.lifecycle("Input files detected: ${valuesFiles.files}")

        if (valuesFiles.files.isEmpty()) {
            LOGGER.info("No input files found. Skipping ResourcesBridge generation.")
            return
        }

        if (!inputChanges.isIncremental || !isUpToDate()) {
            regenerateAll()
        } else {
            performIncrementalGeneration(inputChanges)
        }
    }

    /**
     * Performs incremental resource generation.
     */
    private fun performIncrementalGeneration(inputChanges: InputChanges) {
        val previousResources = loadPreviousResourcesMetadata() ?: emptyMap()
        val resources = extractResources(inputChanges)

        if (resources != previousResources) {
            generateKotlinCode(resources)
            saveResourcesMetadata(resources)
            saveHash()
        }
    }

    /**
     * Regenerates all resources.
     */
    private fun regenerateAll() {
        val resources = extractAllResources()
        generateKotlinCode(resources)
        saveResourcesMetadata(resources)
        saveHash()
    }

    /**
     * Checks if the task is up to date.
     */
    private fun isUpToDate(): Boolean {
        val pkgPath = GENERATED_PACKAGE.replace('.', File.separatorChar)
        val generatedFile = File(outputDir.get().asFile, "$pkgPath/$GENERATED_FILE_NAME")

        return generatedFile.exists() && hashFile.exists() && valuesFiles.files.all { it.exists() } && hashFile.readText(
            Charsets.UTF_8
        ) == calculateHash()
    }

    /**
     * Calculates hash of input files.
     */
    private fun calculateHash(): String {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        digest.update(pluginVersion.get().toByteArray(Charsets.UTF_8))

        valuesFiles.files.forEach { file ->
            Files.newInputStream(file.toPath()).use { input ->
                DigestInputStream(input, digest).use {
                    while (it.read(ByteArray(BUFFER_SIZE)) != -1) { /* reading Digests*/
                    }
                }
            }
        }

        return digest.digest().joinToString("") { HEX_FORMAT.format(it) }
    }

    /**
     * Extracts resources from all files.
     */
    private fun extractAllResources(): Map<ResourceType, Set<String>> =
        EnumMap<ResourceType, MutableSet<String>>(ResourceType::class.java).apply {
            ResourceType.entries.forEach { put(it, LinkedHashSet()) }
            valuesFiles.files.forEach { extractResourcesFromFile(it, this) }
        }

    /**
     * Extracts resources from changed files.
     */
    private fun extractResources(inputChanges: InputChanges): EnumMap<ResourceType, MutableSet<String>> =
        EnumMap<ResourceType, MutableSet<String>>(ResourceType::class.java).apply {
            ResourceType.entries.forEach { put(it, mutableSetOf()) }
            inputChanges.getFileChanges(valuesFiles).forEach { change ->
                extractResourcesFromFile(change.file, this)
            }
        }

    /**
     * Extracts resources from a single file.
     */
    private fun extractResourcesFromFile(
        file: File,
        resources: EnumMap<ResourceType, MutableSet<String>>,
    ) {
        var found = false
        file.useLines { lines ->
            lines.forEach { line ->
                NAME_PATTERNS.forEach { (type, pattern) ->
                    pattern.matcher(line).takeIf { it.find() }?.group(1)?.replace(".", "_")?.let {
                        resources[type]?.add(it)
                        found = true
                    }
                }
            }
        }

        if (!found) {
            ResourceType.entries.filter { it.isFileBased }.forEach { type ->
                if (file.extension == type.tag) {
                    resources[type]?.add(file.nameWithoutExtension.replace(".", "_"))
                }
            }
        }
    }

    /**
     * Generates Kotlin code for resource access.
     */
    private fun generateKotlinCode(resources: Map<ResourceType, Set<String>>) {
        val getResIdBody = resources.entries.joinToString("\n") { (type, names) ->
            "\"${type.tag}\" -> when (name) {" + names.joinToString("\n") { "\"$it\" -> R.${type.tag}.$it" } + "\nelse -> -1 }"
        }

        val classBuilder = TypeSpec.classBuilder("ResourcesBridge")
            .addSuperinterface(ClassName("com.voyager.utils.interfaces", "ResourcesProvider"))
            .addFunction(
                FunSpec.builder("getResId").addModifiers(KModifier.OVERRIDE)
                    .addParameter("type", String::class).addParameter("name", String::class)
                    .returns(Int::class)
                    .addStatement("return when (type) {\n$getResIdBody\nelse -> -1 }").build()
            )

        FileSpec.builder(GENERATED_PACKAGE, GENERATED_FILE_NAME.removeSuffix(".kt"))
            .addKotlinDefaultImports(false).addImport(packageName.get(), "R")
            .addType(classBuilder.build()).build().writeTo(outputDir.get().asFile)
    }

    /**
     * Loads previous resource metadata.
     */
    private fun loadPreviousResourcesMetadata(): Map<ResourceType, Set<String>>? =
        if (metadataFile.exists()) json.decodeFromString(metadataFile.readText()) else null

    /**
     * Saves resource metadata.
     */
    private fun saveResourcesMetadata(resources: Map<ResourceType, Set<String>>) {
        metadataFile.writeText(json.encodeToString(resources))
    }

    /**
     * Saves hash of input files.
     */
    private fun saveHash() {
        hashFile.writeText(calculateHash())
    }
}
