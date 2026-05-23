import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // KSP 插件，处理Room
    id("com.google.devtools.ksp") version "2.3.7"
    // 序列化
    kotlin("plugin.serialization") version "2.3.21"
}

android {
    namespace = "com.huangyanzhen.mytraveljournal"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mytraveljournal"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = true
    }
    ndkVersion = "27.1.12297006"
    buildToolsVersion = "36.0.0"
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(platform(libs.kotlinx.coroutines.bom))
    androidTestImplementation(platform(libs.kotlinx.coroutines.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    /**
     * 核心功能支持
     */

    // Room支持
    implementation(libs.androidx.room.runtime)

    // 支持 Room 的 Kotlin 协程和 Flow 扩展
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Kotlin 官方 JSON 序列化库
    implementation(libs.kotlinx.serialization.json)

    // 用于解析照片的 EXIF 信息
    implementation(libs.androidx.exifinterface)

    // 协程测试库（提供 runTest 方法）
    androidTestImplementation(libs.kotlinx.coroutines.test)
    // AndroidX 测试核心库
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.core.ktx)
}