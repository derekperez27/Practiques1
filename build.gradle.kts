
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.dominio.formularioapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dominio.formularioapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ▼▼▼ BLOQUE PARA LEER LA API KEY DE FORMA SEGURA ▼▼▼
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.reader())
        }

        // ▼▼▼ LÍNEA CON LA SOLUCIÓN DEFINITIVA ▼▼▼
        // Le pasamos el valor a buildConfigField con las comillas escapadas (\"...\")
        // para que el archivo BuildConfig.java generado contenga un String válido.
        buildConfigField("String", "NEWS_API_KEY", "\"${localProperties.getProperty("NEWS_API_KEY", "")}\"")

        // ▲▲▲ FIN DE LA CORRECCIÓN ▲▲▲
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true // Permite que se genere el archivo BuildConfig
    }
}

dependencies {
    // Dependencias del catálogo
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Dependencias manuales que ya tenías
    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Room para base de datos local
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version") // Para usar Coroutines (programación asíncrona)
    kapt("androidx.room:room-compiler:$room_version")   // El compilador de anotaciones de Room


    // ▼▼▼ DEPENDENCIAS AÑADIDAS PARA LA API DE NOTICIAS (RETROFIT) ▼▼▼
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    // ▲▲▲ FIN DE LAS DEPENDENCIAS AÑADIDAS ▲▲▲


    // Dependencias de testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
