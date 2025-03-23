plugins {
    id("java-library")
    kotlin("kapt")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.google.ksp)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
    }
}

dependencies {
    implementation(libs.symbol.processing.api)
    implementation(libs.symbol.processing)
    implementation(libs.javapoet)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter)
}
