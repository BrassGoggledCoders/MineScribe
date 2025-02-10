val groovyVersion: String = "4.0.24"

dependencies {
    implementation("org.apache.groovy:groovy:${groovyVersion}")
    implementation("org.apache.groovy:groovy-json:${groovyVersion}")
    implementation("org.apache.groovy:groovy-groovysh:${groovyVersion}")

    testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")
}

application {
    mainClass = "xyz.brassgoggledcoders.minescribe.form.test.FormTestApplication"
}