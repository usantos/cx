@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSMercadoPagoBrand
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPrizeMercadoPagoMark

internal val artworkPreviews1 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPrizeMercadoPagoMark") { state ->
        with(state) {
            LoteriasCaixaDSPrizeMercadoPagoMark()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSMercadoPagoBrand") { state ->
        with(state) {
            LoteriasCaixaDSMercadoPagoBrand()
        }
    }
)
