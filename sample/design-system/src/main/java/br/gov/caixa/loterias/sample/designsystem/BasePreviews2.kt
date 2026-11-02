@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton

internal val basePreviews2 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton") { state ->
        with(state) {
            LoteriasCaixaDSTextButton(
                onClick = action,
                content = { LoteriasCaixaDSText(actionLabel) }
            )
        }
    }
)
