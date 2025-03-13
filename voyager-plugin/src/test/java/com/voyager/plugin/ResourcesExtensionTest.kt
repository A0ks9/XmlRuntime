package com.voyager.plugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ResourcesExtensionFunctionalTest {

    @TempDir
    lateinit var testProjectDir: File // Creates a temporary Gradle project

    @Test
    fun `test plugin applies and extension is available`() {
        // Create build.gradle.kts in the test project
        File(testProjectDir, "build.gradle.kts").apply {
            writeText(
                """
                plugins {
                    id("com.dynamic.plugin")
                }
                
                resources {
                    resFiles.from("src/main/res/values/strings.xml")
                }
                """.trimIndent()
            )
        }

        // Create a settings.gradle.kts (needed for test project setup)
        File(testProjectDir, "settings.gradle.kts").writeText("rootProject.name = \"testProject\"")

        // Create a fake resource file inside src/main/res/values
        val resDir = File(testProjectDir, "src/main/res/values").apply { mkdirs() }
        val fakeResFile = File(resDir, "strings.xml").apply {
            writeText("<resources><string name=\"app_name\">Test App</string></resources>")
        }

        // Run Gradle with TestKit
        val result = GradleRunner.create().withProjectDir(testProjectDir)
            .withArguments("help") // Just check if the plugin applies
            .withPluginClasspath().build()

        // Verify the plugin applied successfully
        assertTrue(result.output.contains("BUILD SUCCESSFUL"), "Plugin should apply successfully")

        // Verify that the file exists (simulating extension usage)
        assertTrue(fakeResFile.exists(), "Fake resource file should exist")
    }
}
