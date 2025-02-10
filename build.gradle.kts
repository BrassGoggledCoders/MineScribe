plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.runtime") version "1.13.1"
    id("org.springframework.boot") version "3.3.1"
    groovy
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation("io.methvin:directory-watcher:0.18.0")
    implementation("com.dlsc.preferencesfx:preferencesfx-core:11.8.0")

    implementation(project(":Form"))
}

application {
    mainClass = "xyz.brassgoggledcoders.minescribe.MineScribeSpringApplication"
}

allprojects {
    apply(plugin = "org.openjfx.javafxplugin")
    apply(plugin = "org.beryx.runtime")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "groovy")

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
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("net.rgielen:javafx-weaver-spring-boot-starter:1.3.0")

        implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.17.1")

        implementation("org.slf4j:slf4j-api:2.0.13")
        implementation("org.jetbrains:annotations:24.0.0")
        implementation("io.vavr:vavr:0.10.4")

        implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
        implementation("org.kordamp.ikonli:ikonli-materialdesign2-pack:12.3.1")
        implementation("io.github.mkpaz:atlantafx-base:2.0.1")

        testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
        testImplementation("org.junit.platform:junit-platform-launcher")

    }

    runtime {
        options.addAll("--strip-debug", "--compress", "2", "--no-header-files")
        modules.addAll("javafx.controls", "javafx.fxml")
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()

        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
