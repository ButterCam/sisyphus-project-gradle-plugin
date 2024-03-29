package com.bybutter.sisyphus.project.gradle.publishing

import com.bybutter.sisyphus.project.gradle.ensurePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin

class ProjectSigningPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.ensurePlugin("com.netflix.nebula.maven-base-publish") {
            apply(it)
        }.also {
            if (!it) return
        }

        val publishing = target.extensions.getByType(PublishingExtension::class.java)

        if (target.hasProperty("signing.gnupg.keyName")) {
            target.pluginManager.apply(SigningPlugin::class.java)
            val signing = target.extensions.getByType(SigningExtension::class.java)
            signing.useGpgCmd()
            target.afterEvaluate {
                publishing.publications.all {
                    signing.sign(it)
                }
            }
        }
    }
}
