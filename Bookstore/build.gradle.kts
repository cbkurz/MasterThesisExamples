plugins {
    id("application")
}

group = "kieker.examples.monitoring.aspectj"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

val kiekerVersion = "1.15.2"
dependencies {
    implementation("ch.qos.logback:logback-classic:1.1.7")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.codehaus.groovy:groovy-all:3.0.2")
    implementation("net.kieker-monitoring:kieker:${kiekerVersion}")


    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
java {
    // AspectJ uses reflections which are no longer allowed after Version 11 (11 is the last long term support version)
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

var mainClassName = "kieker.examples.monitoring.aspectj.BookstoreStarter"


tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = mainClassName
    }
}

tasks.register("runMonitoring", JavaExec::class) {
    mainClass = mainClassName
    classpath = sourceSets["main"].runtimeClasspath
    jvmArgs = listOf("-Dkieker.monitoring.writer.filesystem.FileWriter.customStoragePath=monitoring-logs",
            "-javaagent:lib/kieker-${kiekerVersion}-aspectj.jar",
            "-Dorg.aspectj.weaver.showWeaveInfo=true",
            "-Daj.weaving.verbose=true")
}

