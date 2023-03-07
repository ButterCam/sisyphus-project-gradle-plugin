package com.bybutter.sisyphus.project.gradle.threepart

import com.bybutter.sisyphus.project.gradle.ensurePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.antlr.AntlrTask
import org.gradle.api.tasks.bundling.Jar
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask

class SisyphusAntlrKotlinPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.ensurePlugin("org.gradle.antlr") {
            applyBasic(it)
        }.let {
            if (it) applyBasic(target)
        }
        target.ensurePlugin("org.gradle.antlr", "kotlin") {
            applyWithKotlin(it)
        }.let {
            if (it) applyWithKotlin(target)
        }
        target.ensurePlugin("org.gradle.antlr", "org.jlleitschuh.gradle.ktlint") {
            applyWithKtlint(it)
        }.let {
            if (it) applyWithKtlint(target)
        }
    }

    private fun applyBasic(target: Project) {
        target.tasks.withType(Jar::class.java).configureEach {
            it.mustRunAfter(target.tasks.withType(AntlrTask::class.java))
        }
    }

    private fun applyWithKotlin(target: Project) {
        target.tasks.withType(KotlinCompile::class.java).configureEach {
            it.mustRunAfter(target.tasks.withType(AntlrTask::class.java))
        }
    }

    private fun applyWithKtlint(target: Project) {
        target.tasks.withType(KtLintCheckTask::class.java).configureEach {
            it.mustRunAfter(target.tasks.withType(AntlrTask::class.java))
        }
    }
}
