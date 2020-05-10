package ms.ralph.gradle.dependency.plantuml.exporter

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class DependencyPlantUmlExporterPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension =
            target.extensions.create("dependencyPlantUmlExporter", DependencyPlantUmlExporterExtension::class.java)

        target.tasks.register("exportDependencyGraph", DependencyPlantUmlExporterTask::class.java) {
            it.outputs.upToDateWhen { false }
            it.group = "reporting"
            it.description = "Generates a dependency graph"
            it.extension = extension
            it.outputDirectory = File(target.buildDir, "reports/dependency-graph/")
        }
    }
}
