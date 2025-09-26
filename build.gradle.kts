// build.gradle.kts (Project-level)
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.0" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// Optional: Java/Kotlin compile settings for all subprojects
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions { jvmTarget = "11" }
    }

    tasks.withType<JavaCompile>().configureEach {
        javaCompiler.set(
            project.extensions.getByType<org.gradle.jvm.toolchain.JavaToolchainService>()
                .compilerFor { languageVersion.set(JavaLanguageVersion.of(11)) }
        )
    }
}
