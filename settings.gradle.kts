rootProject.name = "MineScribe"

include("Editor")
include("Core")
include("Minecraft")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.minecraftforge.net") {
            name = "Forge"
        }
        maven("https://maven.parchmentmc.org") {
            name = "ParchmentMC"
        }
    }
}
