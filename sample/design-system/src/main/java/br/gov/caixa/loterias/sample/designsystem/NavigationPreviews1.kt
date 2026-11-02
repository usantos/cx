@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSSupportTopBar

internal val navigationPreviews1 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSSupportTopBar") { state ->
        with(state) {
            LoteriasCaixaDSSupportTopBar(
                title = sample,
                onBack = action
            )
        }
    }
)
