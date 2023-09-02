plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven { url = uri("https://repo.eclipse.org/content/repositories/releases/") }
    maven { url = uri("https://repo.eclipse.org/content/repositories/snapshots/") }
    maven { url = uri("https://repo.eclipse.org/service/local/repositories/acceleo-releases/content/") }
    maven { url = uri("https://repo.eclipse.org/service/local/repositories/maven_central/content") }
}

dependencies {
    // emf
    implementation("org.eclipse.emf:org.eclipse.emf.common:2.9.2.v20131212-0545")
    implementation("org.eclipse.emf:org.eclipse.emf.ecore:2.9.2.v20131212-0545")
    implementation("org.eclipse.emf:org.eclipse.emf.ecore.xmi:2.9.1.v20131212-0545")
    implementation("org.eclipse.emf:org.eclipse.emf.mapping:2.13.0")

    // uml
    implementation("org.eclipse.uml2:uml:5.0.0-v20140602-0749")
    implementation("org.eclipse.uml2:common:2.0.0-v20140602-0749")
}