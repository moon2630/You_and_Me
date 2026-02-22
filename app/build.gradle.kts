plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.uptrend"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.uptrend"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("androidx.navigation:navigation-fragment:2.7.5")
    implementation("androidx.navigation:navigation-ui:2.7.5")
    implementation("androidx.activity:activity:1.10.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(project(":Adapter And DataModel"))
    implementation ("com.intuit.sdp:sdp-android:1.1.0")
    implementation ("com.intuit.ssp:ssp-android:1.1.0")
    implementation("com.github.ybq:Android-SpinKit:1.4.0")
    implementation("io.github.muddz:styleabletoast:2.4.0")
    implementation("com.airbnb.android:lottie:3.4.0")
    implementation ("com.google.android.material:material:1.0.0")
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("com.github.Theophrast:SquareImageView:1.0.1")
    implementation ("com.google.android.material:material:1.2.0")
    implementation ("com.razorpay:checkout:1.6.26")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
//    implementation ("com.github.Inconnu08:android-ratingreviews:1.2.0")
    implementation("com.github.jd-alexander:LikeButton:0.2.3")
    implementation ("androidx.biometric:biometric:1.1.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.google.firebase:firebase-database:20.3.0")
    implementation ("com.google.firebase:firebase-storage:20.3.0")

}