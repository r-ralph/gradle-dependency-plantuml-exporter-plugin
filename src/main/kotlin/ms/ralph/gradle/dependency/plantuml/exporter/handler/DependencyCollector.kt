package ms.ralph.gradle.dependency.plantuml.exporter.handler

import ms.ralph.gradle.dependency.plantuml.exporter.DependencyPlantUmlExporterExtension
import ms.ralph.gradle.dependency.plantuml.exporter.model.Dependency
import ms.ralph.gradle.dependency.plantuml.exporter.model.Module
import ms.ralph.gradle.dependency.plantuml.exporter.model.ModuleId
import ms.ralph.gradle.dependency.plantuml.exporter.model.PackageId
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency

class DependencyCollector(
    private val extension: DependencyPlantUmlExporterExtension
) {
    fun collect(project: Project): Pair<Set<Module>, Set<Dependency>> {
        val projects = collectPedigreeProjects(project)
        val modules = projects.map { Module(it.getModuleId(), it.getPackageId(), it.getCustomDisplayName()) }.toSet()
        val dependencies = collectAllProjectDependencies(projects)
        return modules to dependencies
    }

    private fun collectAllProjectDependencies(projects: List<Project>): Set<Dependency> = projects
        .flatMap { project -> project.configurations.map { configuration -> project to configuration } }
        .flatMap { (project, configuration) ->
            configuration.dependencies.withType(ProjectDependency::class.java).map { project to it.dependencyProject }
        }
        .map { (dependentProject, targetProject) ->
            Dependency(dependentProject.getModuleId(), targetProject.getModuleId())
        }
        .toSet()

    private fun collectPedigreeProjects(baseProject: Project): List<Project> =
        listOf(baseProject) + baseProject.childProjects.values.flatMap { collectPedigreeProjects(it) }

    private fun Project.getModuleId(): ModuleId = ModuleId(extension.moduleIdProvider.invoke(this))
    private fun Project.getPackageId(): PackageId = PackageId(extension.packageIdProvider.invoke(this))
    private fun Project.getCustomDisplayName(): String = extension.moduleDisplayNameProvider.invoke(this)
}
