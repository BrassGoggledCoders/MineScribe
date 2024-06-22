import java.net.URI

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

sourceControl {
    gitRepository(URI("https://github.com/andy-goryachev/FxDock")) {
        producesModule("github.andy-goryachev:FxDock")
    }
}

rootProject.name = "MineScribe"
