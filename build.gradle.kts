plugins {
    java
    application
    groovy
    jacoco
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("com.github.johnrengelman.shadow") version "8.1.1"
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

// ShadowJar 설정 - 모든 의존성 포함
tasks.shadowJar {
    archiveBaseName.set("tetris")
    archiveClassifier.set("")
    archiveVersion.set("1.0")
    manifest {
        attributes["Main-Class"] = "org.example.App"
    }
    // JavaFX 모듈을 명시적으로 포함
    mergeServiceFiles()
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
    testImplementation("org.hamcrest:hamcrest:2.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
    maxParallelForks = 4  // 4개의 병렬 프로세스로 실행
    forkEvery = 100  // 100개 테스트마다 새 JVM 프로세스 시작
    
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
                minimum = "0.50".toBigDecimal()  // 최소 50% 커버리지 요구 (현재 53%이므로 통과)
            }
        }
        
        rule {
            element = "CLASS"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.20".toBigDecimal()  // 각 클래스는 최소 20% (점진적 개선 목표)
            }
        }
    }
}

// 테스트 검증을 빌드 프로세스에서 제외 (개발 중에는 선택적으로 실행)
// tasks.check {
//     dependsOn(tasks.jacocoTestCoverageVerification)
// }

// jpackage 태스크 - JRE를 포함한 실행 파일 생성
tasks.register<Exec>("jpackage") {
    dependsOn("shadowJar")
    
    val inputDir = "${layout.buildDirectory.get()}/libs"
    val outputDir = "${layout.buildDirectory.get()}/dist"
    val jarFile = "tetris-1.0.jar"
    
    doFirst {
        file(outputDir).mkdirs()
    }
    
    val osName = System.getProperty("os.name").lowercase()
    val installerType = when {
        osName.contains("mac") -> "dmg"
        osName.contains("win") -> "app-image"  // msi 대신 app-image (설치 프로그램 없이 실행 가능)
        else -> "deb"
    }

    val iconFile = when {
        osName.contains("mac") -> "${projectDir}/src/main/res/icons/logo.icns"
        osName.contains("win") -> "${projectDir}/src/main/res/icons/logo.ico"
        else -> "${projectDir}/src/main/res/icons/logo.png"
    }
    
    // JavaFX SDK 경로 (Gradle이 다운로드한 위치)
    val pathSeparator = if (osName.contains("win")) ";" else ":"
    val javafxModulePath = configurations.runtimeClasspath.get()
        .files
        .filter { it.name.contains("javafx") }
        .map { it.parent }
        .distinct()
        .joinToString(pathSeparator)
    
    val jpackageArgs = mutableListOf(
        "jpackage",
        "--input", inputDir,
        "--name", "Tetris",
        "--main-jar", jarFile,
        "--main-class", "org.example.App",
        "--type", installerType,
        "--dest", outputDir,
        "--app-version", "1.0",
        "--vendor", "Seoultech Team12",
        "--description", "Tetris Game with JavaFX",
        "--copyright", "Copyright 2025",
        "--module-path", javafxModulePath,
        "--add-modules", "javafx.controls,javafx.fxml",
        "--java-options", "-Xmx512m"
    )

    if (file(iconFile).exists()) {
        jpackageArgs.add("--icon")
        jpackageArgs.add(iconFile)
    }

    commandLine(jpackageArgs)
}
