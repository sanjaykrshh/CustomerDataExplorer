plugins {
    id("java")
    id("com.gradleup.shadow") version "9.2.2"
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
}
group = "customer.data"
version = "1.0-SNAPSHOT"


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // Spring Boot Web for local REST API
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring Cloud Function core + AWS adapter
    implementation("org.springframework.cloud:spring-cloud-function-context:4.1.2")
    implementation("org.springframework.cloud:spring-cloud-function-adapter-aws:4.1.2")


    // AWS API Gateway event POJOs
    implementation("com.amazonaws:aws-lambda-java-events:3.13.0")


    //logging
    implementation("org.slf4j:slf4j-api:2.0.13")


    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}


tasks.shadowJar {
    archiveBaseName.set("CustomerDataExplorer")
    archiveClassifier.set("all")
    mergeServiceFiles() // Ensures META-INF service files are merged properly
    manifest {
        attributes["Main-Class"] = "org.springframework.cloud.function.adapter.aws.FunctionInvoker"
    }
}
