buildscript {
    val hiltVersion = "2.33-beta"
//    val kotlinVersion by extra("1.4.31")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
