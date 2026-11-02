@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaignTheme
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSHomeV2Theme
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme

internal val themePreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaignTheme") { state ->
        with(state) {
            LoteriasCaixaDSCampaignTheme(
                campaign = LoteriasCaixaDSCampaign.DEFAULT,
                content = { LoteriasCaixaDSText(sample) }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSHomeV2Theme") { state ->
        with(state) {
            LoteriasCaixaDSHomeV2Theme(content = { LoteriasCaixaDSText(sample) })
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme") { state ->
        with(state) {
            LoteriasCaixaDSTheme(
                darkTheme = false,
                content = { LoteriasCaixaDSText(sample) }
            )
        }
    }
)
