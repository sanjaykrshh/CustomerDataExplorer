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

    // Spring Boot starter (Core only, NO WEB)
    implementation("org.springframework.boot:spring-boot-starter") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }

    // Spring Cloud Function core + AWS adapter
    implementation("org.springframework.cloud:spring-cloud-function-context:4.1.2") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
        exclude(group = "org.springframework", module = "spring-web")
        exclude(group = "org.springframework", module = "spring-webmvc")
    }
    implementation("org.springframework.cloud:spring-cloud-function-adapter-aws:4.1.2") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
        exclude(group = "org.springframework", module = "spring-web")
        exclude(group = "org.springframework", module = "spring-webmvc")
    }

    // AWS API Gateway event POJOs
    implementation("com.amazonaws:aws-lambda-java-events:3.13.0")

    // JSON - Jackson for serialization (managed by Spring Boot)
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")


    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")


    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

// Configure shadowJar for AWS Lambda deployment
tasks.shadowJar {
    archiveBaseName.set("CustomerDataExplorer")
    archiveClassifier.set("")

    // Merge service files for Spring
    mergeServiceFiles()
    append("META-INF/spring.handlers")
    append("META-INF/spring.schemas")
    append("META-INF/spring.tooling")

    manifest {
        attributes(
            "Main-Class" to "org.springframework.cloud.function.adapter.aws.FunctionInvoker",
            "Start-Class" to "customer.data.CustomerApplication"
        )
    }

    // Exclude unnecessary files
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
}

// Make shadowJar the default artifact
tasks.build {
    dependsOn(tasks.shadowJar)
}
