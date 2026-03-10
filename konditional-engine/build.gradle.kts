plugins {
    kotlin("jvm")
    `java-test-fixtures`
}

dependencies {
    api(project(":konditional-types"))
    api(project(":kontracts"))

    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter)
    testFixturesImplementation(project(":konditional-types"))
}

kotlin {
    sourceSets.named("main") {
        kotlin.srcDirs(
            "../konditional-core/src/main/kotlin",
            "../konditional-runtime/src/main/kotlin",
            "src/main/kotlin",
        )
        kotlin.include("io/amichne/konditional/api/**")
        kotlin.include("io/amichne/konditional/context/RampUp.kt")
        kotlin.include("io/amichne/konditional/core/**")
        kotlin.include("io/amichne/konditional/internal/**")
        kotlin.include("io/amichne/konditional/rules/**")
        kotlin.exclude("io/amichne/konditional/api/KonditionalInternalApi.kt")
        kotlin.exclude("io/amichne/konditional/context/AppLocale.kt")
        kotlin.exclude("io/amichne/konditional/context/Context.kt")
        kotlin.exclude("io/amichne/konditional/context/ContextKey.kt")
        kotlin.exclude("io/amichne/konditional/context/LocaleTag.kt")
        kotlin.exclude("io/amichne/konditional/context/Platform.kt")
        kotlin.exclude("io/amichne/konditional/context/PlatformTag.kt")
        kotlin.exclude("io/amichne/konditional/context/Version.kt")
        kotlin.exclude("io/amichne/konditional/context/axis/**")
        kotlin.exclude("io/amichne/konditional/core/features/Identifiable.kt")
        kotlin.exclude("io/amichne/konditional/core/id/**")
        kotlin.exclude("io/amichne/konditional/values/**")
    }
    sourceSets.named("testFixtures") {
        kotlin.srcDir("../konditional-core/src/testFixtures/kotlin")
    }
}

sourceSets.named("main") {
    resources.srcDir("../konditional-runtime/src/main/resources")
}
