/**
 * Gradle extension for configuring the Voyager Resources Plugin.
 *
 * This extension, typically named `resources` in a `build.gradle.kts` script (or `voyager` as per
 * the example, which seems to be a typo in the original KDoc; standard practice would be to match
 * `EXTENSION_NAME` from `ResourcesPlugin.kt`), allows users to specify the set of Android
 * resource files that should be processed by the [GenerateResourcesTask].
 *
 * Example Usage in `build.gradle.kts`:
 * ```kotlin
 * // Assuming the plugin registers the extension as "resources"
 * resources {
 *     resFiles.from(fileTree("src/main/res/values") {
 *         include("**/strings.xml")
 *         include("**/colors.xml")
 *     })
 *     // Alternatively, to include all XMLs in res/values:
 *     // resFiles.from(fileTree("src/main/res/values").include("**/*.xml"))
 * }
 * ```
 *
 * The primary property to configure is [resFiles].
 *
 * @see ResourcesPlugin Where this extension is registered.
 * @see GenerateResourcesTask The task that uses configurations from this extension.
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
package com.voyager.plugin

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
// import org.gradle.api.file.ProjectLayout // Not directly used, can be removed
import javax.inject.Inject

/**
 * Configurable extension for the Voyager Resources Plugin.
 *
 * This abstract class is instantiated by Gradle, allowing build script users to configure
 * properties that affect the behavior of the plugin, primarily the [GenerateResourcesTask].
 *
 * @param project The Gradle [Project] instance, injected by Gradle. This is used to access
 *                Gradle services like `ObjectFactory` for creating managed properties.
 */
abstract class ResourcesExtension @Inject constructor(project: Project) {

    /**
     * A [ConfigurableFileCollection] representing the set of Android resource XML files
     * (typically from `src/main/res/values/`) that Voyager should process.
     *
     * Build script authors should configure this property to point to their resource files.
     * Example in `build.gradle.kts`:
     * ```kotlin
     * resources {
     *     resFiles.from(fileTree("src/main/res/values").include("**/*.xml"))
     *     // Or specify individual files:
     *     // resFiles.from(files("src/main/res/values/strings.xml", "src/main/res/values/colors.xml"))
     * }
     * ```
     * This collection is used as an input for the [GenerateResourcesTask].
     */
    val resFiles: ConfigurableFileCollection = project.objects.fileCollection()
}
