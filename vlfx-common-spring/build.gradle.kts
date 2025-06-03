plugins {
    id("kotlin-springboot-common-conventions")
}

dependencies {
    implementation(project(":vlfx-common-utils"))

    implementation("org.springframework.boot:spring-boot-starter-web")
}