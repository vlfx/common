plugins {
    `kotlin-dsl`
//    `maven-publish`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    gradleApi()
    implementation(libs.kotlin.jvm.gradlePlugin)
    implementation(libs.kotlin.spring.gradlePlugin)
    implementation(libs.springboot.gradlePlugin)
    implementation(libs.spring.dependency.management.gradlePlugin)

    implementation(libs.dokka.gradlePlugin)
    implementation(libs.maven.publish.gradlePlugin)

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

//publishing {
//    repositories {
//        mavenLocal()
//    }
//}