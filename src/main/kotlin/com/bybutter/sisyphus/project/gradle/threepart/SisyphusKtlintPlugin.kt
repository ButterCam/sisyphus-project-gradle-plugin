package com.bybutter.sisyphus.project.gradle.threepart

import com.bybutter.sisyphus.project.gradle.ensurePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask

class SisyphusKtlintPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.ensurePlugin("org.jmailen.kotlinter") {
            apply(it)
        }.also {
            if (!it) return
        }

        target.tasks.withType(LintTask::class.java).whenTaskAdded {
            it.exclude("**/generated/**")
        }

        target.tasks.withType(FormatTask::class.java).whenTaskAdded {
            it.exclude("**/generated/**")
        }
    }
}
