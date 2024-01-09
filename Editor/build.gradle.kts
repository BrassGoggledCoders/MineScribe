plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version ("1.8.12")
    id("org.openjfx.javafxplugin") version ("0.1.0")
    id("org.beryx.jlink") version ("2.25.0")
}

val junitVersion: String = "5.9.2"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

javafx {
    version = "20.0.1"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainModule.set("xyz.brassgoggledcoders.minescribe.editor")
    mainClass.set("xyz.brassgoggledcoders.minescribe.editor.Application")
}

repositories {
    mavenCentral()
    maven {
        name = "SonaType"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    maven {
        name = "Mojang"
        url = uri("https://libraries.minecraft.net")
    }
}

dependencies {
    implementation("org.controlsfx:controlsfx:11.1.2")
    implementation("com.dlsc.formsfx:formsfx-core:11.6.0") {
        exclude(group = "org.openjfx")
    }
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")

    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("org.kordamp.ikonli:ikonli-feather-pack:12.3.1")

    implementation("io.github.mkpaz:atlantafx-base:2.0.1")
    implementation("io.github.mkpaz:atlantafx-styles:2.0.1")

    implementation("com.google.code.gson:gson:2.10.1")

    @Suppress("VulnerableLibrariesLocal", "RedundantSuppression")
    implementation("com.google.inject:guice:7.0.0")
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")

    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("org.slf4j:jul-to-slf4j:2.0.9")

    implementation("org.graalvm.polyglot:polyglot:23.1.0")
    implementation("org.graalvm.polyglot:js-community:23.1.0")

    implementation("org.jetbrains:annotations:24.0.0")

    @Suppress("VulnerableLibrariesLocal", "RedundantSuppression")
    implementation("com.mojang:datafixerupper:5.0.28")

    implementation(project(":Core")) {
        exclude(group = "io.netty")
    }


    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(project.file("${buildDir}/distributions/app-${javafx.platform.classifier}.zip"))
    options.addAll(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "Minescribe"
    }
}