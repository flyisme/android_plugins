package com.demo.android.plugin

import com.android.build.gradle.AppExtension
import com.didiglobal.booster.gradle.getAndroid
import com.demo.android.plugin.transform.MyTransform
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @className com.demo.android.plugin.HycanPlugin
 * @description HycanPLugin
 * @author heyufei
 * @since 2021/12/16 2:47 下午
 * @version 1.0
 */
class MyPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw GradleException("HycanPlugin, Android Application plugin required.")
        }

        project.getAndroid<AppExtension>().registerTransform(MyTransform(project))

    }

}