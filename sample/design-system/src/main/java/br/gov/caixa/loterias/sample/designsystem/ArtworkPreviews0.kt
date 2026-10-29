@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSAttentionIcon
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryMark
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPaymentBrandMark
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPaymentProvider
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPaymentProviderMark
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixBrand
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixCopyIcon
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixMark
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixTimerIcon
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPrizeLotteryMark
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSSelfAssessmentIcon

internal val artworkPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSAttentionIcon") { state ->
        with(state) {
            CaixaDSAttentionIcon()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSSelfAssessmentIcon") { state ->
        with(state) {
            CaixaDSSelfAssessmentIcon()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryMark") { state ->
        with(state) {
            CaixaDSLotteryMark()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixMark") { state ->
        with(state) {
            CaixaDSPixMark()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPaymentProviderMark") { state ->
        with(state) {
            CaixaDSPaymentProviderMark(provider = CaixaDSPaymentProvider.MERCADO_PAGO)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPaymentBrandMark") { state ->
        with(state) {
            CaixaDSPaymentBrandMark(brand = "VISA")
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixBrand") { state ->
        with(state) {
            CaixaDSPixBrand()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixTimerIcon") { state ->
        with(state) {
            CaixaDSPixTimerIcon()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixCopyIcon") { state ->
        with(state) {
            CaixaDSPixCopyIcon(copied = checked)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPrizeLotteryMark") { state ->
        with(state) {
            CaixaDSPrizeLotteryMark()
        }
    }
)
