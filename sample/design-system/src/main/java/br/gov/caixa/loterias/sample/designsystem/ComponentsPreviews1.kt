@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSNotificationPlaceholder
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSContentState
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSFingerprintIcon
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSIconButton
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSLabelledField
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSLoading
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSLoteriasLogo
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSNumberBall
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSectionTitle
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS

internal val componentsPreviews1 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSButton") { state ->
        with(state) {
            LoteriasCaixaDSButton(
                text = actionLabel,
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSIconButton") { state ->
        with(state) {
            LoteriasCaixaDSIconButton(
                icon = LoteriasCaixaDSNotificationPlaceholder,
                contentDescription = actionLabel,
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSectionTitle") { state ->
        with(state) {
            LoteriasCaixaDSSectionTitle(title = sample)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAlertDialog") { state ->
        with(state) {
            OpenPreview(state) {
                LoteriasCaixaDSAlertDialog(
                    title = sample,
                    message = hint,
                    confirmText = closeLabel,
                    onConfirm = dismiss,
                    onDismiss = dismiss
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSLoading") { state ->
        with(state) {
            LoteriasCaixaDSLoading(label = sample)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSLoteriasLogo") { state ->
        with(state) {
            LoteriasCaixaDSLoteriasLogo(
                contentDescription = sample,
                modifier = Modifier.size(
                    140.dp,
                    60.dp
                )
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSFingerprintIcon") { state ->
        with(state) {
            LoteriasCaixaDSFingerprintIcon(
                color = LoteriasCaixaDS.colors.brand,
                modifier = Modifier.size(64.dp)
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSNumberBall") { state ->
        with(state) {
            LoteriasCaixaDSNumberBall(
                number = 7,
                selected = checked
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSContentState") { state ->
        with(state) {
            LoteriasCaixaDSContentState(
                loading = false,
                error = null,
                padding = PaddingValues(8.dp),
                onRetry = action,
                content = { LoteriasCaixaDSText(hint) }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSLabelledField") { state ->
        with(state) {
            LoteriasCaixaDSLabelledField(
                label = sample,
                value = text,
                onValueChange = { text = it },
                placeholder = hint
            )
        }
    }
)
