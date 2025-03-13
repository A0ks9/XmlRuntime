package com.voyager.plugin

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.work.FileChange
import org.gradle.work.InputChanges
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class GenerateResourcesTaskTest {

    @Test
    fun `test ResourcesBridge generation`() {
        // Create a dummy Gradle project.
        val project: Project = ProjectBuilder.builder().build()
        project.version = "1.0.0-Beta01"

        // Set up a temporary "values" directory and create a dummy strings.xml file.
        val tempValuesDir = createTempDir("resValues")
        val stringsFile = File(tempValuesDir, "strings.xml")
        stringsFile.writeText(
            """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <string name="app_name">Test App</string>
            </resources>
            """.trimIndent()
        )

        // Create a temporary output directory for generated Kotlin file.
        val tempOutputDir = createTempDir("generatedResources")
        val outputFile = File(tempOutputDir, "ResourcesBridge.kt")

        // Create and configure the GenerateResourcesTask.
        val task = project.tasks.create("generateResources", GenerateResourcesTask::class.java)
        // Set input files: our temporary values directory.
        task.valuesFiles.from(tempValuesDir)
        // Set required plugin version and package name.
        task.pluginVersion.set("1.0.0-Beta01")
        task.packageName.set("com.example.app")
        // Set the output file.
        task.outputFile.set(outputFile)

        // Create a fake InputChanges that is non-incremental so the task always regenerates.
        val fakeInputChanges = NonIncrementalInputChanges()

        // Run the task action.
        task.generateBridge(fakeInputChanges)

        // Verify that the output file exists and is non-empty.
        assertTrue(outputFile.exists(), "The output file should exist after task execution.")
        assertTrue(
            outputFile.readText().isNotEmpty(), "The output file should contain generated code."
        )

        // Clean up temporary directories.
        tempValuesDir.deleteRecursively()
        tempOutputDir.deleteRecursively()
    }
}

/**
 * A simple non-incremental implementation of InputChanges.
 * It always returns isIncremental = false and no file changes.
 */
class NonIncrementalInputChanges : InputChanges {
    override fun isIncremental(): Boolean = false
    override fun getFileChanges(parameter: FileCollection): Iterable<FileChange?> = emptyList()

    override fun getFileChanges(parameter: Provider<out FileSystemLocation?>): Iterable<FileChange?> =
        emptyList()
}
