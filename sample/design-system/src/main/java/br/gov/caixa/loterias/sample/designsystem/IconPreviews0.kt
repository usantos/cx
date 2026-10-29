@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSPrizeChevron
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSPurchaseHelpIcon

internal val iconPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.icon.CaixaDSPrizeChevron") { state ->
        with(state) {
            CaixaDSPrizeChevron()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.icon.CaixaDSPurchaseHelpIcon") { state ->
        with(state) {
            CaixaDSPurchaseHelpIcon()
        }
    }
)
