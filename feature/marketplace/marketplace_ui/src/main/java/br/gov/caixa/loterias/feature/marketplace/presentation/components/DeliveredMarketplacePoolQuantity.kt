package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.ui.R as DeliveryR

private const val QUANTITY_MINUS_TEXT_SIZE = 24
private const val QUANTITY_ACTION_TEXT_SIZE = 16
private const val QUANTITY_LABEL_TEXT_SIZE = 16
private const val QUANTITY_VALUE_TEXT_SIZE = 18

@Composable
internal fun PoolQuantityButton(label: String, enabled: Boolean, colors: MarketplaceColors, tag: String, action: () -> Unit) {
    val largeFont = LocalDensity.current.fontScale > 1f
    Box(
        Modifier.then(if (largeFont) Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp).padding(4.dp) else Modifier.size(32.dp))
            .clip(RoundedCornerShape(4.dp)).background(if (enabled) colors.dark else LoteriasCaixaDSColorTokens.NotificationSearch)
            .legacyAutomationId(tag).uiId("pool.0017").clickable(enabled = enabled, role = Role.Button, onClick = action)
            .semantics {
                contentDescription = if (label ==
                    "−"
                ) {
                    "Diminuir quantidade de cotas"
                } else {
                    "Aumentar quantidade de cotas"
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = LoteriasCaixaDSColorTokens.White,
            style = marketplaceText(if (label == "−") QUANTITY_MINUS_TEXT_SIZE else QUANTITY_ACTION_TEXT_SIZE),
            modifier = Modifier
                .uiId("pool.0018")
        )
    }
}

@Composable
internal fun androidx.compose.foundation.layout.BoxScope.MarketplacePoolQuantity(
    pool: MarketplacePool,
    shares: Int,
    adding: Boolean,
    colors: MarketplaceColors,
    actions: MarketplaceCardActions,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.align(Alignment.TopCenter).offset(y = 295.dp).width(IntrinsicSize.Max),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.height(22.dp), contentAlignment = Alignment.Center) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_quantidade_de_cotas_ddb3fd),
                style = marketplaceText(QUANTITY_LABEL_TEXT_SIZE),
                color = LoteriasCaixaDSColorTokens.TicketChoiceMuted,
                modifier = Modifier.uiId("pool.0025")
            )
        }
        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp).height(32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PoolQuantityButton("−", shares > 1 && !adding, colors, "carousel-decrease-${pool.code}", actions.decrease)
            Box(Modifier.width(50.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(
                    shares.toString(),
                    style = marketplaceText(QUANTITY_VALUE_TEXT_SIZE),
                    color = LoteriasCaixaDSColorTokens.SocialGraphite,
                    modifier = Modifier
                        .uiId("pool.0026")
                )
                Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp).width(34.dp).height(1.dp).background(colors.dark))
            }
            PoolQuantityButton(
                "+",
                shares < pool.availableShares && !adding,
                colors,
                "carousel-increase-${pool.code}",
                actions.increase
            )
        }
    }
}
