import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.compose.internal.utils.getLocalProperty

fun DependencyHandlerScope.kapt(dependencyProvider : Provider<MinimalExternalModuleDependency>){
    add("kapt", dependencyProvider.get())
}

plugins {
    kotlin("kapt")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.gms)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.crashlytics)
    id("com.codingfeline.buildkonfig")
}

buildkonfig {
    packageName = "com.samadtch.bilinguai"

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
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
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
            //Compose
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

            //Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
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

            //Others
            implementation(libs.kotlinx.datetime)//DateTime
            implementation(libs.datastore)//DataStore
            implementation(libs.kamel)
        }
    }
}

android {
    namespace = "com.samadtch.inspired"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.samadtch.inspired"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 4
        versionName = "0.0.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        implementation(libs.androidx.material)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.palette)
        implementation(libs.androidx.security)
        implementation(libs.androidx.browser)
        implementation(libs.androidx.splashscreen)
        debugImplementation(compose.uiTooling)

        //Firebase
        implementation(project.dependencies.platform(libs.firebase.android.bom))
        implementation(libs.firebase.android.analytics)

        //Dependency Injection
        implementation(libs.koin.android)
        implementation(libs.hilt)
        implementation(libs.hilt.compose.navigation)
        kapt(libs.hilt.android.compiler)
        kapt(libs.hilt.compiler)
    }
}

