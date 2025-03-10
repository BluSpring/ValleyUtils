import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.0"
    id("fabric-loom") version "1.9-SNAPSHOT"
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.5"
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 17
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven") {
                name = "Modrinth"
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }

    maven("https://maven.parchmentmc.org")
    maven("https://maven.bawnorton.com/releases")
    maven("https://maven.ladysnake.org/releases")
    maven("https://maven.uuid.gg/releases")
    maven("https://maven.jamieswhiteshirt.com/libs-release")

    flatDir {
        dirs("libs")
    }
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.20.1:2023.09.03@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    modImplementation("maven.modrinth:sodium:mc1.20.1-0.5.11")
    modImplementation("maven.modrinth:iris:1.7.5+1.20.1")
    modCompileOnly("maven.modrinth:enchancement:1.20-26")
    modCompileOnly("maven.modrinth:tooltipfix:1.1.1-1.20")

    shadow(implementation("com.squareup.okhttp3:okhttp:4.12.0")!!)

    include(implementation(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:0.2.0")!!)!!)
    modCompileOnly("maven.modrinth:ouNrBQtq:1.20.1-3.20.17.1.60") // sophisticated backpacks
    modImplementation("maven.modrinth:arsenal:0.1.5-1.20.1")
    modImplementation("maven.modrinth:ratatouille:1.0.9-1.20.1")
    modImplementation("dev.upcraft.datasync:datasync-minecraft-1.20.1-fabric:0.9.0")
    modImplementation("com.jamieswhiteshirt:reach-entity-attributes:2.4.0")


    val ccaVersion = "5.2.2"
    modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-base:$ccaVersion")
    modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-item:$ccaVersion")
    modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:$ccaVersion")
    modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-world:$ccaVersion")

    modCompileOnly("maven.modrinth:yungs-cave-biomes:1.20.1-Fabric-2.0.0")

    modImplementation("maven.modrinth:architectury-api:9.2.14+fabric")
    modImplementation("maven.modrinth:lets-do-vinery:ZnUrEL2E")
    modRuntimeOnly("maven.modrinth:cloth-config:11.1.136+fabric")

    include(implementation("com.moulberry:mixinconstraints:1.0.6")!!)

    if (file("libs").isDirectory()) {
        file("libs").listFiles().forEach { file ->
            val splitPos = file.name.lastIndexOf("-")

            if (file.name != "desktop.ini") {
                println(file.name)

                val modartifact = file.name.substring(0, splitPos)
                val modversion = file.name.substring(splitPos + 1, file.name.length - 4)
                val modreference = "lib:$modartifact:$modversion"

                dependencies {
                    modImplementation (project.dependencies.create(modreference) {
                        isTransitive = false
                    })
                }
            }
        }
    } else file("libs").mkdir()
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version"),
            "kotlin_loader_version" to project.property("kotlin_loader_version")
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

tasks.shadowJar {
    configurations = listOf(project.configurations.getByName("shadow"))
    archiveClassifier.set("dev-shadow")

    exclude("kotlin/**/*", "kotlinx/**/*", "org/intellij/**/*", "org/jetbrains/**/*")
    relocate("org.snakeyaml", "xyz.bluspring.valleyutils.reloc.snakeyaml")
    relocate("com.charleskorn.kaml", "xyz.bluspring.valleyutils.reloc.kaml")
    relocate("net.thauvin.erik.urlencoder", "xyz.bluspring.valleyutils.reloc.urlencoder")
    relocate("okio", "xyz.bluspring.valleyutils.reloc.okio")
    relocate("com.squareup", "xyz.bluspring.valleyutils.reloc.squareup")
}

tasks.remapJar {
    dependsOn(tasks.shadowJar)
    inputFile.set(tasks.shadowJar.get().archiveFile)
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}
