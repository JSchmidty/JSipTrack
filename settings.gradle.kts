rootProject.name = "SipTrack"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(":shared")
include(":composeApp")
include(":androidApp")
include(":iosApp")
include(":wearApp")
include(":watchosApp")
