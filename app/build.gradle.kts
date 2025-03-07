plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.plugin.serialization)
    id("kotlin-parcelize")
    id("com.dynamic.plugin")
}

android {
    namespace = "com.dynamic"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dynamic"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0-Beta1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        proguardFiles()
        multiDexEnabled = true
        externalNativeBuild {
            cmake {
                cppFlags(
                    "-std=c++17 -O3 -fPIC",
                    "-I${projectDir}/src/main/cpp/pugixml/src",
                    "-I${projectDir}/src/main/cpp/rapidjson/include"
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        viewBinding = true
    }

    dataBinding {
        enable = true
    }
    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }
    buildToolsVersion = "36.0.0 rc5"

    kotlin {
        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    ndkVersion = "27.0.12077973"
}

resources {
    resFiles.from(
        fileTree("src/main/res") {
            include("**/strings.xml", "**/colors.xml", "**/themes.xml", "**/styles.xml")
        }
    )
}

dependencies {
    implementation(project(":viewcore-ksp"))
    ksp(project(":viewcore-ksp"))
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(kotlin("reflect"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.appcompat)
    implementation(libs.sliding.pane.layout)
    implementation(libs.glide)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization)
    ksp(libs.room.compiler)
    //implementation(libs.tencent.mmkv)
    implementation(libs.rx.java)
    implementation(libs.rx.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}