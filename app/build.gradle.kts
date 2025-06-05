plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.ksp)
    id("com.voyager.plugin") version "1.0.0-Beta01"
}

android {
    namespace = "com.example"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0-Beta1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        proguardFiles()
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

    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs += listOf("-Xskip-prerelease-check")
    }
}

resources {
    resFiles.from(
        fileTree("src/main/res") {
            include("**/strings.xml", "**/colors.xml", "**/themes.xml", "**/styles.xml")
        })
}

dependencies {
    implementation(project(":voyager-processor"))
    ksp(project(":voyager-processor"))
    implementation(project(":Voyager"))

    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.rx.java)
    implementation(libs.rx.android)
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}