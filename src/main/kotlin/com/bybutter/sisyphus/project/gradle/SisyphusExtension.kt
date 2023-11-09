package com.bybutter.sisyphus.project.gradle

import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dsl.ParsedModuleStringNotation
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.SisyphusDevelopmentLayer

open class SisyphusExtension(val project: Project) {
    val developer: Property<String> = project.objects.property(String::class.java)

    val layer: Property<SisyphusDevelopmentLayer> =
        project.objects.property(SisyphusDevelopmentLayer::class.java).value(SisyphusDevelopmentLayer.IMPLEMENTATION)

    val repositories: MapProperty<String, Repository> =
        project.objects.mapProperty(String::class.java, Repository::class.java).empty()

    val dependencyRepositories: ListProperty<String> =
        project.objects.listProperty(String::class.java).value(listOf("local", "central", "portal", "google"))

    val releaseRepositories: ListProperty<String> =
        project.objects.listProperty(String::class.java).value(listOf("release"))

    val snapshotRepositories: ListProperty<String> =
        project.objects.listProperty(String::class.java).value(listOf("snapshot"))

    val dockerPublishRegistries: ListProperty<String> = project.objects.listProperty(String::class.java).empty()

    val managedDependencies: MapProperty<String, ParsedModuleStringNotation> =
        project.objects.mapProperty(String::class.java, ParsedModuleStringNotation::class.java).empty()

    init {
        developer.set(project.resolveProperty("sisyphus.developer"))
        for (key in project.properties.keys) {
            val result = repositoryUrlRegex.matchEntire(key) ?: continue
            val repositoryName = result.groupValues[1]

            val url = project.resolveProperty("sisyphus.repositories.$repositoryName.url") ?: continue
            val username = project.resolveProperty("sisyphus.repositories.$repositoryName.username")
            val password =project.resolveProperty("sisyphus.repositories.$repositoryName.password") as? String

            repositories.put(repositoryName, Repository(url, username, password))
        }

        project.resolveProperty("sisyphus.dependency.repositories")?.split(',')?.let {
            dependencyRepositories.set(it)
        }
        project.resolveProperty("sisyphus.release.repositories")?.split(',')?.let {
            releaseRepositories.set(it)
        }
        project.resolveProperty("sisyphus.snapshot.repositories")?.split(',')?.let {
            snapshotRepositories.set(it)
        }
        project.resolveProperty("sisyphus.docker.repositories")?.split(',')?.let {
            dockerPublishRegistries.set(it)
        }
        project.resolveProperty("sisyphus.dependency.overriding")?.split(',')?.associate {
            val moduleStringNotation = ParsedModuleStringNotation(it, "")
            "${moduleStringNotation.group}:${moduleStringNotation.name}" to moduleStringNotation
        }?.let {
            managedDependencies.set(it)
        }
        project.resolveProperty("sisyphus.layer")?.let { SisyphusDevelopmentLayer.valueOf(it) }?.let {
            layer.set(it)
        }
    }

    private fun Project.resolveProperty(name: String): String? {
        return resolveVariable(findProperty(name) as? String)
    }

    private fun Project.resolveVariable(value: String?): String? {
        value ?: return null

        val result = variableRegex.matchEntire(value) ?: return value
        val variableName = result.groupValues[1]

        val property = findProperty(variableName) ?: System.getenv(variableName) ?: return null

        return resolveVariable(property.toString())
    }

    fun recommendVersion(): String? {
        val branchName: String? = System.getenv("BRANCH_NAME")
        val githubRef: String? = System.getenv("GITHUB_REF")
        val tagName: String? = System.getenv("TAG_NAME")
        val buildVersion: String? = System.getenv("BUILD_VERSION")

        return when {
            !developer.orNull.isNullOrEmpty() -> "${developer.get()}-SNAPSHOT"
            buildVersion != null -> "$buildVersion"
            tagName != null -> "$tagName"
            branchName != null -> "$branchName-SNAPSHOT"
            githubRef != null && pullRequestRefRegex.matches(githubRef) ->
                "PR-${
                    pullRequestRefRegex.matchEntire(
                        githubRef,
                    )?.groupValues?.get(1)
                }-SNAPSHOT"

            else -> null
        }
    }

    companion object {
        private val repositoryUrlRegex = """sisyphus\.repositories\.([A-Za-z][A-Za-z0-9-_]+)\.url""".toRegex()
        private val pullRequestRefRegex = """refs/pull/([0-9]+)/merge""".toRegex()
        private val variableRegex = """\$\{([A-Za-z][A-Za-z0-9-_.]+)\}""".toRegex()
    }
}
