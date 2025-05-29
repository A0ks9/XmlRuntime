/**
 * This file defines the `ResourcesPlugin`, a Gradle plugin for Android projects
 * that integrates Voyager's resource processing and code generation capabilities.
 *
 * The plugin performs the following key functions:
 * 1.  **Creates a Gradle Extension:** Registers a [ResourcesExtension] named "resources",
 *     allowing users to configure input resource file sets in their `build.gradle` scripts.
 * 2.  **Integrates with Android Build Variants:** Uses the `AndroidComponentsExtension`
 *     to iterate over each build variant (e.g., debug, release, custom flavors).
 * 3.  **Registers `GenerateResourcesTask`:** For each Android variant, it registers an instance
 *     of [GenerateResourcesTask]. This task is responsible for parsing resource XML files
 *     and generating a `ResourcesBridge.kt` file.
 * 4.  **Configures Task Inputs/Outputs:** It wires the `ResourcesExtension` properties
 *     (like input resource files) and variant-specific information (like package name)
 *     to the inputs of the `GenerateResourcesTask`. The output directory for the generated
 *     code is also configured.
 * 5.  **Adds Generated Code to Source Sets:** The output directory of the `GenerateResourcesTask`
 *     is added as a generated Java source directory to the corresponding Android variant's
 *     source set. This makes the generated `ResourcesBridge.kt` available for compilation
 *     and use within the project.
 * 6.  **Establishes Task Dependencies:** Ensures that the Kotlin compilation task for each
 *     variant depends on the respective `GenerateResourcesTask` instance, so resource
 *     generation always occurs before compilation.
 *
 * This plugin automates the process of making Android resources accessible programmatically
 * through the Voyager framework's `ResourcesBridge`.
 *
 * @see GenerateResourcesTask For the core resource processing and code generation logic.
 * @see ResourcesExtension For configuring the plugin's behavior.
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.plugin

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.util.Locale

/**
 * `ResourcesPlugin` is a Gradle [Plugin] that integrates Voyager's resource processing
 * capabilities into an Android project.
 *
 * When applied, this plugin:
 * 1. Registers the [ResourcesExtension] to allow configuration in `build.gradle` files.
 * 2. Hooks into the Android build process using [AndroidComponentsExtension].
 * 3. For each Android build variant, it registers a [GenerateResourcesTask] instance.
 * 4. Configures this task with inputs from the extension and variant-specific details.
 * 5. Adds the output directory of the task as a source directory for the variant,
 *    making the generated `ResourcesBridge.kt` available to the project's codebase.
 * 6. Ensures that Kotlin compilation tasks depend on the resource generation task.
 */
class ResourcesPlugin : Plugin<Project> {

    /**
     * Companion object for constants and logger.
     */
    companion object {
        /** Name of the Gradle extension for configuring this plugin (e.g., `resources { ... }`). */
        private const val EXTENSION_NAME = "resources"
        /** Default subdirectory within the build directory for generated Kotlin resource files. */
        private const val GENERATED_DIR = "generated/voyager-kt-resources" // Made more specific
        /** Logger instance for this plugin. */
        private val LOGGER: Logger = Logging.getLogger(ResourcesPlugin::class.java)
    }

    /**
     * Applies this plugin to the given Gradle [Project].
     * This method is called by Gradle when the plugin is applied.
     *
     * It performs the main setup for the plugin:
     * - Creates and registers the [ResourcesExtension].
     * - Identifies the Android application/library components.
     * - Iterates through each build variant (e.g., debug, release).
     * - For each variant, it registers and configures a [GenerateResourcesTask].
     * - Hooks the generated sources into the Android variant's source sets.
     * - Sets up task dependencies to ensure resources are generated before compilation.
     *
     * @param project The Gradle [Project] to which this plugin is being applied.
     * @throws GradleException if the `com.android.base` or `com.android.application` / `com.android.library`
     *                         plugin is not applied, as [AndroidComponentsExtension] would not be found.
     */
    override fun apply(project: Project) {
        LOGGER.info("Voyager: Applying ResourcesPlugin to project '${project.name}'.")
        // Create the 'resources' extension for configuration in build.gradle
        val extension = project.extensions.create(
            EXTENSION_NAME, ResourcesExtension::class.java, project
        )

        // Define the output directory for generated Kotlin files, relative to the build directory
        val generatedDirProvider: Provider<Directory> = project.layout.buildDirectory.dir(GENERATED_DIR)

        // Get the AndroidComponentsExtension to hook into Android build variants
        val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
            ?: throw GradleException(
                "Voyager ResourcesPlugin: AndroidComponentsExtension not found. " +
                        "This plugin must be applied to an Android application or library module " +
                        "(e.g., after 'com.android.application' or 'com.android.library')."
            )

        // Process each Android build variant (e.g., debug, release, custom flavors)
        androidComponents.onVariants { variant ->
            LOGGER.lifecycle("Voyager: Configuring resource generation for '${variant.name}' variant.")

            // Create and configure the GenerateResourcesTask for the current variant
            val generateTaskProvider = createGenerateResourcesTask(
                project, variant, extension, generatedDirProvider
            )

            // Add the generated sources to the variant's source set and set up task dependencies
            configureVariantSources(variant, generateTaskProvider, project)
        }
    }

