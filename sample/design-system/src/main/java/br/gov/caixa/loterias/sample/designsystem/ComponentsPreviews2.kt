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
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSChoiceRow
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSDivider
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSEmptyState
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFavoriteRetailer
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplacePoolExpansion
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplacePoolSummary
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceTab
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceToolbarIcon
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSSectionHeader
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

internal val componentsPreviews2 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSSectionHeader") { state ->
        with(state) {
            CaixaDSSectionHeader(
                title = sample,
                subtitle = hint
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSCard") { state ->
        with(state) {
            CaixaDSCard(content = { CaixaDSText(sample) })
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSChoiceRow") { state ->
        with(state) {
            CaixaDSChoiceRow(
                title = sample,
                subtitle = hint,
                selected = checked,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSDivider") { state ->
        with(state) {
            CaixaDSDivider()
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSEmptyState") { state ->
        with(state) {
            CaixaDSEmptyState(
                title = sample,
                message = hint,
                actionLabel = actionLabel,
                onAction = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceTab") { state ->
        with(state) {
            CaixaDSMarketplaceTab(
                label = sample,
                selected = checked,
                onClick = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceToolbarIcon") { state ->
        with(state) {
            CaixaDSMarketplaceToolbarIcon(
                help = true,
                description = sample
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplacePoolSummary") { state ->
        with(state) {
            CaixaDSMarketplacePoolSummary(
                title = sample,
                city = city,
                summary = sample,
                amount = amount,
                lotteryFiltered = false,
                prize = amount,
                prizeWords = sample,
                modalityColor = CaixaDS.colors.brand,
                expanded = checked,
                favorite = checked,
                canFavorite = true,
                onFavorite = toggle,
                onToggle = toggle
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFavoriteRetailer") { state ->
        with(state) {
            CaixaDSMarketplaceFavoriteRetailer(
                name = sample,
                city = city,
                onOpen = action,
                onRemove = action
            )
        }
    },
    preview("br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplacePoolExpansion") { state ->
        with(state) {
            CaixaDSText(stringResource(R.string.wide_preview_note))
            Box(Modifier.horizontalScroll(rememberScrollState())) {
                Box(Modifier.width(400.dp)) {
                    CaixaDSMarketplacePoolExpansion(
                        contest = "1234",
                        prognostics = AnnotatedString(sample),
                        availability = AnnotatedString(sample),
                        shares = shares,
                        canDecrease = shares > 1,
                        canIncrease = true,
                        adding = false,
                        canAdd = true,
                        color = CaixaDS.colors.brand,
                        contentColor = CaixaDSColorTokens.White,
                        detailColor = CaixaDS.colors.brand,
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
