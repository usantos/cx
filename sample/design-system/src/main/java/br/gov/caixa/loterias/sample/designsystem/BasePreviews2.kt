@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton

internal val basePreviews2 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton") { state ->
        with(state) {
            CaixaDSTextButton(
                onClick = action,
                content = { CaixaDSText(actionLabel) }
            )
        }
    }
)
