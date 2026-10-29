@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSNotificationPlaceholder
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSContentState
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSFingerprintIcon
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSIconButton
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSLabelledField
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSLoading
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSLoteriasLogo
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSNumberBall
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSSectionTitle
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS

internal val componentsPreviews1 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSButton") { state ->
        with(state) {
            CaixaDSButton(
                text = actionLabel,
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSIconButton") { state ->
        with(state) {
            CaixaDSIconButton(
                icon = CaixaDSNotificationPlaceholder,
                contentDescription = actionLabel,
                onClick = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSSectionTitle") { state ->
        with(state) {
            CaixaDSSectionTitle(title = sample)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog") { state ->
        with(state) {
            OpenPreview(state) {
                CaixaDSAlertDialog(
                    title = sample,
                    message = hint,
                    confirmText = closeLabel,
                    onConfirm = dismiss,
                    onDismiss = dismiss
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSLoading") { state ->
        with(state) {
            CaixaDSLoading(label = sample)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSLoteriasLogo") { state ->
        with(state) {
            CaixaDSLoteriasLogo(
                contentDescription = sample,
                modifier = Modifier.size(
                    140.dp,
                    60.dp
                )
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSFingerprintIcon") { state ->
        with(state) {
            CaixaDSFingerprintIcon(
                color = CaixaDS.colors.brand,
                modifier = Modifier.size(64.dp)
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSNumberBall") { state ->
        with(state) {
            CaixaDSNumberBall(
                number = 7,
                selected = checked
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSContentState") { state ->
        with(state) {
            CaixaDSContentState(
                loading = false,
                error = null,
                padding = PaddingValues(8.dp),
                onRetry = action,
                content = { CaixaDSText(hint) }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSLabelledField") { state ->
        with(state) {
            CaixaDSLabelledField(
                label = sample,
                value = text,
                onValueChange = { text = it },
                placeholder = hint
            )
        }
    }
)
