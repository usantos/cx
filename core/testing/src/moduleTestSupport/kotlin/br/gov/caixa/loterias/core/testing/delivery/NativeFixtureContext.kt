package br.gov.caixa.loterias.core.testing.delivery

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.os.Process
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File

/** Mantém o oráculo compilado de teste separado do APK real usado para renderizar a produção. */
fun nativeFixtureContext(resourceNamespace: String): Context {
    val instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    val fixtureResources = if (System.getProperty("simlo.dynamicFeature.resourceApk") == null) {
        instrumentationContext.resources
    } else {
        val apk = File(requireNotNull(System.getProperty("simlo.dynamicFeature.fixtureResourceApk"))).absoluteFile
        require(apk.isFile) { "APK compilado das fixtures ausente: $apk" }
        instrumentationContext.packageManager.getResourcesForApplication(
            ApplicationInfo().apply {
                // O PackageManager de teste mantém a instrumentação no cache com recursos do alvo.
                // Um identificador isolado obriga carregar o APK de fixtures indicado em sourceDir.
                packageName = "$resourceNamespace.nativefixture"
                sourceDir = apk.absolutePath
                publicSourceDir = sourceDir
                uid = Process.myUid()
            }
        )
    }
    val fixtureTheme = fixtureResources.newTheme().apply { applyStyle(android.R.style.Theme_Material_Light_NoActionBar, true) }
    return object : ContextWrapper(instrumentationContext) {
        override fun getResources() = fixtureResources
        override fun getAssets() = fixtureResources.assets
        override fun getTheme() = fixtureTheme
        override fun getPackageName() = resourceNamespace
    }
}
