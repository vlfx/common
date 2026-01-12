import java.io.FileFilter

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "vlfx-common"

//include("common-utils")
//include("common-spring")

//include("vlfx-common-utils")
//include("vlfx-common-spring")

// 自动添加子模块,1层
rootDir.listFiles(FileFilter {
    it.isDirectory && !it.isHidden && it.name != "buildSrc"
            && File(it, "build.gradle.kts").exists()
})?.forEach {
//    println("!>>>>>> " + it.name)
    include(it.name)
}