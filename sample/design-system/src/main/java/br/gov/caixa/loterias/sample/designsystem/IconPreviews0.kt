@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSPrizeChevron
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSPurchaseHelpIcon

internal val iconPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSPrizeChevron") { state ->
        with(state) {
            LoteriasCaixaDSPrizeChevron()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSPurchaseHelpIcon") { state ->
        with(state) {
            LoteriasCaixaDSPurchaseHelpIcon()
        }
    }
)
