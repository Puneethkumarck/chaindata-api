plugins {
    id("chaindata-api.api-library")
}

dependencies {
    api("jakarta.validation:jakarta.validation-api")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    api("com.fasterxml.jackson.core:jackson-annotations")
}
