@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartActions
import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartSummary

internal val cartPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartActions") { state ->
        with(state) {
            CaixaDSCartActions(
                onContinueBetting = action,
                onPayment = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartSummary") { state ->
        with(state) {
            CaixaDSCartSummary(
                total = java.math.BigDecimal("25.50"),
                betCount = 3,
                onClick = action
            )
        }
    }
)
