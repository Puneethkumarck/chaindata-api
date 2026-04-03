plugins {
    id("chaindata-api.client-library")
}

dependencies {
    api(project(":chaindata-api-api"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
}
