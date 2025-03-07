package com.dynamic.plugin


import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.AppExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider


class ResourcesPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val extension = extensions.create("resources", ResourcesExtension::class.java, target)
        val generatedDir = layout.buildDirectory.dir("generated/kt-resources/com/dynamic/resources")
        val generatedFile = generatedDir.map { it.file("ResourcesBridge.kt") }

        val generateTask: TaskProvider<GenerateResourcesTask> = tasks.register(
            "generateResources", GenerateResourcesTask::class.java
        ) {
            it.valuesFiles.setFrom(extension.resFiles)
            it.packageName.set(androidNamespace)
            it.outputFile.set(generatedFile)
        }


        afterEvaluate {
            tasks.findByPath("preBuild")?.dependsOn(generateTask)

            extensions.findByType(CommonExtension::class.java)?.apply {
                sourceSets.getByName("main").apply {
                    kotlin.srcDir(generatedDir.get().asFile)
                }
            }
        }
    }
}


private val Project.androidNamespace: String
    get() = extensions.findByType(AppExtension::class.java)?.namespace
        ?: throw GradleException("Android extension not found")