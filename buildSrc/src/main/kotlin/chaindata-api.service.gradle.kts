plugins {
    java
    `java-test-fixtures`
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.diffplug.spotless")
    id("com.google.cloud.tools.jib")
}

group = "com.chaindata"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.1.1")
    }
}

spotless {
    java {
        removeUnusedImports()
        importOrder("java|javax", "jakarta", "org", "com", "")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

jib {
    from {
        image = "eclipse-temurin:25-jre-alpine"
    }
    to {
        image = "chaindata/chaindata-api"
        tags = setOf(version.toString(), "latest")
    }
    container {
        jvmFlags = listOf(
            "-XX:+UseContainerSupport",
            "-XX:MaxRAMPercentage=75.0"
        )
        ports = listOf("8080", "8081")
        user = "1000:1000"
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf(
        "-parameters",
        "-Amapstruct.defaultComponentModel=spring",
        "-Amapstruct.defaultInjectionStrategy=constructor",
        "-Amapstruct.unmappedTargetPolicy=ERROR"
    ))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// --- Integration Test source set ---

val integrationTest by sourceSets.creating {
    java.srcDir("src/integration-test/java")
    resources.srcDir("src/integration-test/resources")
    resources.srcDir("src/test/resources")
    compileClasspath += sourceSets.main.get().output + sourceSets.testFixtures.get().output
    runtimeClasspath += sourceSets.main.get().output + sourceSets.testFixtures.get().output
}

configurations[integrationTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
configurations[integrationTest.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())
configurations[integrationTest.compileOnlyConfigurationName].extendsFrom(configurations.testCompileOnly.get())
configurations[integrationTest.annotationProcessorConfigurationName].extendsFrom(configurations.testAnnotationProcessor.get())

val integrationTestTask = tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"
    useJUnitPlatform()
    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath
    shouldRunAfter(tasks.test)
}

// --- Architecture Test source set ---

val architectureTest by sourceSets.creating {
    java.srcDir("src/architecture-test/java")
    resources.srcDir("src/architecture-test/resources")
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

configurations[architectureTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
configurations[architectureTest.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())
configurations[architectureTest.compileOnlyConfigurationName].extendsFrom(configurations.testCompileOnly.get())
configurations[architectureTest.annotationProcessorConfigurationName].extendsFrom(configurations.testAnnotationProcessor.get())

val architectureTestTask = tasks.register<Test>("architectureTest") {
    description = "Runs architecture tests."
    group = "verification"
    useJUnitPlatform()
    testClassesDirs = architectureTest.output.classesDirs
    classpath = architectureTest.runtimeClasspath
    shouldRunAfter(integrationTestTask)
}

val businessTest by sourceSets.creating {
    java.srcDir("src/business-test/java")
    resources.srcDir("src/business-test/resources")
    resources.srcDir("src/test/resources")
    compileClasspath += sourceSets.main.get().output + sourceSets.testFixtures.get().output
    runtimeClasspath += sourceSets.main.get().output + sourceSets.testFixtures.get().output
}

configurations[businessTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
configurations[businessTest.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())
configurations[businessTest.compileOnlyConfigurationName].extendsFrom(configurations.testCompileOnly.get())
configurations[businessTest.annotationProcessorConfigurationName].extendsFrom(configurations.testAnnotationProcessor.get())

val businessTestTask = tasks.register<Test>("businessTest") {
    description = "Runs business/E2E tests."
    group = "verification"
    useJUnitPlatform()
    testClassesDirs = businessTest.output.classesDirs
    classpath = businessTest.runtimeClasspath
    shouldRunAfter(integrationTestTask)
}

tasks.check {
    dependsOn(integrationTestTask, architectureTestTask, businessTestTask)
}
