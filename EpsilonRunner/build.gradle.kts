plugins {
    id("java")
}

group = "de.kurz.ma.epsilon.runner"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.eclipse.epsilon:org.eclipse.epsilon.emc.emf:2.4.0")
    implementation("org.eclipse.epsilon:org.eclipse.epsilon.eol.engine:2.4.0")
    implementation("org.eclipse.epsilon:org.eclipse.epsilon.etl.engine:2.4.0")
    implementation("org.eclipse.epsilon:org.eclipse.epsilon.egl.engine:2.4.0")

    implementation(fileTree("../Kieker2Uml/Uml2Libs"))

    implementation ("com.beust", "jcommander", "1.82")
}

tasks.test {
    useJUnitPlatform()
}