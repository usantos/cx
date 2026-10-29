package br.gov.caixa.loterias.feature.home.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import br.gov.caixa.loterias.feature.home.BuildConfig

/** O aplicativo fornece informações da compilação sem criar dependência com o módulo do aplicativo. */
data class HomeRuntimeConfiguration(
    val homeV2: Boolean,
    val versionName: String,
    // Prévias e testes isolados usam o relógio local como alternativa; o aplicativo fornece sua fonte sincronizada.
    val serverTime: br.gov.caixa.loterias.core.common.time.ServerTimeSource =
        br.gov.caixa.loterias.core.common.time.ServerTimeSource { java.time.Instant.now() },
)

val LocalHomeRuntimeConfiguration = staticCompositionLocalOf {
    HomeRuntimeConfiguration(BuildConfig.HOME_V2, BuildConfig.DEFAULT_VERSION_NAME)
}
