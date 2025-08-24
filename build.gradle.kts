plugins {
    id("java")
    id("groovy")
}

group = "com.ozdece"
version = "1.0-SNAPSHOT"

// Dependency versions
val reactorVersion = "3.7.9"
val jacksonVersion = "2.19.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.spockframework:spock-core:2.4-M6-groovy-4.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.slf4j:slf4j-api:2.0.17")
    testImplementation("io.projectreactor:reactor-test:$reactorVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("com.formdev:flatlaf:3.6.1")
    implementation("io.projectreactor:reactor-core:$reactorVersion")
    implementation("com.google.guava:guava:33.4.8-jre")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-guava:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("io.vavr:vavr:0.10.7")
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to version
        )
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("started", "passed", "skipped", "failed")
    }
}