@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.components.state.CaixaDSGateMessage
import br.gov.caixa.loterias.core.designsystem.components.state.CaixaDSStatusAction

internal val statePreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.state.CaixaDSStatusAction") { state ->
        with(state) {
            CaixaDSStatusAction(
                title = sample,
                message = hint,
                action = actionLabel,
                onAction = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.state.CaixaDSGateMessage") { state ->
        with(state) {
            CaixaDSGateMessage(message = hint)
        }
    }
)
