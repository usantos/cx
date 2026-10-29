package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSBetTypeIcons
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSCurrentCartChevron
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSCurrentCartDeleteIcon
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSCurrentCartColors
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.presentation.components.CartLotecaContent
import br.gov.caixa.loterias.feature.cart.presentation.components.cartWholeBlockReadout
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartNativeText
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartText
import br.gov.caixa.loterias.feature.cart.presentation.mapper.currentCartModalityName
import br.gov.caixa.loterias.feature.cart.presentation.mapper.formatCartAmount
import br.gov.caixa.loterias.feature.cart.presentation.mapper.label
import br.gov.caixa.loterias.feature.cart.presentation.mapper.poolExpiry
import br.gov.caixa.loterias.feature.cart.presentation.mapper.quantityLabel
import java.time.Instant
import java.util.Locale
import kotlin.math.roundToInt

internal val CurrentCartBlue = CaixaDSCurrentCartColors.Primary

internal val CurrentCartText = CaixaDSCurrentCartColors.Text

private val CurrentCartLine = CaixaDSCurrentCartColors.Divider

/** item_aposta_carrinho usa uma divisão de 75/25 e seis números em cada linha de texto regular. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CurrentCartRow(item: CartItem, now: Instant, onDelete: () -> Unit, onDetail: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (item.kind != CartItemKind.INDIVIDUAL_BET) {
        CurrentCartGroupedRow(item, now, onDelete, onDetail)
        return
    }
    Column(Modifier.fillMaxWidth().cartWholeBlockReadout(item, now)) {
        // A CX ancora as linhas seguintes no TextView, enquanto o ícone de exclusão de 25dp se sobrepõe a ele.
        Box(Modifier.fillMaxWidth()) {
            Text(
                currentCartModalityName(item),
                (Modifier.fillMaxWidth().padding(end = 33.dp)).uiId("cart.0112"),
                style = cartNativeText(16, FontWeight.Bold),
                color = currentCartModalityColor(item.modalityCode)
            )
            Box(Modifier.matchParentSize()) {
                Box(Modifier.align(Alignment.TopEnd)) { CurrentCartDelete(onDelete, 25) }
            }
        }
        val loteca = item.modalityCode.equals("LOTECA", ignoreCase = true)
        val superSeven = item.modalityCode.uppercase(Locale.ROOT) in setOf("SUPER_7", "SUPER_SETE", "SUPERSETE", "SUPER-SETE")
        val superSevenColumnWidth = with(LocalDensity.current) { 22.dp.toPx().toInt().toDp() }
        val superSevenColumnGap = with(LocalDensity.current) { 6.dp.toPx().toInt().toDp() }
        val cloverText = when {
            item.modalityCode.uppercase(Locale.ROOT) in setOf("MAIS_MILIONARIA", "MAISMILIONARIA", "+MILIONARIA") &&
                CartItemBadge.SURPRISE in item.badges -> "trevos: XX - XX"

            item.selectedClovers.isNotEmpty() -> "trevos: ${item.selectedClovers.joinToString(" - ") { it.toString().padStart(2, '0') }}"

            else -> null
        }
        val extras = buildList {
            cloverText?.let(::add)
            item.complementaryLabel
                ?.takeIf { it.isNotBlank() && (cloverText == null || !it.startsWith("Trevos:", ignoreCase = true)) }
                ?.let { label ->
                    val nativeExtra = label.startsWith("Mês da sorte:", ignoreCase = true) ||
                        label.startsWith("Time do coração:", ignoreCase = true)
                    add(if (nativeExtra) label.lowercase(Locale.ROOT) else label)
                }
        }
        val indicators = listOf(CartItemBadge.MIRROR, CartItemBadge.SURPRISE, CartItemBadge.RECURRING).filter { it in item.badges }
        Layout(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), content = {
            Column {
                if (loteca) {
                    // As seleções de partidas ocupam toda a largura do cartão abaixo do concurso e do valor.
                } else if (superSeven) {
                    Row(Modifier.padding(start = superSevenColumnGap), horizontalArrangement = Arrangement.spacedBy(superSevenColumnGap)) {
                        repeat(7) { column ->
                            Column(Modifier.width(superSevenColumnWidth), horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    Modifier.size(superSevenColumnWidth).background(CaixaDSColorTokens.TicketChoiceMuted)
                                        .legacyAutomationId("cart-super-seven-header-$column"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        (column + 1).toString(),
                                        color = CaixaDSColorTokens.White,
                                        style = cartNativeText(14, FontWeight.Bold).copy(fontFamily = CaixaDSFuturaFontFamily),
                                        modifier = Modifier.uiId("cart.0113")
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                val numbers = if (CartItemBadge.SURPRISE in item.badges) {
                                    listOf("XX")
                                } else {
                                    item.selectedNumbers.getOrNull(column).orEmpty().map(Int::toString)
                                }
                                numbers.forEach {
                                    Text(
                                        it,
                                        style = cartText(12).copy(fontFamily = CaixaDSFuturaFontFamily),
                                        color = CaixaDSCurrentCartColors.PoolText,
                                        modifier = Modifier.uiId("cart.0114")
                                    )
                                }
                            }
                        }
                    }
                } else {
                    val lines = if (CartItemBadge.SURPRISE in item.badges) {
                        listOf(List(6) { "XX" }.joinToString(" - "))
                    } else {
                        item.selectedNumbers.flatten().chunked(6).map { numbers ->
                            numbers.joinToString(" - ") { it.toString().padStart(2, '0') }
                        }
                    }
                    lines.forEach {
                        Text(
                            it,
                            style = cartText().copy(letterSpacing = 1.4.sp),
                            color = CurrentCartText,
                            modifier = Modifier
                                .uiId("cart.0115")
                        )
                    }
                    if (lines.isEmpty() &&
                        item.description.isNotBlank()
                    ) {
                        Text(item.description, style = cartNativeText(), color = CurrentCartText, modifier = Modifier.uiId("cart.0116"))
                    }
                }
            }
            Text(
                item.contestNumber?.let { "Conc. $it" }.orEmpty(),
                style = cartNativeText(),
                color = CurrentCartText,
                maxLines = 1,
                modifier = Modifier.uiId("cart.0117")
            )
            Text(
                formatCartAmount(item.amount),
                style = cartNativeText().copy(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                color = CurrentCartText,
                modifier = Modifier.uiId("cart.0118")
            )
            Column {
                extras.forEach {
                    Text(
                        it,
                        style = cartNativeText().copy(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                        color = CurrentCartText,
                        modifier = Modifier.uiId("cart.0119")
                    )
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                indicators.forEach { badge ->
                    val icon = when (badge) {
                        CartItemBadge.MIRROR -> CaixaDSBetTypeIcons.Mirror
                        CartItemBadge.SURPRISE -> CaixaDSBetTypeIcons.Surprise
                        else -> CaixaDSBetTypeIcons.Recurring
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, null, (Modifier.size(icon.defaultWidth, icon.defaultHeight)).uiId("cart.0120"), tint = CurrentCartText)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            when (badge) {
                                CartItemBadge.MIRROR -> uiStrings.getString(LocalizedR.string.ui_espelho_0c9ce2)
                                CartItemBadge.SURPRISE -> uiStrings.getString(LocalizedR.string.ui_surpresinha_34302e)
                                else -> uiStrings.getString(LocalizedR.string.ui_teimosinha_dba980)
                            },
                            style = cartNativeText(14, FontWeight.Bold).copy(fontFamily = CaixaDSFuturaFontFamily),
                            color = CurrentCartText,
                            modifier = Modifier.uiId("cart.0121")
                        )
                    }
                }
            }
        }) { measurables, constraints ->
            // Reproduz as âncoras independentes de ConstraintLayout da CX;
            // a coluna de preço não pode empurrar extras ou indicadores para baixo.
            val split = (constraints.maxWidth * 0.75f).toInt()
            val left = constraints.copy(minWidth = 0, minHeight = 0, maxWidth = split - 8.dp.roundToPx())
            val right = constraints.copy(minWidth = 0, minHeight = 0, maxWidth = constraints.maxWidth - split)
            val numbers = measurables[0].measure(left)
            val contest = measurables[1].measure(right)
            val price = measurables[2].measure(right)
            val extra = measurables[3].measure(left.copy(maxWidth = split))
            val badges = measurables[4].measure(left.copy(maxWidth = split))
            val contestY = if (superSeven) -8.dp.roundToPx() else 0
            val extraY = numbers.height
            val priceY = if (extras.isNotEmpty()) {
                extraY + extra[FirstBaseline] - price[FirstBaseline]
            } else {
                contestY + contest.height + 12.dp.roundToPx()
            }
            val badgeY = when {
                extras.isNotEmpty() -> extraY + extra.height
                superSeven || (CartItemBadge.SURPRISE !in item.badges && item.selectedNumbers.flatten().size > 6) -> numbers.height
                else -> priceY + ((price.height - badges.height) / 2f).roundToInt()
            }
            val height = maxOf(
                numbers.height,
                contest.height,
                priceY + price.height,
                extraY + extra.height,
                if (indicators.isNotEmpty()) badgeY + badges.height else 0
            )
            layout(constraints.maxWidth, height) {
                numbers.placeRelative(0, 0)
                contest.placeRelative(constraints.maxWidth - contest.width, contestY)
                price.placeRelative(constraints.maxWidth - price.width, priceY)
                extra.placeRelative(0, extraY)
                badges.placeRelative(0, badgeY)
            }
        }
        if (loteca) CartLotecaContent(item.lotecaSelections, groupedReadout = true)
        CurrentCartDivider()
    }
}

/** As linhas XML agrupadas ancoram cada campo de forma independente; os limites de exclusão não dimensionam o título. */
@Composable
private fun CurrentCartGroupedRow(item: CartItem, now: Instant, onDelete: () -> Unit, onDetail: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val share = item.poolShare
    val expiry = share?.let { poolExpiry(it.expiresAt, now) }
    val pool = share != null
    val bodyColor = if (pool) CaixaDSCurrentCartColors.PoolText else CurrentCartText
    Layout(modifier = Modifier.fillMaxWidth().padding(top = if (pool) 16.dp else 0.dp).cartWholeBlockReadout(item, now), content = {
        Text(
            if (pool) currentCartModalityName(item.copy(title = item.title.substringBefore(" • "))) else item.title,
            style = cartNativeText(14, if (pool) FontWeight.Bold else FontWeight.SemiBold),
            color = if (pool) currentCartModalityColor(item.modalityCode) else CurrentCartText,
            modifier = Modifier.uiId("cart.0122")
        )
        CurrentCartDelete(onDelete, if (pool) 24 else 32)
        Text(
            if (share != null) {
                uiStrings.getString(
                    LocalizedR.string.ui_cota_reservada_no_1_s_de_2_s_7b73c7,
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (expiry != null) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_expira_em_e5b7cd),
                    style = cartNativeText().copy(fontFamily = CaixaDSFuturaFontFamily),
                    color = CurrentCartBlue,
                    modifier = Modifier.uiId("cart.0126")
                )
                Icon(CaixaDSIcons.Time, null, (Modifier.padding(start = 4.dp).size(16.dp)).uiId("cart.0127"), tint = CurrentCartBlue)
                Text(
                    expiry.remainingLabel,
                    (Modifier.padding(start = 4.dp)).uiId("cart.0128"),
                    style = cartNativeText(14, FontWeight.Bold),
                    color = CurrentCartBlue
                )
            }
        }
        Row(
            Modifier.then(if (onDetail != null) Modifier.uiId("cart.0129").clickable(onClick = onDetail) else Modifier),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_mais_detalhes_269973),
                style = cartNativeText(14, if (pool) FontWeight.Bold else FontWeight.SemiBold),
                color = if (pool) CurrentCartBlue else CaixaDSCurrentCartColors.ComboDetail,
                modifier = Modifier.uiId("cart.0130")
            )
            Spacer(Modifier.width(if (pool) 5.dp else 8.dp))
            Icon(CaixaDSCurrentCartChevron, null, (Modifier.size(16.dp)).uiId("cart.0131"), tint = CurrentCartBlue)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDSCurrentCartColors.GroupDivider))
    }, measurePolicy = currentCartGroupedMeasurePolicy(pool))
}

