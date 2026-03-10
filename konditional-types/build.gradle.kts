plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter)
}

kotlin {
    sourceSets.named("main") {
        kotlin.srcDirs(
            "../konditional-core/src/main/kotlin",
        )
        kotlin.include("io/amichne/konditional/api/KonditionalInternalApi.kt")
        kotlin.include("io/amichne/konditional/context/**")
        kotlin.include("io/amichne/konditional/core/features/Identifiable.kt")
        kotlin.include("io/amichne/konditional/core/id/**")
        kotlin.include("io/amichne/konditional/core/result/**")
        kotlin.include("io/amichne/konditional/rules/predicate/PredicateRef.kt")
        kotlin.include("io/amichne/konditional/values/**")
    }
}
