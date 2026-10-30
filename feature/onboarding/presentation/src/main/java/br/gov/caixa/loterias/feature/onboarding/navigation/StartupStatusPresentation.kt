package br.gov.caixa.loterias.feature.onboarding.navigation

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind

/** Permite ao hospedeiro fornecer recursos Android e rotas da loja para a interface de estado da introdução. */
interface StartupStatusPresentation {
    @Composable
    fun Content(
        kind: StartupStatusKind,
        onRetry: () -> Unit,
        networkAvailability: NetworkAvailability,
        loading: Boolean,
    )
}
