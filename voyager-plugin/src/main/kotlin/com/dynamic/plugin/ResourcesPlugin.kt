package com.dynamic.plugin

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.AppExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

class ResourcesPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val extension = extensions.create("resources", ResourcesExtension::class.java, target)

        val generatedDir = layout.buildDirectory.dir("generated/kt-resources/com/dynamic/resources")
        val generatedFile = generatedDir.map { it.file("ResourcesBridge.kt") }

        val generateTask = tasks.register("generateResources", GenerateResourcesTask::class.java) {
            it.valuesFiles.setFrom(extension.resFiles)
            it.packageName.set(androidNamespace)
            it.outputFile.set(generatedFile)
        }

        extensions.findByType(CommonExtension::class.java)?.sourceSets?.named("main") {
            it.kotlin.srcDir(generatedDir)
        }

        afterEvaluate {
            tasks.findByPath("preBuild")?.dependsOn(generateTask)

            tasks.withType<KotlinCompilationTask<*>>(KotlinCompilationTask::class.java).configureEach {
                it.dependsOn(generateTask)
            }
        }
    }

    private val Project.androidNamespace: String
        get() = extensions.findByType(AppExtension::class.java)?.namespace ?: throw GradleException(
            "Android extension not found"
        )
}
