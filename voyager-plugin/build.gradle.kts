plugins {
    // Kotlin JVM plugin (via version catalog alias).
    alias(libs.plugins.jetbrains.kotlin.jvm)

    // Gradle plugin development and publishing.
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "1.3.1"
}

group = "com.voyager"
version = "1.0.0-Beta01"

gradlePlugin {
    website.set("https://github.com/A0ks9/XmlRuntime")
    vcsUrl.set("https://github.com/A0ks9/XmlRuntime")
    // Register the plugin with a unique ID and implementation class.
    plugins {
        create("voyager-plugin") {
            id = "com.voyager.plugin"
            implementationClass = "com.voyager.plugin.ResourcesPlugin"
            displayName = "Voyager Plugin"
            description = "A plugin for generating kotlin code that works as a bridge between Voyager core lib and the resources"
            tags.set(listOf("android", "xml", "kotlin", "resources"))
        }
    }
}

publishing {
    repositories {
        // Publish to the local Maven repository.
        mavenLocal()
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
