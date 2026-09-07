// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.navigation.safeargs) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
}

val detektPluginId = libs.plugins.detekt.get().pluginId
val ktlintPluginId = libs.plugins.ktlint.get().pluginId
val detektFormatting = libs.detekt.formatting

subprojects {
    apply(plugin = detektPluginId)
    apply(plugin = ktlintPluginId)

    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        buildUponDefaultConfig = true
        autoCorrect = false
        // TODO(milestone 5): flip to false once legacy `app` sources are migrated/cleaned up.
        ignoreFailures = true
    }

    extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        // TODO(milestone 5): flip to false once legacy `app` sources are migrated/cleaned up.
        ignoreFailures.set(true)
    }

    dependencies {
        add("detektPlugins", detektFormatting.get())
    }
}
