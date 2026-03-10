plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":konditional-engine"))

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)
    implementation(kotlin("reflect"))

    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter)
    testImplementation(testFixtures(project(":konditional-engine")))
}

kotlin {
    sourceSets.named("main") {
        kotlin.srcDirs(
            "../konditional-serialization/src/main/kotlin",
            "../konditional-runtime/src/main/kotlin",
            "src/main/kotlin",
        )
        kotlin.include("io/amichne/konditional/internal/serialization/**")
        kotlin.include("io/amichne/konditional/serialization/**")
        kotlin.include("io/amichne/konditional/serialization/NamespaceExtensions.kt")
        kotlin.exclude("io/amichne/konditional/serialization/instance/Configuration.kt")
        kotlin.exclude("io/amichne/konditional/runtime/**")
    }
}
