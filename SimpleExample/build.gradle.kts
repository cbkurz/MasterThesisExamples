plugins {
    id("java")
}

group = "org.kurz.ma.examples.simple"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

java {
    // AspectJ uses reflections which are no longer allowed after Java version 11
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

var mainClassName = "org.kurz.ma.examples.simple.Main"

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
    jvmArgs = listOf("-javaagent:../Bookstore/libs/kieker-1.15.2-aspectj.jar",
            "-Dorg.aspectj.weaver.showWeaveInfo=true",
            "-Daj.weaving.verbose=true")
}
