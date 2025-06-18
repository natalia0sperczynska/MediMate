plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.example.healme"
    compileSdk = 35

    buildFeatures {
        dataBinding = true
        compose = true
    }

    packagingOptions {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/NOTICE.md"
            )
        }
    }


    defaultConfig {
        applicationId = "com.example.medimate"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}
//
//composeCompiler {
//    reportsDestination = layout.buildDirectory.dir("compose_compiler")
//    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
//}

dependencies {
    // Compose BOM
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.work.runtime.ktx)
    val composeBom = platform("androidx.compose:compose-bom:2025.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose & Material3
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.androidx.compose.ui.ui.tooling.preview)
    implementation(libs.androidx.material.icons.extended)
    implementation("androidx.compose.material3.adaptive:adaptive")
    debugImplementation(libs.ui.tooling)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)

    // Jetpack
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    implementation(libs.androidx.runtime.livedata)
    implementation("androidx.compose.runtime:runtime-rxjava2")

    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Navigation
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    //androidTestImplementation(libs.androidx.espresso.core)

    //nav controller
    val nav_version = "2.8.9"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.dynamic.features.fragment)
    androidTestImplementation(libs.androidx.navigation.testing)

    // Firebase
    implementation(platform(libs.firebase.bom.v33150))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx.v2030)
    implementation(libs.kotlinx.coroutines.core)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // Chat ZEGOCLOUD
    implementation(libs.zego.uikit.signaling.plugin.android)
//  implementation(libs.uikit.chat)
    implementation(libs.zego.inapp.chat.uikit.android)

//    // Fragment & AppCompat (for ZIMKit)
//    implementation(libs.androidx.fragment.ktx.v162)
//    implementation(libs.androidx.fragment.ktx)
//    implementation(libs.androidx.appcompat.v161)

    // Coil – image loading
    implementation(libs.coil.compose.v240)
    implementation(libs.kotlinx.coroutines.core.v160)// or newer version
    implementation(libs.kotlinx.coroutines.android) // or newer version

    // Date formatting
    implementation(libs.threetenabp)

    // Core KTX versioned
    implementation(libs.androidx.core.ktx.v1120)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    //material 3
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.compose.material3:material3-window-size-class-android:1.3.2")

    //mail
    implementation ("com.sun.mail:android-mail:1.6.7")
    implementation ("com.sun.mail:android-activation:1.6.7")
    // workManager
    implementation (libs.androidx.work.runtime.ktx.v271)
    //firestore storage
    implementation("com.google.firebase:firebase-storage")
}
