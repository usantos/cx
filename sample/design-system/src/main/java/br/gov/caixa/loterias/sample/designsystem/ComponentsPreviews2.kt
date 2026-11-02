@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSChoiceRow
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSDivider
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSEmptyState
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFavoriteRetailer
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplacePoolExpansion
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplacePoolSummary
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceTab
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceToolbarIcon
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSectionHeader
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

internal val componentsPreviews2 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSectionHeader") { state ->
        with(state) {
            LoteriasCaixaDSSectionHeader(
                title = sample,
                subtitle = hint
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCard") { state ->
        with(state) {
            LoteriasCaixaDSCard(content = { LoteriasCaixaDSText(sample) })
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSChoiceRow") { state ->
        with(state) {
            LoteriasCaixaDSChoiceRow(
                title = sample,
                subtitle = hint,
                selected = checked,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSDivider") { state ->
        with(state) {
            LoteriasCaixaDSDivider()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSEmptyState") { state ->
        with(state) {
            LoteriasCaixaDSEmptyState(
                title = sample,
                message = hint,
                actionLabel = actionLabel,
                onAction = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceTab") { state ->
        with(state) {
            LoteriasCaixaDSMarketplaceTab(
                label = sample,
                selected = checked,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceToolbarIcon") { state ->
        with(state) {
            LoteriasCaixaDSMarketplaceToolbarIcon(
                help = true,
                description = sample
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplacePoolSummary") { state ->
        with(state) {
            LoteriasCaixaDSMarketplacePoolSummary(
                title = sample,
                city = city,
                summary = sample,
                amount = amount,
                lotteryFiltered = false,
                prize = amount,
                prizeWords = sample,
                modalityColor = LoteriasCaixaDS.colors.brand,
                expanded = checked,
                favorite = checked,
                canFavorite = true,
                onFavorite = toggle,
                onToggle = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFavoriteRetailer") { state ->
        with(state) {
            LoteriasCaixaDSMarketplaceFavoriteRetailer(
                name = sample,
                city = city,
                onOpen = action,
                onRemove = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplacePoolExpansion") { state ->
        with(state) {
            LoteriasCaixaDSText(stringResource(R.string.wide_preview_note))
            Box(Modifier.horizontalScroll(rememberScrollState())) {
                Box(Modifier.width(400.dp)) {
                    LoteriasCaixaDSMarketplacePoolExpansion(
                        contest = "1234",
                        prognostics = AnnotatedString(sample),
                        availability = AnnotatedString(sample),
                        shares = shares,
                        canDecrease = shares > 1,
                        canIncrease = true,
                        adding = false,
                        canAdd = true,
                        color = LoteriasCaixaDS.colors.brand,
                        contentColor = LoteriasCaixaDSColorTokens.White,
                        detailColor = LoteriasCaixaDS.colors.brand,
                        onDetail = action,
                        onDecrease = decreaseShares,
                        onIncrease = increaseShares,
                        onAdd = action
                    )
                }
            }
        }
    }
)
