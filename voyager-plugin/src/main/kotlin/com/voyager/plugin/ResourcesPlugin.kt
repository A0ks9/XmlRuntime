/**
 * High-performance Gradle plugin for Android resource management and code generation.
 *
 * This plugin efficiently manages Android resources and generates optimized Kotlin code
 * for resource access. It supports incremental builds and caching for better performance.
 *
 * Key features:
 * - Efficient resource management
 * - Optimized code generation
 * - Incremental build support
 * - Memory-efficient processing
 * - Thread-safe operations
 *
 * Performance optimizations:
 * - Lazy initialization
 * - Efficient file handling
 * - Optimized task configuration
 * - Safe resource management
 *
 * Usage example:
 * ```kotlin
 * plugins {
 *     id("com.voyager.plugin")
 * }
 *
 * resources {
 *     resFiles.from(fileTree("src/main/res"))
 * }
 * ```
 *
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
 * Plugin for managing Android resources and generating optimized Kotlin code.
 *
 * This plugin provides efficient resource management and code generation capabilities
 * for Android projects, with optimized performance and memory usage.
 */
class ResourcesPlugin : Plugin<Project> {

    companion object {
        private const val EXTENSION_NAME = "resources"
        private const val GENERATED_DIR = "generated/kt-resources"
        private val LOGGER: Logger = Logging.getLogger(ResourcesPlugin::class.java)
    }

    /**
     * Applies the plugin to the given project.
     *
     * This method sets up the plugin's functionality, including:
     * - Creating the extension
     * - Configuring the build directory
     * - Setting up variant-specific tasks
     * - Configuring dependencies
     *
     * @param project The Gradle project to apply the plugin to
     * @throws GradleException if Android components are not found
     */
    override fun apply(project: Project) {
        // Create extension with optimized configuration
        val extension = project.extensions.create(
            EXTENSION_NAME, ResourcesExtension::class.java, project
        )

        // Configure generated code directory
        val generatedDir = project.layout.buildDirectory.dir(GENERATED_DIR)

        // Get Android components with proper error handling
        val androidComponents =
            project.extensions.findByType(AndroidComponentsExtension::class.java)
                ?: throw GradleException(
                    "AndroidComponentsExtension not found. " + "Ensure this plugin is applied to an Android module."
                )

        // Configure variants with optimized task creation
        androidComponents.onVariants { variant ->
            LOGGER.lifecycle("Registering resource generation for ${variant.name} variant.")

            // Create and configure task for the variant
            val generateTask = createGenerateTask(
                project, variant, extension, generatedDir
            )

            // Configure source sets and dependencies
            configureVariant(variant, generateTask, project)
        }
    }

    /**
     * Creates a resource generation task for the given variant.
     *
     * @param project The Gradle project
     * @param variant The Android variant
     * @param extension The resources extension
     * @param generatedDir The directory for generated code
     * @return The configured task
     */
    private fun createGenerateTask(
        project: Project,
        variant: Variant,
        extension: ResourcesExtension,
        generatedDir: Provider<Directory>,
    ) = project.tasks.register(
        "generateResources${variant.name.capitalize()}", GenerateResourcesTask::class.java
    ) { task ->
        task.valuesFiles.setFrom(extension.resFiles)
        task.pluginVersion.set(project.version.toString())
        task.packageName.set(variant.namespace.map { ns ->
            ns ?: throw GradleException("Namespace is missing for ${variant.name} variant.")
        })
        task.outputDir.set(generatedDir)
    }

    /**
     * Configures the variant with source sets and dependencies.
     *
     * @param variant The Android variant
     * @param generateTask The resource generation task
     * @param project The Gradle project
     */
    private fun configureVariant(
        variant: Variant,
        generateTask: TaskProvider<GenerateResourcesTask>,
        project: Project,
    ) {
        // Add generated code to variant's source set
        variant.sources.java?.addGeneratedSourceDirectory(
            generateTask, GenerateResourcesTask::outputDir
        )

        // Configure Kotlin compilation dependencies
        project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
            it.dependsOn(generateTask)
        }
    }

    /**
     * Capitalizes the first character of a string.
     *
     * @return The capitalized string
     */
    private fun String.capitalize(): String = replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
    }
}
