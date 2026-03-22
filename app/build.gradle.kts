import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

// Version bumps automatically on every CI build via github.run_number
// Falls back to 1 / "1.0.0-local" when building locally
val ciVersionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
val ciVersionName = System.getenv("VERSION_NAME") ?: "1.0.0-local"

android {
    namespace = "com.osu.client"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.osu.client"
        minSdk = 26
        targetSdk = 35
        versionCode = ciVersionCode
        versionName = ciVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "OSU_CLIENT_ID", "\"${System.getenv("OSU_CLIENT_ID") ?: localProps.getProperty("osu.client_id", "YOUR_CLIENT_ID")}\"")
        buildConfigField("String", "OSU_CLIENT_SECRET", "\"${System.getenv("OSU_CLIENT_SECRET") ?: localProps.getProperty("osu.client_secret", "YOUR_CLIENT_SECRET")}\"")
        buildConfigField("String", "OSU_REDIRECT_URI", "\"osu://callback\"")
    }

    signingConfigs {
        create("release") {
            val keystoreFile = System.getenv("SIGNING_STORE_FILE")
                ?: localProps.getProperty("signing.store_file")
            val keystorePassword = System.getenv("SIGNING_STORE_PASSWORD")
                ?: localProps.getProperty("signing.store_password")
            val keyAlias = System.getenv("SIGNING_KEY_ALIAS")
                ?: localProps.getProperty("signing.key_alias")
            val keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
                ?: localProps.getProperty("signing.key_password")

            if (keystoreFile != null) {
                storeFile = file("$keystoreFile")
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.splashscreen)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi)
    ksp(libs.moshi.codegen)

    implementation(libs.coil.compose)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.browser)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
}