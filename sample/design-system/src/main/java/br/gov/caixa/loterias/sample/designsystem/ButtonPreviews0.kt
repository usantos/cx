@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSFooterButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSOutlinedButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSPrimaryButton
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

internal val buttonPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSPrimaryButton") { state ->
        with(state) {
            CaixaDSPrimaryButton(
                text = actionLabel,
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSFooterButton") { state ->
        with(state) {
            CaixaDSFooterButton(
                text = actionLabel,
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSOutlinedButton") { state ->
        with(state) {
            CaixaDSOutlinedButton(
                text = actionLabel,
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButton") { state ->
        with(state) {
            CaixaDSBettingButton(
                text = actionLabel,
                onClick = action,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                color = CaixaDS.colors.brand,
                content = CaixaDSColorTokens.White
            )
        }
    }
)
