plugins {
    id("java")
    id("org.javamodularity.moduleplugin") version("1.8.12")
}

group = "xyz.brassgoggledcoders.minescribe"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        name = "Mojang"
        url = uri("https://libraries.minecraft.net")
    }
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.0")
    implementation("com.google.guava:guava:31.0-jre")
    implementation("com.mojang:datafixerupper:5.0.28")

    implementation("org.slf4j:slf4j-api:1.8.0-beta4")
    implementation("org.slf4j:jul-to-slf4j:1.8.0-beta4")

    implementation("org.graalvm.polyglot:polyglot:23.1.0")
    implementation("org.graalvm.polyglot:js-community:23.1.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    modularity.inferModulePath.set(false)
}