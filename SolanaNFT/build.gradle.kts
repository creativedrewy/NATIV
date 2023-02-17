import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    compileSdk = 33
    buildToolsVersion = "31.0.0"

    defaultConfig {
        minSdk = 21
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val properties = Properties()
        if (project.rootProject.file("local.properties").exists()) {
            properties.load(FileInputStream("local.properties"))
        }

        buildConfigField("String", "RPC_API_KEY", "\"${properties.getProperty("RPC_API_KEY")}\"")
        buildConfigField("String", "RPC_BASE_URL", "\"https://rpc.helius.xyz/?api-key=\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":ChainSupport"))

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("com.github.metaplex-foundation:metaplex-android:1.4.1") {
        exclude(group = "com.github.metaplex-foundation.kborsh", module = "kborsh-jvm")
    }

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.9.0")

    kapt("com.google.dagger:hilt-compiler:2.44.2")
    implementation("com.google.dagger:hilt-android:2.44.2")

    testImplementation("junit:junit:4.+")

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}