package br.gov.caixa.loterias.core.testing.runner

import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties
import org.robolectric.RobolectricTestRunner

/** Usa o APK universal real como contêiner de recursos; o código exercitado continua no módulo proprietário. */
open class DynamicFeatureRobolectricTestRunner(testClass: Class<*>) : RobolectricTestRunner(testClass) {
    override fun getBuildSystemApiProperties(): Properties = Properties().apply {
        putAll(requireNotNull(super.getBuildSystemApiProperties()) { "Configuração AGP ausente para o teste do split" })
        val resourceApk = Path.of(
            requireNotNull(System.getProperty("simlo.dynamicFeature.resourceApk")) {
                "Execute a suíte pelo Gradle para preparar o APK universal de recursos"
            }
        )
        require(Files.isRegularFile(resourceApk)) { "APK universal de recursos ausente: $resourceApk" }
        setProperty("android_resource_apk", resourceApk.toAbsolutePath().toString())
        setProperty("android_custom_package", requireNotNull(System.getProperty("simlo.dynamicFeature.baseApplicationId")))
    }
}
