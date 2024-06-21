import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.firebase.appdistribution)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.detekt.android)
}

android {
    namespace = "com.devofure.workoutschedule"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.devofure.workoutschedule"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(
                    mapOf("room.schemaLocation" to "$projectDir/schemas")
                )
            }
        }
    }

    signingConfigs {
        create("release") {
            val keyAliasEnv = System.getenv("KEY_ALIAS")
            val keyPasswordEnv = System.getenv("KEY_PASSWORD")
            val storeFileEnv = System.getenv("STORE_FILE")
            val storePasswordEnv = System.getenv("STORE_PASSWORD")

            if (keyAliasEnv != null) {
                keyAlias = keyAliasEnv
                keyPassword = keyPasswordEnv
                storeFile = file(storeFileEnv)
                storePassword = storePasswordEnv
            } else {
                val keystorePropertiesFile = rootProject.file("keystore.properties")
                val keystoreProperties = Properties()
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))

                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
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
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
}

composeCompiler {
    enableStrongSkippingMode = true
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
}

firebaseAppDistribution {
    //releaseNotesFile = "release-notes.txt"
    groups = "testers"
    serviceCredentialsFile = "app/service-account.json"
}

dependencies {
    detektPlugins(libs.detekt.compose)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.ui.auth)
    implementation(libs.gson)
    implementation(libs.material)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    implementation(libs.timber)
    implementation(libs.reorderable)
    implementation(platform(libs.firebase.bom))
    ksp(libs.room.compiler)

    // Testing dependencies
    androidTestImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit.ktx)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.androidx.junit.ktx)
    testImplementation(libs.kotlinx.coroutines.test)
}
