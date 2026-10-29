@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSMercadoPagoBrand
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPrizeMercadoPagoMark

internal val artworkPreviews1 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPrizeMercadoPagoMark") { state ->
        with(state) {
            CaixaDSPrizeMercadoPagoMark()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSMercadoPagoBrand") { state ->
        with(state) {
            CaixaDSMercadoPagoBrand()
        }
    }
)
