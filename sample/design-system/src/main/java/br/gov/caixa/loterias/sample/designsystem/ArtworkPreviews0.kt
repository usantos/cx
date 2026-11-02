@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSAttentionIcon
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryMark
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentBrandMark
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentProvider
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentProviderMark
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPixBrand
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPixCopyIcon
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPixMark
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPixTimerIcon
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPrizeLotteryMark
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSSelfAssessmentIcon

internal val artworkPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSAttentionIcon") { state ->
        with(state) {
            LoteriasCaixaDSAttentionIcon()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSSelfAssessmentIcon") { state ->
        with(state) {
            LoteriasCaixaDSSelfAssessmentIcon()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryMark") { state ->
        with(state) {
            LoteriasCaixaDSLotteryMark()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPixMark") { state ->
        with(state) {
            LoteriasCaixaDSPixMark()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentProviderMark") { state ->
        with(state) {
            LoteriasCaixaDSPaymentProviderMark(provider = LoteriasCaixaDSPaymentProvider.MERCADO_PAGO)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentBrandMark") { state ->
        with(state) {
            LoteriasCaixaDSPaymentBrandMark(brand = "VISA")
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPixBrand") { state ->
        with(state) {
            LoteriasCaixaDSPixBrand()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPixTimerIcon") { state ->
        with(state) {
            LoteriasCaixaDSPixTimerIcon()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPixCopyIcon") { state ->
        with(state) {
            LoteriasCaixaDSPixCopyIcon(copied = checked)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPrizeLotteryMark") { state ->
        with(state) {
            LoteriasCaixaDSPrizeLotteryMark()
        }
    }
)
