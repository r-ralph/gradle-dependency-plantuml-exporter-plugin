package ms.ralph.gradle.dependency.plantuml.exporter.handler

import ms.ralph.gradle.dependency.plantuml.exporter.model.Dependency
import ms.ralph.gradle.dependency.plantuml.exporter.model.Module
import ms.ralph.gradle.dependency.plantuml.exporter.model.ModuleId
import ms.ralph.gradle.dependency.plantuml.exporter.model.Package
import ms.ralph.gradle.dependency.plantuml.exporter.model.PackageId
import ms.ralph.gradle.dependency.plantuml.exporter.model.RootPackage

class PlantUmlStyleProcessor {

    fun process(
        rootPackage: RootPackage,
        modules: Set<Module>,
        dependencies: Set<Dependency>
    ): String {
        val stringBuilder = StringBuilder()
        stringBuilder.appendln("@startuml")
        rootPackage.subPackages.forEach { subPackage ->
            getUmlStyleStringRecursive(subPackage, modules, 0, stringBuilder)
        }

        stringBuilder.appendln()
        dependencies.forEach {
            stringBuilder.appendln("[${it.dependentModuleId.umlAware()}] --down-> [${it.targetModuleId.umlAware()}]")
        }
        stringBuilder.appendln("@enduml")
        return stringBuilder.toString()
    }

    private fun getUmlStyleStringRecursive(
        currentPackage: Package,
        allModules: Set<Module>,
        indent: Int,
        stringBuilder: StringBuilder
    ) {
        stringBuilder.appendln(
            """package "${currentPackage.displayName}" as "${currentPackage.id.umlAware()}" {"""
                .indented(indent)
        )
        currentPackage.subPackages.forEach { subPackage ->
            getUmlStyleStringRecursive(subPackage, allModules, indent + 1, stringBuilder)
        }
        allModules.filter { it.packageId == currentPackage.id }.forEach { module ->
            stringBuilder.appendln("""[${module.id.umlAware()}] as "${module.displayName}"""".indented(indent + 1))
        }
        stringBuilder.appendln("}".indented(indent))
    }

    private fun String.indented(size: Int): String = "${"    ".repeat(size)}$this"
    private fun ModuleId.umlAware(): String = "m${value}"
    private fun PackageId.umlAware(): String = "p${value}"
}
