plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

javafx {
    version = "21.0.3"
    modules = listOf("javafx.controls", "javafx.fxml")
}

repositories {
    mavenCentral()

    flatDir(mapOf("name" to "libs", "dirs" to "$projectDir/libs"))
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("org.slf4j:slf4j-api:2.0.13")

    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("org.kordamp.ikonli:ikonli-material2-pack:12.3.1")

    implementation("com.dlsc.preferencesfx:preferencesfx-core:11.8.0")

    runtimeOnly("org.slf4j:slf4j-simple:2.0.13")
}

application {
    mainClass = "xyz.brassgoggledcoders.minescribe.MineScribe"
}

