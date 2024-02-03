import groovy.json.JsonSlurper
import java.net.URI

plugins {
    id("java")
    id("net.minecraftforge.gradle") version ("5.1.+")
    id("org.parchmentmc.librarian.forgegradle") version ("1.+")
    id("org.spongepowered.mixin") version ("0.7-SNAPSHOT")
}

group = "xyz.brassgoggledcoders.minescribe"
version = "0.1.0"

val customDependencies = File("${rootProject.projectDir}/tmp/minecraft_deps.json")
val customDependenciesJson = JsonSlurper().parseText(customDependencies.readText()) as Map<*, *>

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()

    mavenLocal()

    val mavens = customDependenciesJson["mavens"] as? Map<*, *>
    if (mavens != null) {
        for ((nameValue, urlValue) in mavens) {
            maven {
                name = nameValue.toString()
                url = URI(urlValue.toString())
            }
        }
    }
}

dependencies {
    "minecraft"("net.minecraftforge:forge:1.19.2-43.2.0")

    val deObf = customDependenciesJson["deObf"] as List<*>
    for (deObfValue in deObf) {
        implementation(fg.deobf(deObfValue.toString()))
    }

    implementation(project(":Core"))

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

sourceSets {
    main {
        resources {
            srcDir("/src/generated/resources/")
        }
    }
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
        create("data") {
            taskName("data")
            workingDirectory(project.file("run/data"))
            ideaModule("${rootProject.name}.${project.name}.main")
            args.addAll(
                listOf(
                    "--mod", "minescribe",
                    "--all",
                    "--output", file("src/generated/resources/").toString(),
                    "--existing", file("src/main/resources/").toString()
                )
            )
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