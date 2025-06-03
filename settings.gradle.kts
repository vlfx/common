import java.io.FileFilter

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "common"

//include("common-utils")
//include("common-spring")

include("vlfx-common-utils")
include("vlfx-common-spring")


//file(rootDir).listFiles(FileFilter { it.isDirectory && it.name.startsWith("common")}).forEach {
////    include(it.name)
//    println(">>>>>>>> ${it.name}")
//}

