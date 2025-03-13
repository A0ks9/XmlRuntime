package com.voyager.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ResourcesPluginTest {

    @Test
    fun `plugin applies and registers generateResources task`() {
        // Create a dummy Gradle project for testing.
        val project: Project = ProjectBuilder.builder().build()
        project.version = "1.0.0-Beta01"

        // Add a dummy Android extension to supply the required 'androidNamespace'.
        // The plugin expects an extension of type AppExtension with a "namespace" property.
        project.extensions.add("android", DummyAppExtension("com.example.test"))

        // Create a dummy ResourcesExtension (the plugin creates this extension in apply())
        // In a real project, this would be your real implementation.
        project.extensions.create("resources", ResourcesExtension::class.java, project)

        // Apply the plugin.
        project.pluginManager.apply("com.dynamic.plugin")

        // Check that the generateResources task was registered.
        val generateTask = project.tasks.findByName("generateResources")
        assertNotNull(
            generateTask,
            "The generateResources task should be registered by the plugin."
        )
    }
}

// Minimal dummy implementation of ResourcesExtension for testing.
open class ResourcesExtension(project: Project) {
    // For testing purposes, we define a file collection.
    // In a real scenario, this would be configured to point to your resource XML files.
    val resFiles = project.files("src/main/res/values")
}

// Minimal dummy implementation of AppExtension for testing.
class DummyAppExtension(val namespace: String)
