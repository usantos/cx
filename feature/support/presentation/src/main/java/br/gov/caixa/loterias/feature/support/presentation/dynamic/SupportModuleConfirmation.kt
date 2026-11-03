package br.gov.caixa.loterias.feature.support.presentation.dynamic

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleConfirmation

/** Mantém o contrato do piloto com o único adaptador de confirmação da plataforma. */
@Composable
internal fun SupportModuleConfirmation(sessionId: Int, onResult: (Boolean) -> Unit) {
    FeatureModuleConfirmation(sessionId, onResult)
}
