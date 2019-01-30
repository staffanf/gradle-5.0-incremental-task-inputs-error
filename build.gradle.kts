plugins {
    groovy
    `java-gradle-plugin`
}

repositories {
    jcenter()
}

dependencies {
    compile(gradleApi())
    compile(localGroovy())
    testImplementation("org.spockframework:spock-core:1.0-groovy-2.4") {
        exclude(mapOf("module" to "groovy-all"))
    }
    testImplementation("junit:junit:4.12")
}

gradlePlugin {
    (plugins) {
        create("test.plugin") {
            id = "test.plugin"
            implementationClass = "inctaskinputs.error.TestPlugin"
        }
    }
}
