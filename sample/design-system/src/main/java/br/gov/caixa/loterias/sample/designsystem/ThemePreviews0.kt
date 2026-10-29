@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaignTheme
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSHomeV2Theme
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme

internal val themePreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaignTheme") { state ->
        with(state) {
            CaixaDSCampaignTheme(
                campaign = CaixaDSCampaign.DEFAULT,
                content = { CaixaDSText(sample) }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.theme.CaixaDSHomeV2Theme") { state ->
        with(state) {
            CaixaDSHomeV2Theme(content = { CaixaDSText(sample) })
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme") { state ->
        with(state) {
            CaixaDSTheme(
                darkTheme = false,
                content = { CaixaDSText(sample) }
            )
        }
    }
)
