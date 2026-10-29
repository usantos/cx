@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSNotificationPlaceholder
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountChoice
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountCopy
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountField
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountInlineWarning
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountSaveFooter
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAnchoredTooltip
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAnimation
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoading
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSBetTypeItem
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSBetTypeStrip
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

internal val componentsPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountField") { state ->
        with(state) {
            CaixaDSAccountField(
                label = sample,
                value = text,
                onValueChange = { text = it },
                emptyMask = hint
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountInlineWarning") { state ->
        with(state) {
            CaixaDSAccountInlineWarning(message = hint)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountChoice") { state ->
        with(state) {
            CaixaDSAccountChoice(
                selected = selection,
                options = options.mapIndexed {
                        index,
                        label,
                    ->
                    label to index
                },
                onSelected = { selection = it }
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountSaveFooter") { state ->
        with(state) {
            CaixaDSAccountSaveFooter(
                label = actionLabel,
                onClick = action,
                enabled = true
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountCopy") { state ->
        with(state) {
            CaixaDSAccountCopy(
                value = sample,
                size = 18f,
                bold = true
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSAnchoredTooltip") { state ->
        with(state) {
            OpenPreview(state) {
                CaixaDSAnchoredTooltip(
                    text = sample,
                    onDismiss = dismiss
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSAnimation") { state ->
        with(state) {
            val context = LocalContext.current
            CaixaDSAnimation(
                composition = remember(context) {
                    com.airbnb.lottie.LottieCompositionFactory.fromRawResSync(context, R.raw.catalog_animation).value
                },
                progress = { 0.5f },
                modifier = Modifier.size(120.dp)
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoading") { state ->
        with(state) {
            CaixaDSAuthenticationLoading(label = sample)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog") { state ->
        with(state) {
            TimedLoadingPreview(state)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSBetTypeStrip") { state ->
        with(state) {
            CaixaDSBetTypeStrip(
                items = listOf(
                    CaixaDSBetTypeItem(
                        sample,
                        CaixaDSNotificationPlaceholder
                    )
                ),
                backgroundColor = CaixaDS.colors.brand,
                contentColor = CaixaDSColorTokens.White
            )
        }
    }
)
