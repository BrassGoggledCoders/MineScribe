plugins {
    id("java")
}

group = "xyz.brassgoggledcoders.minescribe"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.netty:netty-buffer:4.1.86.Final")
    implementation("io.netty:netty-codec:4.1.86.Final")
    implementation("io.netty:netty-handler:4.1.86.Final")
    implementation("io.netty:netty-transport:4.1.86.Final")

    implementation("org.jetbrains:annotations:24.0.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}