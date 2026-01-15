plugins {
    id("com.vanniktech.maven.publish")
}


infix fun <T> Property<T>.by(value: T) {
    set(value)
}

mavenPublishing {
    coordinates(project.group.toString(), project.name, project.version.toString())


    pom {
        name by project.name
        description by "Some common extensions for Kotlin and popular frameworks."
        inceptionYear by "2026"
        url by "https://github.com/vlfx/common/"
        licenses {
            license {
                name by "The Apache License, Version 2.0"
                url by "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution by "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id by "vlfx"
                name by "vlfx"
                url by "https://github.com/vlfx/"
                email by "luofuxing@me.com"
            }
        }
        scm {
            url by "https://github.com/vlfx/common/"
            connection by "scm:git:git://github.com/vlfx/common.git"
            developerConnection by "scm:git:ssh://git@github.com/vlfx/common.git"
        }
    }
    publishToMavenCentral()
    signAllPublications()
}
