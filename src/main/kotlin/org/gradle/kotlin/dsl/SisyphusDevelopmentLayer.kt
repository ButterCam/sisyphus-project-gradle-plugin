package org.gradle.kotlin.dsl

import com.bybutter.sisyphus.project.gradle.SisyphusExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.attributes.Attribute
import org.gradle.api.provider.Provider

enum class SisyphusDevelopmentLayer {
    /**
     * Just for implementation development, it will disable all internal module version replace.
     */
    IMPLEMENTATION,

    /**
     * Layer 1, Api schema and proto development
     */
    API,

    /**
     * Layer 2, Middleware and internal component development, include [API]
     */
    PLATFORM,

    /**
     * Layer 3, Framework development, include [API], [PLATFORM]
     */
    FRAMEWORK,

    ;

    companion object {
        val attribute = Attribute.of(SisyphusDevelopmentLayer::class.java)
    }
}

fun <T : Dependency> Project.apiLayer(dependency: T): Dependency {
    return layer(dependency, SisyphusDevelopmentLayer.API)
}

fun <T : Dependency> Project.platformLayer(dependency: T): Dependency {
    return layer(dependency, SisyphusDevelopmentLayer.PLATFORM)
}

fun <T : Dependency> Project.frameworkLayer(dependency: T): Dependency {
    return layer(dependency, SisyphusDevelopmentLayer.FRAMEWORK)
}

fun <T : Dependency> Project.layer(
    dependency: T,
    layer: SisyphusDevelopmentLayer,
): Dependency {
    val sisyphus = extensions.findByType(SisyphusExtension::class.java) ?: return dependency
    if (!sisyphus.developer.isPresent) return dependency
    if (sisyphus.layer.get().ordinal < layer.ordinal) return dependency

    return when (dependency) {
        is MinimalExternalModuleDependency -> {
            dependencies.module("${dependency.module.group}:${dependency.module.name}:${this@layer.version}")
        }

        is ExternalModuleDependency -> {
            dependency.version {
                it.require(this@layer.version.toString())
            }
            dependency
        }

        else -> dependency
    }
}

fun <T : Dependency> Project.apiLayer(dependency: Provider<T>): Provider<Dependency> {
    return layer(dependency, SisyphusDevelopmentLayer.API)
}

fun <T : Dependency> Project.platformLayer(dependency: Provider<T>): Provider<Dependency> {
    return layer(dependency, SisyphusDevelopmentLayer.PLATFORM)
}

fun <T : Dependency> Project.frameworkLayer(dependency: Provider<T>): Provider<Dependency> {
    return layer(dependency, SisyphusDevelopmentLayer.FRAMEWORK)
}

fun <T : Dependency> Project.layer(
    dependency: Provider<T>,
    layer: SisyphusDevelopmentLayer,
): Provider<Dependency> {
    return dependency.map {
        layer(it, layer)
    }
}
