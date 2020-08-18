plugins {
    id("com.gradle.enterprise") version "3.1"
    id("net.vivin.gradle-semantic-build-versioning") version "4.0.0"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"

        publishAlways()
    }
}

include(":fastpermissions")
rootProject.name = "FastPermissions"