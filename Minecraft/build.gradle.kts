import groovy.json.JsonSlurper
import java.net.URI

plugins {
    id("java")
    id("net.neoforged.gradle.userdev") version "7.0.120"
}

group = "xyz.brassgoggledcoders.minescribe"
version = "0.1.0"

val customDependencies = File("${rootProject.projectDir}/tmp/minecraft_deps.json")
var customDependenciesJson = emptyMap<String, Any>() as Map<*, *>
if (customDependencies.exists()) {
    customDependenciesJson = JsonSlurper().parseText(customDependencies.readText()) as Map<*, *>
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()

    mavenLocal()

    if (customDependenciesJson.containsKey("mavens")) {
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
}

dependencies {
    implementation("net.neoforged:neoforge:20.4.237")

    if (customDependenciesJson.containsKey("deObf")) {
        val deObf = customDependenciesJson["deObf"] as List<*>
        for (deObfValue in deObf) {
            implementation(deObfValue.toString())
        }
    }
    implementation(project(":Core"))
}

sourceSets {
    main {
        resources {
            srcDir("/src/generated/resources/")
        }
    }
}

minecraft {
    runs {
        configureEach {
            modSource(sourceSets.main.get())
            modSource(project(":Core").sourceSets.main.get())
            dependencies {
                runtime(project(":Core"))
                runtime("jakarta.inject:jakarta.inject-api:2.0.1")
            }
        }

        create("client") {

        }
    }
}