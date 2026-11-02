@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.components.state.LoteriasCaixaDSGateMessage
import br.gov.caixa.loterias.core.designsystem.components.state.LoteriasCaixaDSStatusAction

internal val statePreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.state.LoteriasCaixaDSStatusAction") { state ->
        with(state) {
            LoteriasCaixaDSStatusAction(
                title = sample,
                message = hint,
                action = actionLabel,
                onAction = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.state.LoteriasCaixaDSGateMessage") { state ->
        with(state) {
            LoteriasCaixaDSGateMessage(message = hint)
        }
    }
)
