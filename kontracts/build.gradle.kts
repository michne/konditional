plugins {
    id("konditional.published-library")
}

konditionalPublishing {
    artifactId.set("kontracts")
    moduleName.set("Kontracts")
    moduleDescription.set("JSON schema and OpenAPI contract DSL support for Konditional.")
}

dependencies {
    implementation(kotlin("reflect"))

    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter)
}
