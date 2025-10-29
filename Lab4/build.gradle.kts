// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.kotlin.serialization) apply false
    // [KSP] Enabling KSP plugin for Room DB for whole project
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
}
