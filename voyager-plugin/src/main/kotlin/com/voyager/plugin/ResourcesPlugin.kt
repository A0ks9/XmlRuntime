package com.voyager.plugin

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.util.Locale

class ResourcesPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension =
            project.extensions.create("resources", ResourcesExtension::class.java, project)

        // Directory where the generated code will be placed
        val generatedDir = project.layout.buildDirectory.dir("generated/kt-resources")

        // Ensure Android plugin is applied and fetch AndroidComponents
        val androidComponents =
            project.extensions.findByType(AndroidComponentsExtension::class.java)
                ?: throw GradleException("AndroidComponentsExtension not found. Ensure this plugin is applied to an Android module.")

        // Register for each variant (e.g., debug, release)
        androidComponents.onVariants { variant ->
            project.logger.lifecycle("Registering resource generation for ${variant.name} variant.")

            // Create a task for each variant
            val generateTask = project.tasks.register(
                "generateResources${
                variant.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                }
            }", GenerateResourcesTask::class.java) {
                it.valuesFiles.setFrom(extension.resFiles)
                it.pluginVersion.set(project.version.toString())

                it.packageName.set(variant.namespace.map { ns ->
                    ns ?: throw GradleException("Namespace is missing for ${variant.name} variant.")
                })

                it.outputDir.set(generatedDir)
            }

            // Add generated code to variant's source set
            variant.sources.java?.addGeneratedSourceDirectory(
                generateTask, GenerateResourcesTask::outputDir
            )

            // Ensure Kotlin compilation depends on resource generation
            project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
                it.dependsOn(generateTask)
            }
        }
    }
}
