plugins {
    java
    application
    groovy
    jacoco
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.10.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass.set("org.example.App")
}

javafx {
    version = "25"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.apache.groovy:groovy:4.0.21")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    testImplementation("org.testfx:testfx-core:4.0.18")
    testImplementation("org.testfx:testfx-junit5:4.0.18")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // 테스트 후 자동으로 리포트 생성
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // 테스트가 먼저 실행되어야 함
    
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                // 제외할 클래스 패턴 (필요시)
                exclude(
                    "**/deprecated/**",  // deprecated 패키지 제외
                    "**/App.class"       // 메인 클래스 제외
                )
            }
        })
    )
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()  // 최소 80% 커버리지 요구
            }
        }
        
        rule {
            element = "CLASS"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.60".toBigDecimal()  // 각 클래스는 최소 60%
            }
        }
    }
}

// 테스트 검증을 빌드 프로세스에 포함 (선택사항)
tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

// jlink block is left as-is, but note: jlink is designed for modular projects. If you do not use modules, jlink may not work as expected. You may remove this block if you do not need custom runtime images.
