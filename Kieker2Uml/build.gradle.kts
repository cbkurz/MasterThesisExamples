plugins {
    id("application")
    id ("java")
}

group = "org.kurz.ma.examples.kieker2uml"
version = "1.0-SNAPSHOT"

// Simplify the JAR's file name
var archivesBaseName = "Kieker2Uml"
version = "0.0.1"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
//    maven { url "https://repo.eclipse.org/" }
}

dependencies {
    // logging
    implementation ("ch.qos.logback:logback-classic:1.2.9")
    implementation ("org.slf4j:slf4j-api:1.7.30")

    // cli - https://jcommander.org/
    implementation ("com.beust", "jcommander", "1.82")

    // architecture
    implementation ("de.cau.cs.se.teetime:teetime:3.1.0")

    // emf / ecore / Uml2
    implementation(fileTree("Uml2Libs"))

    // kieker
    implementation ("net.kieker-monitoring:kieker:2.0.0-SNAPSHOT")

    // utility
    // https://mvnrepository.com/artifact/io.vavr/vavr
    implementation ("io.vavr", "vavr", "0.10.4")
    // https://mvnrepository.com/artifact/commons-io/commons-io
    implementation ("commons-io", "commons-io", "2.13.0")

    // test
    platform("org.junit:junit-bom:5.9.1")
    testImplementation ("org.junit.jupiter:junit-jupiter")
}

var mainClassName = "org.kurz.ma.examples.kieker2uml.Kieker2UmlMain"

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = mainClassName
    }
}