plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
}

// With `cache-redirector`, fails with
//   * What went wrong:
//   Execution failed for task ':lib:printSkiko_macos-x64'.
//   > Could not resolve all files for configuration ':lib:skiko_macos-x64'.
//      > Could not find skiko-awt-runtime-macos-x64-0.7.81.jar (org.jetbrains.skiko:skiko-awt-runtime-macos-x64:0.7.81).
//       Searched in the following locations:
//       https://cache-redirector.jetbrains.com/plugins.gradle.org/org/jetbrains/skiko/skiko-awt-runtime-macos-x64/0.7.81/skiko-awt-runtime-macos-x64-0.7.81.jar
//repositories {
//    maven("https://cache-redirector.jetbrains.com/plugins.gradle.org")
//    maven("https://cache-redirector.jetbrains.com/maven.pkg.jetbrains.space/public/p/compose/dev")
//}

// Without `cache-redirector` it works (to try out, comment out the first `repositories` block and uncomment the following one:
repositories {
     maven("https://plugins.gradle.org/m2")
     maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

val skikos = listOf("macos-arm64", "macos-x64").map { p ->
    val confProvider = configurations.register("skiko_$p") {
        isTransitive = false
        withDependencies {
            val skikoDep = project.dependencies.create("org.jetbrains.skiko:skiko-awt-runtime-$p:0.7.81")
            add(skikoDep)
        }
    }
    val skikoJarProvider = layout.file(confProvider.map { it.resolve().single() })

    tasks.register("printSkiko_$p", DefaultTask::class.java) {
        doLast {
            println(skikoJarProvider.get())
        }
    }
}

tasks.register("hello", DefaultTask::class.java) {
    dependsOn(skikos)
}
