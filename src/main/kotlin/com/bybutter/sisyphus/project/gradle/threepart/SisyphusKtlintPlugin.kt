package com.bybutter.sisyphus.project.gradle.threepart

import com.bybutter.sisyphus.project.gradle.ensurePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.io.File

class SisyphusKtlintPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.ensurePlugin("org.jlleitschuh.gradle.ktlint") {
            apply(it)
        }.also {
            if (!it) return
        }

        val extension = target.extensions.getByType(KtlintExtension::class.java)
        extension.filter {
            val pattern1 = "${File.separatorChar}generated${File.separatorChar}"
            val pattern2 = "${File.separatorChar}generated-src${File.separatorChar}"
            it.exclude {
                it.file.path.contains(pattern1)
            }
            it.exclude {
                it.file.path.contains(pattern2)
            }
        }
        extension.reporters {
            it.reporter(ReporterType.CHECKSTYLE)
        }
    }
}
