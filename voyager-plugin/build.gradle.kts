plugins {
    // Using version catalog alias for the Kotlin JVM plugin.
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("java-gradle-plugin")
    id("maven-publish")
}

group = "com.voyager"
version = "1.0.0-Beta01"

gradlePlugin {
    // Register the plugin with a unique id and implementation class.
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
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/A0ks9/XmlRuntime")
            credentials {
                username = System.getenv("GITHUB_USER") ?: "A0ks9"
                password =
                    System.getenv("GITHUB_TOKEN") ?: "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"
            }
        }
    }
}


java {
    // Configure Java toolchain (ensures consistent Java version across environments).
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    // Configure Kotlin toolchain; using the JavaToolchainSpec for JVM toolchain support.
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    // Gradle API is needed to develop Gradle plugins.
    implementation(gradleApi())

    // Kotlin standard library and Gradle plugin support.
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin"))

    // Additional libraries from the version catalog.
    implementation(libs.kotlin.poet)
    implementation(libs.kotlinx.serialization)

    // Android build tools as a compile-only dependency.
    compileOnly(libs.android.build.tools)

    // Testing dependencies.
    testImplementation(gradleTestKit()) // For functional testing of the plugin.
    testImplementation(kotlin("test"))  // Kotlin testing framework.
    testImplementation(libs.junit.jupiter) // JUnit 5 for unit tests.
}

tasks.test {
    // Enable JUnit 5 platform for testing.
    useJUnitPlatform()
}
