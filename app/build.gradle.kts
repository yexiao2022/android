import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    id ("org.jetbrains.kotlin.android")
    kotlin("kapt")
}
val timestamp = SimpleDateFormat("MM_dd_HH_mm").format(Date())
android {
    namespace = "com.lh.painting"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.drawing.trace.sketch.draw"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        setProperty("archivesBaseName", "DrawSketch_V" + versionName + "(${versionCode})_$timestamp")
    }

    buildTypes {

        release {
            isMinifyEnabled = true
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
        viewBinding = true
    }


}

dependencies {
    kapt( "androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    androidTestImplementation("androidx.room:room-testing:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    androidTestImplementation("androidx.room:room-testing:2.6.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    val camerax_version = "1.1.0-beta01"
    //noinspection GradleDependency
    implementation("androidx.camera:camera-core:${camerax_version}")
    //noinspection GradleDependency
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    //noinspection GradleDependency
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    //noinspection GradleDependency
    implementation("androidx.camera:camera-video:${camerax_version}")
    //noinspection GradleDependency
    implementation("androidx.camera:camera-view:${camerax_version}")
    //noinspection GradleDependency
    implementation("androidx.camera:camera-extensions:${camerax_version}")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}