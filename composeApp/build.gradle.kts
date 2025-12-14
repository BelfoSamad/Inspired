import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.compose.internal.utils.getLocalProperty

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.gms)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.crashlytics)
    id("com.codingfeline.buildkonfig")
}

buildkonfig {
    packageName = "com.samadtch.inspired"

    // default config is required
    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "BASE_URL", getLocalProperty("BASE_URL").toString())
        buildConfigField(FieldSpec.Type.STRING, "AUTH_URL", getLocalProperty("AUTH_URL").toString())
        buildConfigField(FieldSpec.Type.STRING, "REDIRECT_URL", getLocalProperty("REDIRECT_URL").toString())
        buildConfigField(FieldSpec.Type.STRING, "SCOPES", getLocalProperty("SCOPES").toString())
        buildConfigField(FieldSpec.Type.STRING, "CLIENT_ID", getLocalProperty("CLIENT_ID").toString())
        buildConfigField(FieldSpec.Type.STRING, "CLIENT_SECRET", getLocalProperty("CLIENT_SECRET").toString())
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            //Firestore
            implementation(project.dependencies.platform(libs.firebase.android.bom))
            implementation(libs.firebase.android.config)
            implementation(libs.firebase.android.crashlytics)

            //Koin
            implementation(libs.koin.android)

            //Others
            implementation(libs.review.ktx)
        }
        commonMain.dependencies {
            //Kotlin
            implementation(libs.kotlinx.datetime)//DateTime
            implementation(libs.kotlinx.serialization)//Serialization

            //Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation.compose)

            //Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            //Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            //Others
            implementation(libs.datastore)//DataStore
            implementation(libs.kamel)
        }
    }
}

android {
    namespace = "com.samadtch.inspired"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.samadtch.inspired"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 100
        versionName = "1.0.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
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
}

dependencies {
    debugImplementation(compose.uiTooling)

    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.material)
    implementation(libs.androidx.palette)
    implementation(libs.androidx.security)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.splashscreen)

    //Firebase
    implementation(project.dependencies.platform(libs.firebase.android.bom))
    implementation(libs.firebase.android.analytics)

    //Dependency Injection
    implementation(libs.hilt)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.compose.navigation)
}

