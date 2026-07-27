plugins {
    `java-library`
}

dependencies {
    implementation(libs.edc.ih.spi)
    implementation(libs.edc.spi.participantcontext.config)

    testImplementation(libs.edc.lib.common.crypto)
    testImplementation(libs.edc.lib.keys)
    testImplementation(libs.edc.junit)
}