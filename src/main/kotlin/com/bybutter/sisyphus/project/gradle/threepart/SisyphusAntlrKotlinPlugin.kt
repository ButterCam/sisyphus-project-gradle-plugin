package com.bybutter.sisyphus.project.gradle.threepart

import com.bybutter.sisyphus.project.gradle.ensurePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.antlr.AntlrTask
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.util.internal.GUtil

class SisyphusAntlrKotlinPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.ensurePlugin("org.gradle.antlr") {
            applyBasic(it)
        }.let {
            if (it) applyBasic(target)
        }
    }

    private fun applyBasic(target: Project) {
        val sourceSets = target.extensions.getByType(SourceSetContainer::class.java)

        sourceSets.forEach {
            val generateTaskName =
                when (it.name) {
                    "main" -> "generateGrammarSource"
                    else -> "generate${GUtil.toCamelCase(it.name)}GrammarSource"
                }
            target.tasks.named(generateTaskName, AntlrTask::class.java).configure { task ->
                it.java.setSrcDirs(it.java.srcDirs.filter { task.outputDirectory.absolutePath != it.absolutePath })
                it.java.srcDir(task)
            }
        }
    }
}
