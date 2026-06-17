plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
}

group = "me.andreasmelone"
version = "1.0.0"

val lwjglVersion = "3.3.6"
val jomlVersion = "1.10.9"
val slf4jVersion = "2.0.18"

java {
    targetCompatibility = JavaVersion.VERSION_25
    sourceCompatibility = JavaVersion.VERSION_25
}

repositories {
    mavenCentral()
}

val lwjglNatives = providers.gradleProperty("lwjglNatives")
    .orElse("natives-windows")


dependencies {
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-reload4j:$slf4jVersion")

    implementation("it.unimi.dsi:fastutil:8.5.18")

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-openal")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")
    implementation ("org.lwjgl", "lwjgl", classifier = lwjglNatives.get())
    implementation ("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives.get())
    implementation ("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives.get())
    implementation ("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives.get())
    implementation ("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives.get())
    implementation("org.joml:joml:$jomlVersion")
}

tasks {
    jar {
        manifest {
            attributes(
                "Main-Class" to "me.andreasmelone.digisynth.Bootstrap"
            )
        }
    }
}