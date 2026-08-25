import java.util.UUID

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
}

// ---------------------------------------------------------------------------
// Debug keystore: generated automatically on first build with keytool.
// No binary keystore files are stored in the repository; all generation
// values below are plain text inside this file.
// ---------------------------------------------------------------------------
val debugStorePassword = UUID.randomUUID().toString()
val debugKeyAlias = "EgyptologyDebugKey"
val debugKeyPassword = UUID.randomUUID().toString()

val debugKeystoreFile = file("$rootDir/debug.keystore")

if (!debugKeystoreFile.exists()) {
  val keytool = file("${System.getProperty("java.home")}/bin/keytool").absolutePath
  val process = ProcessBuilder(
    keytool, "-genkeypair",
    "-keystore", debugKeystoreFile.absolutePath,
    "-storepass", debugStorePassword,
    "-keypass", debugKeyPassword,
    "-alias", debugKeyAlias,
    "-keyalg", "RSA",
    "-keysize", "2048",
    "-validity", "10000",
    "-dname", "CN=Android Debug,O=Android,C=US"
  ).redirectErrorStream(true).start()
  val output = process.inputStream.bufferedReader().readText()
  if (process.waitFor() == 0) {
    logger.lifecycle("Generated debug.keystore.")
  } else {
    logger.error("Failed to generate debug.keystore: $output")
  }
}

android {
  namespace = "com.negm.egyptology"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.negm.egyptology"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
  }

  signingConfigs {
    create("debugConfig") {
      storeFile = debugKeystoreFile
      storePassword = debugStorePassword
      keyAlias = debugKeyAlias
      keyPassword = debugKeyPassword
    }
    create("release") {
      val envKeystore = System.getenv("KEYSTORE_PATH")
      val keyFile = when {
        envKeystore != null && file(envKeystore).exists() -> file(envKeystore)
        file("${rootDir}/my-upload-key.jks").exists() -> file("${rootDir}/my-upload-key.jks")
        else -> null
      }
      if (keyFile != null) {
        storeFile = keyFile
        storePassword = System.getenv("STORE_PASSWORD") ?: debugStorePassword
        keyAlias = System.getenv("KEY_ALIAS") ?: debugKeyAlias
        keyPassword = System.getenv("KEY_PASSWORD") ?: debugKeyPassword
      } else {
        // Fall back to the auto-generated debug.keystore so CI builds always sign.
        storeFile = debugKeystoreFile
        storePassword = debugStorePassword
        keyAlias = debugKeyAlias
        keyPassword = debugKeyPassword
      }
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      isShrinkResources = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }

  packaging {
    resources {
      excludes += setOf(
        "META-INF/DEPENDENCIES",
        "META-INF/LICENSE",
        "META-INF/LICENSE.txt",
        "META-INF/license.txt",
        "META-INF/NOTICE",
        "META-INF/NOTICE.txt",
        "META-INF/notice.txt",
        "META-INF/*.kotlin_module",
        "META-INF/AL2.0",
        "META-INF/LGPL2.1"
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  debugImplementation(libs.androidx.compose.ui.tooling)
}
