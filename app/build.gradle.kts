import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.altintakipandroid"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.dienu.altintakip"
        minSdk = 24
        targetSdk = 36
        versionCode = 4
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val secretFile = rootProject.file("secret.properties")
        val defaultApiKey = if (secretFile.exists()) {
            secretFile.readLines()
                .mapNotNull { line ->
                    val trim = line.trim()
                    if (trim.startsWith("DEFAULT_API_KEY=")) trim.removePrefix("DEFAULT_API_KEY=").trim() else null
                }
                .firstOrNull() ?: ""
        } else ""
        buildConfigField("String", "DEFAULT_API_KEY", "\"$defaultApiKey\"")

        // Sertifika pinning: api.dienu.work için public key hash. Boş = pinning kapalı. Bkz. docs/CERT_PINNING.md
        val certPin = if (secretFile.exists()) {
            secretFile.readLines()
                .mapNotNull { line ->
                    val trim = line.trim()
                    if (trim.startsWith("CERT_PIN_API_DIENU_WORK=")) trim.removePrefix("CERT_PIN_API_DIENU_WORK=").trim() else null
                }
                .firstOrNull() ?: ""
        } else ""
        buildConfigField("String", "CERT_PIN_API_DIENU_WORK", "\"$certPin\"")
    }

    signingConfigs {
        create("release") {
            val secretFile = rootProject.file("secret.properties")
            if (!secretFile.exists()) return@create
            val props = Properties()
            secretFile.reader(Charsets.UTF_8).use { props.load(it) }
            val storeFilePath = props["STORE_FILE"]?.toString()?.trim() ?: return@create
            val storePassword = props["STORE_PASSWORD"]?.toString()?.trim() ?: return@create
            val keyAlias = props["KEY_ALIAS"]?.toString()?.trim() ?: return@create
            val keyPassword = props["KEY_PASSWORD"]?.toString()?.trim() ?: return@create
            val storeFileObj = rootProject.file(storeFilePath)
            if (!storeFileObj.exists()) return@create
            storeFile = storeFileObj
            this.storePassword = storePassword
            this.keyAlias = keyAlias
            this.keyPassword = keyPassword
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val releaseSigning = signingConfigs.findByName("release")
            if (releaseSigning != null && releaseSigning.storeFile != null) {
                signingConfig = releaseSigning
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
}

// Her release AAB'yi sürüm/build numarasına göre kopyala; eskisi kaybolmasın.
afterEvaluate {
    tasks.named("bundleRelease").configure {
        doLast {
            val versionName = android.defaultConfig.versionName ?: "unknown"
            val versionCode = android.defaultConfig.versionCode
            val buildDir = layout.buildDirectory.get().asFile
            val aabFile = buildDir.resolve("outputs/bundle/release/app-release.aab")
            if (aabFile.exists()) {
                val destDir = buildDir.resolve("release-bundles").resolve("$versionName-$versionCode")
                destDir.mkdirs()
                val destFile = destDir.resolve("app-release-$versionName-$versionCode.aab")
                aabFile.copyTo(destFile, overwrite = true)
                println("AAB kopyalandı: $destFile")
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.coil.compose)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.mlkit.barcode.scanning)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}