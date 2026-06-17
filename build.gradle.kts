plugins {
    id("java")
}

group = "me.andreasmelone"
version = "1.0-SNAPSHOT"

val lwjglVersion = "3.3.6"
val jomlVersion = "1.10.9"
val lwjglNatives = "natives-windows"

val slf4jVersion = "2.0.18"

repositories {
    mavenCentral()
}

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
    implementation ("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    implementation("org.joml:joml:$jomlVersion")
}