plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("java-gradle-plugin")
    id("maven-publish")
}

group = "com.dynamic"
version = "1.0.0-Beta01"

gradlePlugin {
    plugins.create("voyager-plugin") {
        id = "com.dynamic.plugin"
        implementationClass = "com.dynamic.plugin.ResourcesPlugin"
    }
}

publishing {
    repositories.mavenLocal()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin"))
    compileOnly(libs.android.build.tools)
}