plugins {
    kotlin("jvm")
    alias(libs.plugins.dokka)
    alias(libs.plugins.shadow)
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(projects.interfacesMinestom)

    implementation(libs.minestom)
}
