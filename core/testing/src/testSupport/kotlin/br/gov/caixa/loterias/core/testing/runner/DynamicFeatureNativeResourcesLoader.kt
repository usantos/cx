package br.gov.caixa.loterias.core.testing.runner

import android.os.Build
import javax.annotation.Priority
import org.robolectric.nativeruntime.DefaultNativeRuntimeLoader

/**
 * Compatibilidade de testes com recursos reais de splits no SDK 36.
 * Registra os mesmos quatro tipos JNI da correção upstream f027b0b, ainda ausente no Robolectric 4.17.
 */
@Priority(0)
class DynamicFeatureNativeResourcesLoader : DefaultNativeRuntimeLoader() {
    override fun getCoreClassNatives(): List<String> = super.getCoreClassNatives() + if (Build.VERSION.SDK_INT >= SDK_BAKLAVA) {
        listOf(
            "android.content.res.ApkAssets",
            "android.content.res.AssetManager",
            "android.content.res.StringBlock",
            "android.content.res.XmlBlock"
        )
    } else {
        emptyList()
    }
}

private const val SDK_BAKLAVA = 36
