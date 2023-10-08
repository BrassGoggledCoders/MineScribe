plugins {
    id("java")
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
    implementation("io.netty:netty-handler:4.1.86.Final")

    implementation("org.jetbrains:annotations:24.0.0")
    implementation("com.mojang:datafixerupper:5.0.28")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}