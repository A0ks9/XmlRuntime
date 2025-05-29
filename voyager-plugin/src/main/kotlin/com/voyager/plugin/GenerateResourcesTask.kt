/**
 * This file defines the `GenerateResourcesTask`, a Gradle task responsible for parsing
 * Android resource XML files (specifically from `res/values/`) and generating a Kotlin
 * source file (`ResourcesBridge.kt`). This generated file provides a programmatic way
 * to access resource IDs, which can be utilized by the Voyager framework for dynamic UI
 * rendering or other resource-aware operations.
 *
 * The task is designed with performance in mind, featuring:
 * - **Caching:** Annotated with `@CacheableTask`, allowing Gradle to cache outputs and skip
 *   execution if inputs/outputs are unchanged.
 * - **Incremental Builds:** Annotated with `@Incremental` for input file properties, enabling
 *   Gradle to provide information about changed files (`InputChanges`) so the task can
 *   potentially reprocess only what's necessary.
 * - **Efficient Parsing:** Uses `XmlPullParser` for processing XML resource files, which is
 *   generally more efficient than regex for structured XML.
 * - **Optimized Code Generation:** Uses KotlinPoet for generating the Kotlin source file.
 *
 * @see ResourcesPlugin Where this task is registered.
 * @see ResourcesExtension For configuring this task's inputs.
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.plugin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
// kotlinx.serialization.json.Json is removed as metadata file is removed
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
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.util.EnumMap
import javax.inject.Inject


/**
 * Gradle task to parse Android resource XML files (from `res/values/`) and generate
 * a Kotlin `ResourcesBridge` class. This class provides a typed way to access
 * resource IDs, potentially for use in dynamic UI generation or other framework needs.
 *
 * This task is `@CacheableTask` and supports incremental builds by Gradle's mechanisms.
 * It parses XML resource files from `res/values/` to generate a `ResourcesBridge.kt` file,
 * which provides programmatic access to resource IDs.
 *
 * The generated `ResourcesBridge` class implements the `com.voyager.utils.interfaces.ResourcesProvider` interface.
 *
 * @property valuesFiles Collection of input Android resource XML files (e.g., `strings.xml`, `colors.xml`).
 *                      Annotated as `@InputFiles`, `@Incremental`, and path-sensitive for Gradle.
 * @property pluginVersion Version of the plugin, used as an `@Input` to help invalidate Gradle's cache
 *                         when plugin logic changes.
 * @property packageName The application's package name (e.g., "com.example.app"), used to generate
 *                       correct imports for the `R` class. Marked as `@Input`.
 * @property outputDir The directory where the generated `ResourcesBridge.kt` file will be written.
 *                     Annotated as `@OutputDirectory`.
 */
@CacheableTask
abstract class GenerateResourcesTask @Inject constructor() : DefaultTask() {

    /**
     * Companion object holding constants and the logger for this task.
     */
    companion object {
        /** The name of the Kotlin file that will be generated (e.g., "ResourcesBridge.kt"). */
        private const val GENERATED_FILE_NAME = "ResourcesBridge.kt"
        /** The package name for the generated Kotlin file (e.g., "com.voyager.resources"). */
        private const val GENERATED_PACKAGE = "com.voyager.resources"
        /** Logger instance for this task, used for outputting information during task execution. */
        private val LOGGER: Logger = Logging.getLogger(GenerateResourcesTask::class.java)
    }

    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:IgnoreEmptyDirectories
    @get:Incremental
    @get:InputFiles
    abstract val valuesFiles: ConfigurableFileCollection

    @get:Input
    abstract val pluginVersion: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    /**
     * Defines the types of Android resources that this task will look for within XML files
     * in the `res/values` directory. Each enum entry corresponds to an XML tag name.
     *
     * The `isFileBased` property is currently suppressed as unused because this task focuses on
     * parsing resource declarations within `values/*.xml` files, not on discovering resources
     * based on their filenames in other `res` subdirectories (like `drawable` or `layout`).
     */
    private enum class ResourceType(
        /** The XML tag name associated with this resource type (e.g., "string", "color"). */
        val tag: String
    ) {
        COLOR("color"),
        STRING("string"),
        STYLE("style"),
        DIMEN("dimen"),
        BOOL("bool"),
        INTEGER("integer"),
        ARRAY("array"), // For <string-array>, <integer-array>, etc.
        PLURALS("plurals"),
        ATTR("attr"); // For attributes defined in values/attrs.xml

        /**
         * Indicates if the resource type is typically defined by a standalone file in directories
         * like `res/drawable/` or `res/layout/`. This is currently not directly used by this task's
         * parsing logic, which focuses on XML tags within `values` files.
         */
        @Suppress("unused")
        val isFileBased: Boolean = false
    }

