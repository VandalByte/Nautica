plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.dev.nautica"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dev.nautica"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // google sign-in dependencies
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    // recyclerview
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    // material theme
    implementation("com.google.android.material:material:1.12.0")
    // room db
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    // glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation ("androidx.annotation:annotation:1.3.0")
    //viewmodel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    // google maps
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.maps.android:android-maps-utils:2.4.0")
    implementation("com.google.android.libraries.places:places:3.5.0")
    implementation("androidx.fragment:fragment-ktx:1.5.7")
    implementation ("com.google.android.material:material:1.9.0")
    // API stuff
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
}