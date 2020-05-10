package ms.ralph.gradle.dependency.plantuml.exporter.handler

import ms.ralph.gradle.dependency.plantuml.exporter.DependencyPlantUmlExporterExtension
import ms.ralph.gradle.dependency.plantuml.exporter.model.Module
import ms.ralph.gradle.dependency.plantuml.exporter.model.Package
import ms.ralph.gradle.dependency.plantuml.exporter.model.PackageId
import ms.ralph.gradle.dependency.plantuml.exporter.model.RootPackage

class HierarchicalPackageCollector(
    private val extension: DependencyPlantUmlExporterExtension
) {

    fun process(modules: Set<Module>): RootPackage {
        val packageIdSet = modules.map(Module::packageId).toSet()
        val mutableRootPackage =
            MutableRootPackage(
                mutableSetOf()
            )
        packageIdSet.forEach { packageId ->
            addPackage(mutableRootPackage, packageId)
        }
        return mutableRootPackage.toImmutable()
    }

    private fun addPackage(rootPackage: MutableRootPackage, packageId: PackageId) {
        val packageIdPaths = packageId.value.split(':').filter(String::isNotBlank)

        if (packageIdPaths.isEmpty()) {
            // for project root module(":").
            rootPackage.subPackages.firstOrCreate(
                { it.id.value == ":" },
                {
                    MutablePackage(
                        PackageId(":"),
                        extension.packageDisplayNameProvider.invoke(":"),
                        mutableSetOf()
                    )
                }
            )
            return
        }

        val nextPackageId = PackageId(":${packageIdPaths.first()}")
        val nextPackage = rootPackage.subPackages.firstOrCreate(
            { it.id == nextPackageId },
            {
                MutablePackage(
                    nextPackageId,
                    extension.packageDisplayNameProvider.invoke(nextPackageId.value),
                    mutableSetOf()
                )
            }
        )
        addPackage(nextPackage, packageIdPaths.drop(1))
    }

    private fun addPackage(basePackage: MutablePackage, packageIdPaths: List<String>) {
        if (packageIdPaths.isEmpty()) {
            return
        }
        val nextPackageId = PackageId("${basePackage.id.value}:${packageIdPaths.first()}")

        val existingNextPackage = basePackage.subPackages.firstOrNull { it.id == nextPackageId }
        val nextPackage = if (existingNextPackage == null) {
            val newPackage =
                MutablePackage(
                    nextPackageId,
                    extension.packageDisplayNameProvider.invoke(nextPackageId.value),
                    mutableSetOf()
                )
            basePackage.subPackages.add(newPackage)
            newPackage
        } else {
            existingNextPackage
        }
        addPackage(nextPackage, packageIdPaths.drop(1))
    }

    private fun <T> MutableSet<T>.firstOrCreate(predicate: (T) -> Boolean, provider: () -> T): T {
        val existingValue = firstOrNull(predicate)
        return if (existingValue == null) {
            val newValue = provider.invoke()
            add(newValue)
            newValue
        } else {
            existingValue
        }
    }

    private data class MutablePackage(
        val id: PackageId,
        val displayName: String,
        val subPackages: MutableSet<MutablePackage>
    ) {
        fun toImmutable(): Package = Package(
            id,
            displayName,
            subPackages.map { it.toImmutable() }.toSet()
        )
    }

    private data class MutableRootPackage(
        val subPackages: MutableSet<MutablePackage>
    ) {
        fun toImmutable(): RootPackage = RootPackage(
            subPackages.map { it.toImmutable() }.toSet()
        )
    }
}
