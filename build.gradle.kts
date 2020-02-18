import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.61"
    id("org.asciidoctor.convert") version "1.5.3"

}

group = "io.github.dgahn"
version = "1.0.0"

ext {
    set("snippetsDir", file("build/generated-snippets"))
}

application {
    mainClassName = "io.github.dgahn.Application"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    val ktorVersion = "1.3.0"
    val junit5Version = "5.6.0"
    val springRestDocsVersion = "2.0.4.RELEASE"
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.github.microutils:kotlin-logging:1.7.8")

    asciidoctor("org.springframework.restdocs:spring-restdocs-asciidoctor:$springRestDocsVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
    testImplementation("org.springframework.restdocs:spring-restdocs-restassured:$springRestDocsVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
        showStackTraces = true
        showExceptions = true
        showCauses = true
        showStandardStreams = true
    }

    outputs.dir(ext["snippetsDir"] as File)
}

tasks.asciidoctor {
    inputs.dir(ext["snippetsDir"] as File)
    dependsOn(tasks.test)
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
    version = "5.5.1"
}