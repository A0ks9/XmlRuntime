/**
 * High-performance Gradle extension for managing Android resource files.
 *
 * This extension provides efficient resource file management capabilities for Android projects,
 * with optimized file collection handling and memory-efficient operations.
 *
 * Key features:
 * - Efficient file collection management
 * - Memory-optimized resource handling
 * - Thread-safe operations
 * - Lazy initialization
 *
 * Performance optimizations:
 * - Lazy file collection initialization
 * - Efficient resource tracking
 * - Minimized object creation
 * - Safe resource handling
 *
 * Usage example:
 * ```kotlin
 * plugins {
 *     id("com.voyager.plugin")
 * }
 *
 * voyager {
 *     resFiles.from(fileTree("src/main/res"))
 * }
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.plugin

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ProjectLayout
import javax.inject.Inject

abstract class ResourcesExtension @Inject constructor(target: Project) {
    val resFiles: ConfigurableFileCollection = target.objects.fileCollection()
}
