buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
        // AGP sürümünü buraya ekleyin
        classpath("com.android.tools.build:gradle:8.2.2")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // AGP sürümünü buraya ekleyin
    id("com.android.application") version "8.2.2" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}
