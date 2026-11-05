package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSCurrentCartChevron
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSCurrentCartColors
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.feature.cart.presentation.components.cartWholeBlockReadout
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartNativeText
import br.gov.caixa.loterias.feature.cart.presentation.mapper.currentCartModalityName
import br.gov.caixa.loterias.feature.cart.presentation.mapper.formatCartAmount
import br.gov.caixa.loterias.feature.cart.presentation.mapper.poolExpiry
import br.gov.caixa.loterias.feature.cart.presentation.mapper.quantityLabel
import br.gov.caixa.loterias.feature.cart.ui.R as DeliveryR
import java.time.Instant
import kotlin.math.roundToInt

@Composable
fun CurrentCartGroupedRowDelivered(item: CartItem, now: Instant, onDelete: () -> Unit, onDetail: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val share = item.poolShare
    val expiry = share?.let { poolExpiry(it.expiresAt, now) }
    val pool = share != null
    val bodyColor = if (pool) LoteriasCaixaDSCurrentCartColors.PoolText else CurrentCartText
    Layout(modifier = Modifier.fillMaxWidth().padding(top = if (pool) 16.dp else 0.dp).cartWholeBlockReadout(item, now), content = {
        Text(
            if (pool) currentCartModalityName(item.copy(title = item.title.substringBefore(" • "))) else item.title,
            style = cartNativeText(GROUPED_LABEL_TEXT_SIZE, if (pool) FontWeight.Bold else FontWeight.SemiBold),
            color = if (pool) currentCartModalityColor(item.modalityCode) else CurrentCartText,
            modifier = Modifier.uiId("cart.0122")
        )
        CurrentCartDeleteDelivered(onDelete, if (pool) POOL_DELETE_TARGET_DP else COMBO_DELETE_TARGET_DP)
        Text(
            if (share != null) {
                uiStrings.getString(
                    DeliveryR.string.ui_cota_reservada_no_1_s_de_2_s_7b73c7,
                    share.reservedShare.toString(),
                    share.totalShares.toString()
                )
            } else {
                quantityLabel(item.comboBetCount, "aposta", "apostas")
            },
            style = cartNativeText(),
            color = bodyColor,
            modifier = Modifier.uiId("cart.0123")
        )
        Text(
            if (pool) item.contestNumber?.let { "Conc. $it" }.orEmpty() else "",
            style = cartNativeText(),
            color = bodyColor,
            modifier = Modifier.uiId("cart.0124")
        )
        Text(formatCartAmount(item.amount), style = cartNativeText(), color = bodyColor, modifier = Modifier.uiId("cart.0125"))
        CurrentCartPoolTimer(expiry)
        CurrentCartGroupedDetail(pool, onDetail)
        Box(Modifier.fillMaxWidth().height(1.dp).background(LoteriasCaixaDSCurrentCartColors.GroupDivider))
    }, measurePolicy = currentCartGroupedMeasurePolicy(pool))
}

private fun currentCartGroupedMeasurePolicy(pool: Boolean) = MeasurePolicy { measurables, constraints ->
    val loose = constraints.copy(minWidth = 0, minHeight = 0)
    val title = measurables[0].measure(
        loose.copy(maxWidth = (constraints.maxWidth - (if (pool) 24.dp else 44.dp).roundToPx()).coerceAtLeast(0))
    )
    val delete = measurables[1].measure(loose)
    val contest = measurables[CONTEST_MEASURABLE_INDEX].measure(loose)
    val count = measurables[2].measure(loose.copy(maxWidth = (constraints.maxWidth - contest.width).coerceAtLeast(0)))
    val price = measurables[PRICE_MEASURABLE_INDEX].measure(loose)
    val timer = measurables[TIMER_MEASURABLE_INDEX].measure(loose)
    val detail = measurables[DETAIL_MEASURABLE_INDEX].measure(loose)
    val divider = measurables[DIVIDER_MEASURABLE_INDEX].measure(loose)
    val deleteY = ((title.height - delete.height) / 2f).roundToInt()
    val countAnchor = title.height + (if (pool) 8.dp else 6.dp).roundToPx()
    val priceY = if (pool) countAnchor + count.height + 12.dp.roundToPx() else deleteY + delete.height
    val countY = if (pool) {
        countAnchor
    } else {
        ((countAnchor + priceY + price.height - count.height) / 2f).roundToInt()
    }
    val contestY = countY + count[FirstBaseline] - contest[FirstBaseline]
    val timerY = priceY + price.height + 16.dp.roundToPx()
    val detailY = if (pool) timerY + timer.height - detail.height else priceY + price.height + 8.dp.roundToPx()
    val dividerY = detailY + detail.height + 13.dp.roundToPx()
    val bottomPadding = if (pool) 0 else 16.dp.roundToPx()
    layout(constraints.maxWidth, dividerY + divider.height + bottomPadding) {
        title.placeRelative(0, 0)
        delete.placeRelative(constraints.maxWidth - delete.width, deleteY)
        count.placeRelative(0, countY)
        if (pool) contest.placeRelative(constraints.maxWidth - contest.width, contestY)
        price.placeRelative(constraints.maxWidth - price.width, priceY)
        if (pool) timer.placeRelative(0, timerY)
        detail.placeRelative(constraints.maxWidth - detail.width, detailY)
        divider.placeRelative(0, dividerY)
    }
}

@Composable
private fun CurrentCartPoolTimer(expiry: br.gov.caixa.loterias.feature.cart.presentation.mapper.PoolExpiry?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (expiry != null) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_expira_em_e5b7cd),
                style = cartNativeText().copy(fontFamily = LoteriasCaixaDSFuturaFontFamily),
                color = CurrentCartBlue,
                modifier = Modifier.uiId("cart.0126")
            )
            Icon(LoteriasCaixaDSIcons.Time, null, (Modifier.padding(start = 4.dp).size(16.dp)).uiId("cart.0127"), tint = CurrentCartBlue)
            Text(
                expiry.remainingLabel,
                (Modifier.padding(start = 4.dp)).uiId("cart.0128"),
                style = cartNativeText(GROUPED_LABEL_TEXT_SIZE, FontWeight.Bold),
                color = CurrentCartBlue
            )
        }
    }
}

@Composable
private fun CurrentCartGroupedDetail(pool: Boolean, onDetail: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.then(if (onDetail != null) Modifier.uiId("cart.0129").clickable(onClick = onDetail) else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_mais_detalhes_269973),
            style = cartNativeText(GROUPED_LABEL_TEXT_SIZE, if (pool) FontWeight.Bold else FontWeight.SemiBold),
            color = if (pool) CurrentCartBlue else LoteriasCaixaDSCurrentCartColors.ComboDetail,
            modifier = Modifier.uiId("cart.0130")
        )
        Spacer(Modifier.width(if (pool) 5.dp else 8.dp))
        Icon(LoteriasCaixaDSCurrentCartChevron, null, (Modifier.size(16.dp)).uiId("cart.0131"), tint = CurrentCartBlue)
    }
}

private const val GROUPED_LABEL_TEXT_SIZE = 14
private const val POOL_DELETE_TARGET_DP = 24
private const val COMBO_DELETE_TARGET_DP = 32
private const val CONTEST_MEASURABLE_INDEX = 3
private const val PRICE_MEASURABLE_INDEX = 4
private const val TIMER_MEASURABLE_INDEX = 5
private const val DETAIL_MEASURABLE_INDEX = 6
private const val DIVIDER_MEASURABLE_INDEX = 7
