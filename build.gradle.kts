buildscript {
    val hiltVersion = "2.33-beta"
//    val kotlinVersion by extra("1.4.31")
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
//        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.0-rc")
    }
}




allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