    /**
     * The main Gradle task action. This method is executed when the task runs.
     * It handles input validation, determines if a full or incremental build is necessary,
     * extracts resource information, and generates the `ResourcesBridge.kt` file.
     *
     * @param inputChanges Provides information from Gradle about changes to input files,
     *                     allowing the task to perform incremental processing.
     */
    @TaskAction
    fun generateBridgeAction(inputChanges: InputChanges) {
        LOGGER.info("GenerateResourcesTask executing. Incremental: ${inputChanges.isIncremental}")
        val targetGeneratedFile = outputDir.get().asFile
            .resolve(GENERATED_PACKAGE.replace('.', File.separatorChar))
            .resolve(GENERATED_FILE_NAME)
        LOGGER.debug("Target generated file path: ${targetGeneratedFile.absolutePath}")

        if (handleEmptyInputs(targetGeneratedFile)) return

        val rebuildReason = determineRebuildReason(inputChanges, targetGeneratedFile)
        if (rebuildReason != null) {
            // Reason already logged by determineRebuildReason if it's a specific file event
            performFullGeneration(targetGeneratedFile, rebuildReason)
        } else if (shouldSkipIncremental(inputChanges)) {
            LOGGER.lifecycle("No input file changes requiring regeneration were detected by Gradle. Skipping ResourcesBridge generation.")
            return
        } else {
            // This case implies it's an incremental build, no files were removed, output exists,
            // and there were ADDED/MODIFIED files.
            LOGGER.lifecycle("Incremental changes (ADDED/MODIFIED files) detected. Regenerating ResourcesBridge.")
            performFullGeneration(targetGeneratedFile, "Incremental update due to ADDED/MODIFIED files")
        }
    }

    /**
     * Handles the case where input [valuesFiles] are empty.
     * If empty, it logs a message and deletes any pre-existing generated file.
     * @return `true` if inputs were empty and processing should stop, `false` otherwise.
     */
    private fun handleEmptyInputs(targetGeneratedFile: File): Boolean {
        if (valuesFiles.files.isEmpty()) {
            LOGGER.info("No input resource XML files found. Skipping ResourcesBridge generation.")
            if (targetGeneratedFile.exists()) {
                LOGGER.info("Deleting previously generated file as there are no inputs: ${targetGeneratedFile.path}")
                targetGeneratedFile.parentFile.deleteRecursively()
            }
            return true
        }
        return false
    }

    /**
     * Determines if a full rebuild is necessary and returns the reason.
     * @return A string reason if full rebuild is needed, `null` otherwise.
     */
    private fun determineRebuildReason(inputChanges: InputChanges, targetGeneratedFile: File): String? {
        if (!inputChanges.isIncremental) {
            LOGGER.info("Build is not incremental.")
            return "Full rebuild: Not an incremental build."
        }
        if (!targetGeneratedFile.exists()) {
            LOGGER.info("Output file does not exist: ${targetGeneratedFile.name}")
            return "Full rebuild: Output file ${targetGeneratedFile.name} does not exist."
        }

        for (change in inputChanges.getFileChanges(valuesFiles)) {
            LOGGER.debug("Processing change: File ${change.normalizedPath}, Type: ${change.changeType}")
            if (change.changeType == ChangeType.REMOVED) {
                LOGGER.lifecycle("Input file removed ('${change.normalizedPath}'), triggering full rebuild.")
                return "Full rebuild: Input file removed: ${change.normalizedPath}"
            }
        }
        return null // No condition met that *forces* a full rebuild.
    }

