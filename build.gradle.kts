plugins {
    java
    kotlin("jvm") version Dependencies.Version.kotlin
    id("com.gradle.plugin-publish") version Dependencies.Version.gradlePublishPlugin
    `java-gradle-plugin`
}

version = ModuleConfig.version
group = ModuleConfig.groupId

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xinline-classes")
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xinline-classes")
        }
    }
}

gradlePlugin {
    plugins {
        create("dependencyPlantUmlExporterPlugin") {
            id = ModuleConfig.pluginId
            implementationClass = "ms.ralph.gradle.dependency.plantuml.exporter.DependencyPlantUmlExporterPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/r-ralph/gradle-dependency-plantuml-exporter-plugin"
    vcsUrl = "https://github.com/r-ralph/gradle-dependency-plantuml-exporter-plugin.git"
    description = "A Gradle plugin to export module dependency graph as PlantUML style text."
    (plugins) {
        "dependencyPlantUmlExporterPlugin" {
            displayName = ModuleConfig.displayName
            tags = listOf("multi-module", "dependency", "plantuml")
            version = ModuleConfig.version
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
