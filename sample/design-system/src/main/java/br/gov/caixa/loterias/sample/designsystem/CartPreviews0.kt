@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.components.cart.LoteriasCaixaDSCartActions
import br.gov.caixa.loterias.core.designsystem.components.cart.LoteriasCaixaDSCartSummary

internal val cartPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.cart.LoteriasCaixaDSCartActions") { state ->
        with(state) {
            LoteriasCaixaDSCartActions(
                onContinueBetting = action,
                onPayment = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.cart.LoteriasCaixaDSCartSummary") { state ->
        with(state) {
            LoteriasCaixaDSCartSummary(
                total = java.math.BigDecimal("25.50"),
                betCount = 3,
                onClick = action
            )
        }
    }
)
