plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.gmwapp.dudeways"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gmwapp.dudeways"
        minSdk = 24
        targetSdk = 34
        versionCode = 25
        versionName = "25.0"

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
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
        exclude("META-INF/*.kotlin_module")
        exclude("META-INF/androidx.cardview_cardview.version")
    }

    dataBinding {
        enable = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.play.services.location)
    implementation(libs.androidx.work.runtime)
    implementation(libs.play.services.fido)
    val lifecycle_version = "2.6.2"
    val glideVersion = "4.11.0"


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //glide
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    annotationProcessor("com.github.bumptech.glide:compiler:$glideVersion")

    //viewmodel
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")

    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    //coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")


    //di
    implementation("com.github.bumptech.glide:glide:4.15.0")
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    kapt("androidx.hilt:hilt-compiler:1.1.0")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Retrofit
    implementation("com.squareup.okhttp3:logging-interceptor:4.5.0")


    //imagepicker
    implementation("androidx.activity:activity-ktx:1.8.1")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("com.github.dhaval2404:imagepicker:2.1")


    //Dimen
    implementation("com.intuit.ssp:ssp-android:1.0.6")
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    //circleimageview
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //circlePageIndicator
    implementation("me.relex:circleindicator:2.1.6")

    implementation("com.zoho.salesiq:mobilisten:7.0.0")

    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.firebase:firebase-ml-vision:24.0.1")
    implementation("com.google.firebase:firebase-ml-vision-face-model:19.0.0")
    implementation("com.google.firebase:firebase-iid:21.1.0")


    implementation("com.github.CanHub:Android-Image-Cropper:3.1.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")


    //  implementation("com.github.mmoamenn:LuckyWheel_Android:0.1.2")
    implementation("com.google.android.gms:play-services-auth:18.1.0")
    implementation("com.onesignal:OneSignal:[5.0.0, 5.99.99]")
    // implementation("com.google.android.gms:play-services-ads:21.0.0+")

    implementation("com.google.android.gms:play-services-ads:21.0.0+")


    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))

    implementation("com.google.androidbrowserhelper:androidbrowserhelper:2.5.0")

    implementation ("com.github.aabhasr1:OtpView:v1.1.2")


    //implementation ("com.vanniktech:emoji-google:0.21.0")
    implementation ("com.vanniktech:emoji:0.7.0")
    implementation ("com.vanniktech:emoji-google:0.7.0")




}