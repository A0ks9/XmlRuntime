@file:Suppress("UnstableApiUsage")

plugins {
    // Apply plugins using version catalog aliases
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.plugin.serialization)
    id("kotlin-parcelize")
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.github.A0ks9"
            artifactId = "voyager"
            version = "1.0.0-Beta01"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

android {
    namespace = "com.voyager"
    compileSdk = 35
    ndkVersion = "29.0.13113456"

    defaultConfig {
        minSdk = 21

        // Configure native build options using CMake (incubating API suppressed)
        externalNativeBuild {
            cmake {
                cppFlags.addAll(
                    listOf(
                        "-std=c++17",    // Use C++17 standard
                        "-O3",           // High optimization for speed
                        "-fPIC",         // Generate position-independent code
                        "-I${projectDir}/src/main/cpp/pugixml/src",   // Include pugixml headers
                        "-I${projectDir}/src/main/cpp/rapidjson/include" // Include rapidjson headers
                    )
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Enable code shrinking and obfuscation
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.31.6"
        }
    }

    buildFeatures {
        viewBinding = true
    }

    dataBinding {
        enable = true
    }


    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }

        singleVariant("debug") {
            withSourcesJar()
        }
    }
}

dependencies {
    // Core Android libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.flexbox)

    // Kotlin reflection and coroutines for modern asynchronous code
    implementation(kotlin("reflect"))
    implementation(libs.kotlin.coroutines)

    // UI components and efficient image loading
    implementation(libs.sliding.pane.layout)
    implementation(libs.glide)

    // Room Database for efficient local storage
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // JSON parsing and serialization libraries
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization)

    // Reactive programming with RxJava for responsive UIs
    implementation(libs.rx.java)
    implementation(libs.rx.android)

    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.android)

    // Testing frameworks for unit and instrumentation tests
    testImplementation(libs.junit)
    testImplementation(libs.koin.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
