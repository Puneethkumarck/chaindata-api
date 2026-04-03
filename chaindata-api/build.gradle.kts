plugins {
    id("chaindata-api.service")
}

dependencies {
    implementation(project(":chaindata-api-api"))

    // Spring WebFlux (Netty)
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // R2DBC + TimescaleDB
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("org.postgresql:r2dbc-postgresql")

    // Flyway (JDBC at startup, before reactive context)
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)
    runtimeOnly("org.postgresql:postgresql")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // Reactive Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    // Spring Cloud Stream + Kafka
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")

    // Resilience4j
    implementation(libs.resilience4j.spring.boot)
    implementation(libs.resilience4j.reactor)

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // MapStruct
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // Logging
    implementation(libs.logstash.logback.encoder)

    // --- Test dependencies ---
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // ArchUnit
    testImplementation(libs.archunit)

    // TestContainers
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.kafka)
    testImplementation(libs.testcontainers.r2dbc)

    // WireMock
    testImplementation(libs.wiremock)

    // Awaitility
    testImplementation(libs.awaitility)
}
