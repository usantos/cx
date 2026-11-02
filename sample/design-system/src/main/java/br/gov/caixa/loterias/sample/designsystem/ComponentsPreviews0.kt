@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSNotificationPlaceholder
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountChoice
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountCopy
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountField
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountInlineWarning
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountSaveFooter
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAnchoredTooltip
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAnimation
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAuthenticationLoading
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSBetTypeItem
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSBetTypeStrip
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

internal val componentsPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountField") { state ->
        with(state) {
            LoteriasCaixaDSAccountField(
                label = sample,
                value = text,
                onValueChange = { text = it },
                emptyMask = hint
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountInlineWarning") { state ->
        with(state) {
            LoteriasCaixaDSAccountInlineWarning(message = hint)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountChoice") { state ->
        with(state) {
            LoteriasCaixaDSAccountChoice(
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
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountSaveFooter") { state ->
        with(state) {
            LoteriasCaixaDSAccountSaveFooter(
                label = actionLabel,
                onClick = action,
                enabled = true
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountCopy") { state ->
        with(state) {
            LoteriasCaixaDSAccountCopy(
                value = sample,
                size = 18f,
                bold = true
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAnchoredTooltip") { state ->
        with(state) {
            OpenPreview(state) {
                LoteriasCaixaDSAnchoredTooltip(
                    text = sample,
                    onDismiss = dismiss
                )
            }
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAnimation") { state ->
        with(state) {
            val context = LocalContext.current
            LoteriasCaixaDSAnimation(
                composition = remember(context) {
                    com.airbnb.lottie.LottieCompositionFactory.fromRawResSync(context, R.raw.catalog_animation).value
                },
                progress = { 0.5f },
                modifier = Modifier.size(120.dp)
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAuthenticationLoading") { state ->
        with(state) {
            LoteriasCaixaDSAuthenticationLoading(label = sample)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAuthenticationLoadingDialog") { state ->
        with(state) {
            TimedLoadingPreview(state)
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSBetTypeStrip") { state ->
        with(state) {
            LoteriasCaixaDSBetTypeStrip(
                items = listOf(
                    LoteriasCaixaDSBetTypeItem(
                        sample,
                        LoteriasCaixaDSNotificationPlaceholder
                    )
                ),
                backgroundColor = LoteriasCaixaDS.colors.brand,
                contentColor = LoteriasCaixaDSColorTokens.White
            )
        }
    }
)
