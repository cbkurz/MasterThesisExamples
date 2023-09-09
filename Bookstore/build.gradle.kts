plugins {
    id("application")
}

group = "kieker.examples.monitoring.aspectj"
version = "1.0-SNAPSHOT"

// Configuration properties
val kiekerVersion = "1.15.2"
var mainClassName = "kieker.examples.monitoring.application.Main"


repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
}

java {
    // AspectJ uses reflections which are no longer allowed after Version 11 (11 is the last long term support version)
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}



tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = mainClassName
    }
}

tasks.register("runMonitoring", JavaExec::class) {
    mainClass = mainClassName
    classpath = sourceSets["main"].runtimeClasspath
/*
     Check the "src/main/resources/META-INF/" folder for the following two configuration files:
     * kieker.monitoring.properties - for more configurations properties like the time config and where logs are written to
     * aop.xml - For the used aspect configurations and the included/excluded packages for monitoring.
*/
    jvmArgs = listOf("-javaagent:libs/kieker-1.15.2-aspectj.jar",
            "-Dorg.aspectj.weaver.showWeaveInfo=true",
            "-Daj.weaving.verbose=true")
}

