plugins {
    kotlin("jvm") version "1.8.0"

}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation (kotlin("stdlib"))
    testImplementation ("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation ("com.codeborne:selenide:7.5.0")
    //testRuntimeOnly ("org.slf4j:slf4j-simple:1.7.32")
    testImplementation ("org.slf4j:slf4j-simple:2.0.9") // Add this line
    testImplementation ("io.github.bonigarcia:webdrivermanager:5.5.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}