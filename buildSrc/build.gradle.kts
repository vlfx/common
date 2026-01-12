plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "io.github.vlfx"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    gradleApi()
    implementation(libs.jvm)
    implementation(libs.kotlin.spring)
    implementation(libs.springboot)
    implementation(libs.spring.dependency.management)


}

//tasks.test {
//    useJUnitPlatform()
//}
kotlin {
    jvmToolchain(21)
}

//tasks.withType<Test> {
//    useJUnitPlatform()
//}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

publishing {
    repositories {
        mavenLocal()
    }
}