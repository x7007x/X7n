import java.io.File

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
}

// ---------------------------------------------------------------------------
// Debug keystore: generated automatically at build time with keytool using the
// FIXED credentials below. No binary keystore is stored in the repository and
// nothing is regenerated per build - once created, the same keystore keeps
// opening with these exact values on every machine.
// ---------------------------------------------------------------------------
// PKCS12 keystores encrypt the key with the STORE password, so both
// credentials must be the exact same fixed value.
val debugStorePassword = "cb0a5db4-8356-4bd5-830c-bcac1c058525"
val debugKeyAlias = "EgyptologyDebugKey"
val debugKeyPassword = debugStorePassword

val debugKeystoreFile = rootProject.file("debug.keystore")

// Generates the keystore on first build only. A dedicated task class with
// injected properties keeps it compatible with the configuration cache.
abstract class GenerateDebugKeystoreTask : DefaultTask() {
  @get:Internal abstract val keystoreFile: RegularFileProperty
  @get:Internal abstract val keyAlias: Property<String>
  @get:Internal abstract val storePassword: Property<String>
  @get:Internal abstract val keyPassword: Property<String>

  @TaskAction
  fun generate() {
    val keytool = File(System.getProperty("java.home"), "bin/keytool").absolutePath
    val process = ProcessBuilder(
      keytool, "-genkeypair",
      "-keystore", keystoreFile.get().asFile.absolutePath,
      "-storepass", storePassword.get(),
      "-keypass", keyPassword.get(),
      "-alias", keyAlias.get(),
      "-keyalg", "RSA",
      "-keysize", "2048",
      "-validity", "10000",
      "-dname", "CN=Android Debug,O=Android,C=US"
    ).redirectErrorStream(true).start()
    val output = process.inputStream.bufferedReader().readText()
    if (process.waitFor() == 0) {
      println("Generated debug.keystore.")
    } else {
      throw GradleException("Failed to generate debug.keystore: $output")
    }
  }
}

val generateDebugKeystore = tasks.register<GenerateDebugKeystoreTask>("generateDebugKeystore") {
  group = "build"
  description = "Generates debug.keystore with keytool if it does not exist."
  keystoreFile.set(debugKeystoreFile)
  keyAlias.set(debugKeyAlias)
  storePassword.set(debugStorePassword)
  keyPassword.set(debugKeyPassword)
  onlyIf { !keystoreFile.get().asFile.exists() }
}

// Signing happens in packaging/validation tasks - make sure they run after generation.
tasks.matching {
  it.name.startsWith("package") || it.name.startsWith("validateSigning")
}.configureEach {
  dependsOn(generateDebugKeystore)
}

android {
  namespace = "com.negm.egyptology"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.negm.anix"
    minSdk = 24
    targetSdk = 36
    versionCode = 7
    versionName = "1.6"
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
        !envKeystore.isNullOrBlank() && file(envKeystore).exists() -> file(envKeystore)
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
      // Tiny build: R8 code shrinking + resource shrinking strip all unused
      // Compose icons/classes and cut the APK size dramatically.
      isMinifyEnabled = true
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
