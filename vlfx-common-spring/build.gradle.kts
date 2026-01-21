plugins {
    id("kotlin-springboot-common-conventions")
    id("dokka-convention")
    id("publish-convention")
}

dependencies {
//    compileOnly(project(":vlfx-common-utils"))

//    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-json")

    // Source: https://mvnrepository.com/artifact/org.apache.httpcomponents.client5/httpclient5
    implementation("org.apache.httpcomponents.client5:httpclient5")
}