    /**
     * Creates and configures a [GenerateResourcesTask] for a specific Android [Variant].
     * The task is registered with Gradle, and its inputs (resource files, package name, plugin version)
     * and output directory are configured based on the [extension] settings and variant details.
     *
     * @param project The Gradle [Project].
     * @param variant The specific Android [Variant] for which to create the task.
     * @param extension The [ResourcesExtension] instance containing user-defined configurations.
     * @param generatedDirProvider A [Provider] for the output [Directory] where generated files will be placed.
     * @return A [TaskProvider] for the registered [GenerateResourcesTask].
     */
    private fun createGenerateResourcesTask(
        project: Project,
        variant: Variant,
        extension: ResourcesExtension,
        generatedDirProvider: Provider<Directory>,
    ): TaskProvider<GenerateResourcesTask> {
        // Task name, e.g., "generateVoyagerResourcesDebug"
        val taskName = "generateVoyagerResources${variant.name.capitalizeAsciiCompatible()}"
        LOGGER.debug("Voyager: Registering task '$taskName'")

        return project.tasks.register(taskName, GenerateResourcesTask::class.java) { task ->
            task.description = "Generates Voyager ResourcesBridge for the ${variant.name} variant."
            task.group = "Voyager" // Assign a group for better organization in Gradle task list

            // Configure task inputs from the extension and variant
            task.valuesFiles.setFrom(extension.resFiles) // Set from FileCollection configured in extension

            val pluginImplVersion = this::class.java.getPackage()?.implementationVersion
            if (pluginImplVersion == null) {
                LOGGER.debug("Voyager: Could not determine plugin implementation version from manifest. Falling back to project version for task '${task.name}'.")
            }
            task.pluginVersion.set(pluginImplVersion ?: project.version.toString())
            task.packageName.set(variant.namespace) // namespace is Provider<String>
            
            // Configure task output directory
            task.outputDir.set(generatedDirProvider)
        }
        LOGGER.debug("Voyager: Task '$taskName' registered and configuration closure set.")
        return taskProvider
    }

    /**
     * Configures an Android [Variant] to include the sources generated by [GenerateResourcesTask]
     * and ensures that Kotlin compilation tasks depend on the generation task.
     *
     * @param variant The Android [Variant] to configure.
     * @param generateTaskProvider The [TaskProvider] for the [GenerateResourcesTask] whose output
     *                             needs to be added as a source directory.
     * @param project The Gradle [Project], used to access tasks by type.
     */
    private fun configureVariantSources(
        variant: Variant,
        generateTaskProvider: TaskProvider<GenerateResourcesTask>,
        project: Project,
    ) {
        LOGGER.debug("Voyager: Adding generated source directory for '${variant.name}' variant.")
        // Add the output directory of GenerateResourcesTask as a source directory for the variant
        // This makes the generated Kotlin files available for compilation.
        variant.sources.java?.addGeneratedSourceDirectory(
            generateTaskProvider,
            GenerateResourcesTask::outputDir // Method reference to the outputDir property
        )

        // Ensure Kotlin compilation task depends on our resource generation task
        // This is important so that ResourcesBridge.kt is generated before Kotlin compiler tries to compile it.
        project.tasks.withType(KotlinCompilationTask::class.java).configureEach { kotlinCompilationTask ->
            // Check if the compilation task is for the current variant (simplified check)
            // A more robust check might involve `kotlinCompilationTask.name.contains(variant.name, ignoreCase = true)`
            // or inspecting `kotlinCompilationTask.androidSourceSet.name` if available/relevant.
            // For now, a broad dependency is often acceptable for generated code.
            if (kotlinCompilationTask.name.startsWith("compile") &&
                kotlinCompilationTask.name.contains(variant.name, ignoreCase = true)
            ) {
                LOGGER.debug("Voyager: Making task '${kotlinCompilationTask.name}' depend on '${generateTaskProvider.name}'.")
                kotlinCompilationTask.dependsOn(generateTaskProvider)
            }
        }
    }

    /**
     * Capitalizes the first character of this string using a root locale for ASCII compatibility,
     * primarily for creating task names like `generateResourcesDebug` -> `generateResourcesDebug`.
     * If the string is empty, returns an empty string.
     *
     * Note: Kotlin's `capitalize()` was deprecated. This provides a simple, ASCII-focused replacement
     * suitable for task name generation.
     *
     * @return The string with its first character capitalized, or the original string if empty or already capitalized.
     */
    private fun String.capitalizeAsciiCompatible(): String {
        return if (isNotEmpty()) {
            // For task names, simple uppercase of first char is usually sufficient and locale-neutral for ASCII.
            // titlecase can have different behavior for some Unicode chars not expected in variant names.
            this[0].uppercaseChar() + this.substring(1)
        } else {
            this
        }
    }
}
