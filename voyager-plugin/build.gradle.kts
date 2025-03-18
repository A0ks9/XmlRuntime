plugins {
    // Kotlin JVM plugin (via version catalog alias).
    alias(libs.plugins.jetbrains.kotlin.jvm)

    // Gradle plugin development and publishing.
    id("java-gradle-plugin")
    id("maven-publish")
}

group = "com.voyager"
version = "1.0.0-Beta01"

gradlePlugin {
    // Register the plugin with a unique ID and implementation class.
    plugins {
        create("voyager-plugin") {
            id = "com.voyager.plugin"
            implementationClass = "com.voyager.plugin.ResourcesPlugin"
        }
    }
}

publishing {
    repositories {
        maven {
            // Publishing to GitHub Packages.
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/A0ks9/XmlRuntime")
            credentials {
                // Use environment variables for security.
                username = System.getenv("GITHUB_USER") ?: "A0ks9"
                password = System.getenv("GITHUB_TOKEN") ?: error("Missing GITHUB_TOKEN!")
            }
        }
        gradlePluginPortal()
    }
}

java {
    // Ensure compatibility with Java 21.
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    // Ensure compatibility with Kotlin/JVM 21.
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    // Gradle API (for developing Gradle plugins).
    implementation(gradleApi())

    // Kotlin standard libraries and Gradle plugin support.
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin"))

    // Other dependencies from your version catalog.
    implementation(libs.kotlin.poet)
    implementation(libs.kotlinx.serialization)

    // Android Gradle plugin as a compile-only dependency (if needed).
    compileOnly(libs.android.build.tools)

    // For testing your Gradle plugin.
    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    // Enable JUnit 5 platform for testing.
    useJUnitPlatform()
}

// Ensure the plugin is built before publishing.
tasks.named("publish") {
    dependsOn("build")
}
