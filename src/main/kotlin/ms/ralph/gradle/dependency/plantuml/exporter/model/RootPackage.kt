package ms.ralph.gradle.dependency.plantuml.exporter.model

data class RootPackage(
    val subPackages: Set<Package>
)
