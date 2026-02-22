plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.uptrenddelivery"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.uptrenddelivery"
        minSdk = 24
        targetSdk = 35          // ← Change from 34 to 35 (MATCH compileSdk)
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("androidx.activity:activity:1.8.2")  // Updated to latest stable
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(project(":Adapter And DataModel"))
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    implementation("com.github.ybq:Android-SpinKit:1.4.0")
    implementation("io.github.muddz:styleabletoast:2.4.0")
    implementation("com.airbnb.android:lottie:6.1.0")  // Updated to latest stable
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("androidx.biometric:biometric:1.1.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.google.firebase:firebase-database:20.3.0")
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Google Maps & Location
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")  // Latest
}