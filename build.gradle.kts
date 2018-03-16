import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.1.51"
}

application {
    mainClassName = "io.ktor.server.netty.DevelopmentEngine"
}

buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.2.30"

    var ktor_version: String by extra
    ktor_version = "0.9.1"

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlin_version))
    }
}

group = "com.codebutler.tabulaserver"
version = "1.0-SNAPSHOT"

apply {
    plugin("java")
    plugin("kotlin")
}

val kotlin_version: String by extra
val ktor_version: String by extra

repositories {
    jcenter()
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
}

dependencies {
    compile(kotlin("stdlib-jdk8", kotlin_version))

    compile("technology.tabula:tabula:1.0.1") {
        exclude(group = "org.slf4j", module = "slf4j-simple")
    }

    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:0.21")

    compile("com.squareup.okhttp3:okhttp:3.10.0")

    compile("com.squareup.moshi:moshi:1.5.0")
    compile("com.squareup.moshi:moshi-kotlin:1.5.0")

    compile("io.ktor:ktor-server-core:$ktor_version")
    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("io.ktor:ktor-server-jetty:$ktor_version")
    compile("io.ktor:ktor-auth:$ktor_version")
    compile("io.ktor:ktor-auth-jwt:$ktor_version")

    compile("ch.qos.logback:logback-classic:1.2.1")

    compile("io.sentry:sentry:1.7.2")

    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

configure<KotlinProjectExtension> {
    experimental.coroutines = Coroutines.ENABLE
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

task("stage").dependsOn("build", "clean")
tasks["build"].mustRunAfter("clean")