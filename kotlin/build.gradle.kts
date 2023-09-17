plugins {
    kotlin("jvm")
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint)
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}

// ktlint {
//    version.set("0.42.1")
// }

kotlin {
    explicitApi()
}

dependencies {
    api(projects.interfacesCore)

    // Needed for Paper extensions.
    compileOnly(projects.interfacesPaper)
    compileOnly(libs.adventure.api)
    compileOnly(libs.paper.api)
}
