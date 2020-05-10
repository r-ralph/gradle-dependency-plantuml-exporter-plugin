package ms.ralph.gradle.dependency.plantuml.exporter

import ms.ralph.gradle.dependency.plantuml.exporter.handler.DependencyCollector
import ms.ralph.gradle.dependency.plantuml.exporter.handler.HierarchicalPackageCollector
import ms.ralph.gradle.dependency.plantuml.exporter.handler.PlantUmlStyleProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class DependencyPlantUmlExporterTask : DefaultTask() {

    @Internal
    lateinit var extension: DependencyPlantUmlExporterExtension

    @OutputDirectory
    lateinit var outputDirectory: File

    @TaskAction
    fun run() {
        val (modules, dependencies) = DependencyCollector(
            extension
        ).collect(project)
        val rootPackage = HierarchicalPackageCollector(
            extension
        ).process(modules)
        val plantUmlText = PlantUmlStyleProcessor()
            .process(rootPackage, modules, dependencies)
        File(outputDirectory, "dependency.plantuml").writeText(plantUmlText)
    }
}
