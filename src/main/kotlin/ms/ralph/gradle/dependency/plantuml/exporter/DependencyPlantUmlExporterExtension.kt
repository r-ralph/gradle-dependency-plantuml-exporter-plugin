package ms.ralph.gradle.dependency.plantuml.exporter

import org.gradle.api.Project

open class DependencyPlantUmlExporterExtension {
    var moduleIdProvider: (Project) -> String = { it.path }
    var packageIdProvider: (Project) -> String = { it.parent?.path ?: "" }
    var moduleDisplayNameProvider: (Project) -> String = { it.path }
    var packageDisplayNameProvider: (String) -> String = { it }
}