    /**
     * Checks if an incremental build should be skipped.
     * This is true if Gradle reports incremental changes, but none of them are actual ADDED or MODIFIED files.
     */
    private fun shouldSkipIncremental(inputChanges: InputChanges): Boolean {
        // This method assumes determineRebuildReason already confirmed it's an incremental run
        // and no files were REMOVED, and the output file exists.
        return !inputChanges.getFileChanges(valuesFiles).any {
            it.changeType == ChangeType.ADDED || it.changeType == ChangeType.MODIFIED
        }
    }

    /**
     * Performs a full generation of the ResourcesBridge file.
     * This involves cleaning the output directory, extracting all resources, and generating the Kotlin code.
     */
    private fun performFullGeneration(targetGeneratedFile: File, reason: String) {
        LOGGER.lifecycle("Performing full regeneration of ResourcesBridge: $reason")
        try {
            val parentDir = targetGeneratedFile.parentFile
            if (parentDir.exists()) {
                LOGGER.debug("Cleaning output directory: ${parentDir.absolutePath}")
                parentDir.deleteRecursively()
            }
            LOGGER.debug("Creating output directory: ${outputDir.get().asFile.absolutePath}")
            outputDir.get().asFile.mkdirs()
        } catch (e: Exception) {
            LOGGER.error("Error preparing output directory for ResourcesBridge generation.", e)
            // Potentially rethrow or handle more gracefully if directory setup is critical
            return // Stop if output dir can't be prepared
        }

        val resources = extractAllResources()
        val totalResourcesCount = resources.values.sumOf { it.size }
        LOGGER.info("Extracted $totalResourcesCount total resource entries.")

        if (totalResourcesCount == 0) {
            LOGGER.warn("No resources found after parsing all input files. ResourcesBridge will not be generated.")
            // Ensure no stale file remains if it existed before cleaning
            if (targetGeneratedFile.exists()) targetGeneratedFile.delete()
            return
        }
        generateKotlinCode(resources, targetGeneratedFile)
    }


    /**
     * Extracts resource names from all valid XML input files specified in [valuesFiles].
     * It uses an [XmlPullParser] for efficient and correct XML parsing.
     *
     * @return A map where keys are [ResourceType]s and values are sets of resource names (String).
     */
    private fun extractAllResources(): Map<ResourceType, Set<String>> {
        val resourcesMap = EnumMap<ResourceType, MutableSet<String>>(ResourceType::class.java)
        // Initialize map with empty sets for all known resource types to ensure all keys exist.
        ResourceType.entries.forEach { resourcesMap[it] = LinkedHashSet<String>() }

        val filesToProcess = valuesFiles.asFileTree.filter { it.isFile && it.extension.equals("xml", ignoreCase = true) }
        LOGGER.info("Found ${filesToProcess.files.size} XML files to process for resources.")

        filesToProcess.forEach { file ->
            LOGGER.debug("Extracting resources from: ${file.path}")
            extractResourcesFromSingleFile(file, resourcesMap)
        }
        return resourcesMap
    }

