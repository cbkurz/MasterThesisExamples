plugins {
    id("java")
}

group = "org.kurz.ma.examples.uml2plantuml"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // logging
    implementation ("ch.qos.logback:logback-classic:1.2.9")
    implementation ("org.slf4j:slf4j-api:1.7.30")

    implementation(fileTree("../Kieker2Uml/Uml2Libs"))

}

tasks.test {
    useJUnitPlatform()
}