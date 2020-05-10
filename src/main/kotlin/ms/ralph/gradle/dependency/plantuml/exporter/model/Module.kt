package ms.ralph.gradle.dependency.plantuml.exporter.model

data class Module(
    val id: ModuleId,
    val packageId: PackageId,
    val displayName: String
)
