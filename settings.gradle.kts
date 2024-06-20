rootProject.name = "MineScribe"

include("Editor")
include("Core")
include("Minecraft")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases") {
            name = "NeoForge"
        }
    }
}
