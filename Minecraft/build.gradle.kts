plugins {
    id("java")
    id("net.minecraftforge.gradle") version("5.1.+")
    id("org.parchmentmc.librarian.forgegradle") version ("1.+")
    id("org.spongepowered.mixin") version("0.7-SNAPSHOT")
}

group = "xyz.brassgoggledcoders.minescribe"
version = "0.1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    "minecraft"("net.minecraftforge:forge:1.19.2-43.2.0")

    implementation(project(":Core"))

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

minecraft {
    mappings("parchment", "2022.11.27-1.19.2")

    runs {
        create("client") {
            taskName("client")
            workingDirectory(project.file("run/client"))
            ideaModule("${rootProject.name}.${project.name}.main")
            mods {
                create("minescribe") {
                    source(sourceSets.main.get())
                    source(project(":Core").sourceSets.main.get())
                }
            }
        }
    }
}

mixin {
    add(sourceSets.main.get(), "minescribe.refmap.json")
    config("minescribe.mixins.json")
}