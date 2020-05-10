package ms.ralph.gradle.dependency.plantuml.exporter.model

data class Package(
    val id: PackageId,
    val displayName: String,
    val subPackages: Set<Package>
)
