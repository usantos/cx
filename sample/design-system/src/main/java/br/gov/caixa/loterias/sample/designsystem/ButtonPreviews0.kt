@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSFooterButton
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSOutlinedButton
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSPrimaryButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

internal val buttonPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSPrimaryButton") { state ->
        with(state) {
            LoteriasCaixaDSPrimaryButton(
                text = actionLabel,
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSFooterButton") { state ->
        with(state) {
            LoteriasCaixaDSFooterButton(
                text = actionLabel,
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSOutlinedButton") { state ->
        with(state) {
            LoteriasCaixaDSOutlinedButton(
                text = actionLabel,
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSBettingButton") { state ->
        with(state) {
            LoteriasCaixaDSBettingButton(
                text = actionLabel,
                onClick = action,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                color = LoteriasCaixaDS.colors.brand,
                content = LoteriasCaixaDSColorTokens.White
            )
        }
    }
)