private fun currentCartGroupedMeasurePolicy(pool: Boolean) = MeasurePolicy { measurables, constraints ->
    val loose = constraints.copy(minWidth = 0, minHeight = 0)
    val title = measurables[0].measure(
        loose.copy(maxWidth = (constraints.maxWidth - (if (pool) 24.dp else 44.dp).roundToPx()).coerceAtLeast(0))
    )
    val delete = measurables[1].measure(loose)
    val contest = measurables[3].measure(loose)
    val count = measurables[2].measure(loose.copy(maxWidth = (constraints.maxWidth - contest.width).coerceAtLeast(0)))
    val price = measurables[4].measure(loose)
    val timer = measurables[5].measure(loose)
    val detail = measurables[6].measure(loose)
    val divider = measurables[7].measure(loose)
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
private fun CurrentCartDelete(onDelete: () -> Unit, size: Int) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val artworkSize = when (size) {
        // O recurso nativo de lixeira possui uma caixa intrínseca de 20dp com seus próprios recuos de visualização.
        // Reaplicar o espaçamento de ImageButton do XML deixa apenas cerca de 4 x 5dp de desenho visível.
        25 -> 20

        32 -> 18

        // ImageButton do combo: 32dp menos 7dp de espaçamento em ambos os lados.
        else -> 24 // ImageView do bolão ajusta o drawable aos seus limites de 24dp.
    }
    IconButton(onClick = onDelete, modifier = (Modifier.size(size.dp)).uiId("cart.0132")) {
        Icon(
            CaixaDSCurrentCartDeleteIcon,
            uiStrings.getString(LocalizedR.string.ui_excluir_8b1951),
            (Modifier.size(artworkSize.dp)).uiId("cart.0133"),
            tint = if (size == 25) CaixaDSCurrentCartColors.Delete else Color.Unspecified
        )
    }
}

@Composable
private fun CurrentCartDivider() {
    Spacer(Modifier.height(14.dp))
    Box(Modifier.fillMaxWidth().height(1.dp).background(CurrentCartLine))
    Spacer(Modifier.height(12.dp))
}

@Composable
internal fun currentCartModalityColor(code: String): Color {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val supported = code.uppercase(Locale.ROOT).replace("-", "").replace("_", "") in setOf(
        "MEGASENA", "LOTOFACIL", uiStrings.getString(LocalizedR.string.ui_lotofacil_dd4339), "QUINA", "LOTOMANIA", "TIMEMANIA", "DUPLASENA",
        "DIADESORTE", "SUPER7", "SUPERSETE", "MAISMILIONARIA", "+MILIONARIA", "LOTECA"
    )
    if (!supported) return CurrentCartBlue
    val style = caixaDSLotteryStyle(0, code)
    val nativeLightTitle = code.uppercase(Locale.ROOT).replace("-", "").replace("_", "") in
        setOf("SUPER7", "SUPERSETE", "TIMEMANIA", "DIADESORTE")
    return if (nativeLightTitle) style.light else style.listText
}