    /**
     * Extracts resource names from a single XML file using [XmlPullParser].
     * This method is called by [extractAllResources] for each XML file.
     *
     * @param file The XML [File] to parse.
     * @param resources The [EnumMap] (mapping [ResourceType] to a mutable set of resource names)
     *                  to be populated with the extracted resource information.
     */
    private fun extractResourcesFromSingleFile(
        file: File,
        resources: EnumMap<ResourceType, MutableSet<String>>,
    ) {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false // Typically false for simple res/values XML
            val parser = factory.newPullParser()

            var resourceFoundInFile = false
            file.inputStream().use { fis ->
                parser.setInput(fis, null) // Autodetect encoding from XML prolog or use UTF-8
                var eventType = parser.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        val tagName = parser.name
                        val resourceType = ResourceType.entries.firstOrNull { it.tag.equals(tagName, ignoreCase = true) }
                        if (resourceType != null) {
                            val nameValue = parser.getAttributeValue(null, "name")
                            if (!nameValue.isNullOrBlank()) {
                                resources[resourceType]?.add(nameValue.replace(".", "_"))
                                resourceFoundInFile = true
                            } else {
                                LOGGER.debug("Found resource tag <$tagName> without a 'name' attribute in ${file.name} at line ${parser.lineNumber}")
                            }
                        }
                    }
                    eventType = parser.next()
                }
            }
            if (!resourceFoundInFile) {
                LOGGER.debug("No target resource declarations (e.g., <string name=...>) found in file: ${file.name}")
            }
        } catch (e: Exception) {
            LOGGER.error("Failed to parse XML resource file: ${file.absolutePath}. Skipping this file. Error: ${e.message}", e)
        }
    }


    /**
     * Generates the `ResourcesBridge.kt` Kotlin file using KotlinPoet.
     * The generated `ResourcesBridge` class will implement the `com.voyager.utils.interfaces.ResourcesProvider` interface.
     * Its `getResId` method will contain a `when` statement to map string resource types and names
     * to their corresponding integer IDs from the application's `R` class.
     *
     * @param resources A map where keys are [ResourceType] enums and values are sets of resource name strings
     *                  that were extracted from the XML files.
     * @param generatedFile The [File] object representing the target path for the generated Kotlin source file.
     *                      The necessary parent directories for this file will be created if they don't exist.
     */
    private fun generateKotlinCode(resources: Map<ResourceType, Set<String>>, generatedFile: File) {
        val totalResourcesCount = resources.values.sumOf { it.size }
        LOGGER.info("Generating Kotlin code for ResourcesBridge with $totalResourcesCount total resource entries into: ${generatedFile.absolutePath}")

        val getResIdBodyFormat = StringBuilder()
        getResIdBodyFormat.append("when (type) {\n")

        // Iterate over sorted resource types for consistent output order
        resources.entries.sortedBy { it.key.tag }.forEach { (type, names) ->
            if (names.isNotEmpty()) {
                getResIdBodyFormat.append("    %S -> when (name) {\n") // %S for String literal (type.tag)
                // Iterate over sorted names for consistent output order within each type
                names.sorted().forEach { name ->
                    // Example: "string_app_name" -> R.string.app_name
                    getResIdBodyFormat.append("        %S -> R.${type.tag}.${name}\n") // %S for name
                }
                getResIdBodyFormat.append("        else -> -1\n") // Default for unknown name in this type
                getResIdBodyFormat.append("    }\n")
            }
        }
        getResIdBodyFormat.append("    else -> -1\n") // Default for unknown type
        getResIdBodyFormat.append("}")

        // Prepare arguments for the format string in the correct order
        val formatArgs = mutableListOf<String>()
        resources.entries.sortedBy { it.key.tag }.forEach { (type, names) ->
            if (names.isNotEmpty()) {
                formatArgs.add(type.tag) // Argument for type.tag
                names.sorted().forEach { name ->
                    formatArgs.add(name) // Argument for resource name
                }
            }
        }
        
        // Define ClassName for the R class and the interface
        // val rClassName = ClassName(packageName.get(), "R") // Not directly used by KotlinPoet if R types are fully qualified in string
        val resourcesProviderInterface = ClassName("com.voyager.utils.interfaces", "ResourcesProvider")

        val getResIdFun = FunSpec.builder("getResId")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("type", String::class)
            .addParameter("name", String::class)
            .returns(Int::class)
            .addStatement("return " + getResIdBodyFormat.toString(), *formatArgs.toTypedArray())
            .build()

        val resourcesBridgeClass = TypeSpec.classBuilder("ResourcesBridge")
            .addKdoc("Auto-generated by Voyager's GenerateResourcesTask. Do not edit manually.")
            .addSuperinterface(resourcesProviderInterface)
            .addFunction(getResIdFun)
            .build()

        val fileSpec = FileSpec.builder(GENERATED_PACKAGE, GENERATED_FILE_NAME.removeSuffix(".kt"))
            .addType(resourcesBridgeClass)
            .addImport(packageName.get(), "R") // Import the main R class
            .indent("    ") // Use 4 spaces for indentation
            .build()
        
        try {
            generatedFile.parentFile.mkdirs() // Ensure parent directory exists
            fileSpec.writeTo(generatedFile)   // Write directly to the target file
            LOGGER.lifecycle("Successfully wrote ${generatedFile.name} to ${generatedFile.absolutePath}")
        } catch (e: Exception) {
            LOGGER.error("Failed to write generated Kotlin file: ${generatedFile.absolutePath}", e)
        }
    }
}
