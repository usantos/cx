package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSSurface as Surface
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.presentation.mapper.PoolExpiryState
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartModalityColor
import br.gov.caixa.loterias.feature.cart.presentation.mapper.formatCartAmount
import br.gov.caixa.loterias.feature.cart.presentation.mapper.label
import br.gov.caixa.loterias.feature.cart.presentation.mapper.poolExpiry
import br.gov.caixa.loterias.feature.cart.presentation.mapper.quantityLabel
import java.time.Instant
import java.util.Locale

@Composable
internal fun CartRow(item: CartItem, now: Instant, onDelete: (() -> Unit)?, onDetail: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val expiry = item.poolShare?.let { poolExpiry(it.expiresAt, now) }
    val expired = expiry?.state == PoolExpiryState.EXPIRED
    val modalityColor = if (expired) CaixaDSColorTokens.ReservationExpired else cartModalityColor(item.modalityCode)
    Column(
        Modifier.fillMaxWidth().background(CaixaDS.colors.surface)
            .padding(horizontal = 20.dp, vertical = 12.dp).cartWholeBlockReadout(item, now)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(
                    item.title,
                    color = modalityColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .uiId("cart.0016")
                )
                item.contestNumber?.let {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_concurso_1_s_32ee4c, it.toString()),
                        color = if (expired) CaixaDSColorTokens.ReservationExpired else CaixaDS.colors.mutedText,
                        fontSize = 12.sp,
                        modifier = Modifier.uiId("cart.0017")
                    )
                }
            }
            Text(
                formatCartAmount(item.amount),
                color = if (expired) CaixaDSColorTokens.ReservationExpired else CaixaDS.colors.text,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.uiId("cart.0018")
            )
            if (onDelete != null) {
                IconButton(onClick = onDelete, modifier = (Modifier.size(32.dp)).uiId("cart.0019")) {
                    Icon(
                        CaixaDSIcons.Delete,
                        uiStrings.getString(LocalizedR.string.ui_excluir_8b1951),
                        (Modifier.size(20.dp)).uiId("cart.0020"),
                        tint = CaixaDS.colors.error
                    )
                }
            }
        }
        if (item.badges.isNotEmpty()) {
            Row(
                Modifier.fillMaxWidth().uiId("cart.0021").horizontalScroll(rememberScrollState()).padding(top = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item.badges.forEach { CartBadge(it, modalityColor) }
            }
        }
        val rows = item.selectedNumbers.filter(List<Int>::isNotEmpty)
        if (rows.isNotEmpty()) {
            rows.forEach { numbers ->
                Text(
                    numbers.joinToString(" - ") { it.toString().padStart(2, '0') },
                    (Modifier.padding(top = 8.dp)).uiId("cart.0022"),
                    color = modalityColor,
                    fontWeight = FontWeight.Bold
                )
            }
        } else if (item.description.isNotBlank()) {
            Text(item.description, (Modifier.padding(top = 7.dp)).uiId("cart.0023"), color = CaixaDS.colors.mutedText)
        }
        if (item.selectedClovers.isNotEmpty()) {
            Text(
                uiStrings.getString(
                    LocalizedR.string.ui_trevos_1_s_946f5c,
                    item.selectedClovers.joinToString(" - ") { it.toString().padStart(2, '0') }
                ),
                (Modifier.padding(top = 5.dp)).uiId("cart.0024"),
                color = modalityColor,
                fontWeight = FontWeight.Bold
            )
        }
        if (item.kind == CartItemKind.INDIVIDUAL_BET) {
            Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                CartQuantity(
                    uiStrings.getString(LocalizedR.string.final_you_bet),
                    quantityLabel(
                        item.numberQuantity,
                        uiStrings.getString(LocalizedR.string.ui_numero_84871a),
                        uiStrings.getString(LocalizedR.string.ui_numeros_5b88ef)
                    )
                )
                CartQuantity("Para", quantityLabel(item.recurringDraws, "concurso", "concursos"))
            }
        }
        item.poolShare?.let { share ->
            val expiry = poolExpiry(share.expiresAt, now)
            val timerColor = when (expiry.state) {
                PoolExpiryState.EXPIRED -> CaixaDSColorTokens.ReservationExpired
                PoolExpiryState.WARNING -> CaixaDSColorTokens.ReservationWarning
                else -> CaixaDS.colors.mutedText
            }
            Text(
                uiStrings.getString(LocalizedR.string.ui_bolao_1_s_d4b106, share.code),
                (Modifier.padding(top = 10.dp)).uiId("cart.0025"),
                color = CaixaDS.colors.text
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_02d_02d_9c3cfc).format(Locale.ROOT, share.reservedShare, share.totalShares),
                color = if (expired) CaixaDSColorTokens.ReservationExpired else CaixaDS.colors.text,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.uiId("cart.0026")
            )
            Text(
                if (expired) {
                    uiStrings.getString(
                        LocalizedR.string.ui_cota_expirada_90c16c
                    )
                } else {
                    uiStrings.getString(LocalizedR.string.ui_cotas_ef3c1d)
                },
                color = if (expired) CaixaDSColorTokens.ReservationCancelled else timerColor,
                modifier = Modifier.uiId("cart.0027")
            )
            Row(Modifier.padding(top = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    CaixaDSIcons.Time,
                    null,
                    (Modifier.size(18.dp)).uiId("cart.0028"),
                    tint = timerColor
                )
                Text(
                    expiry.remainingLabel,
                    (Modifier.padding(start = 5.dp)).uiId("cart.0029"),
                    color = timerColor
                )
            }
        }
        if (item.comboBetCount > 0) {
            Text(
                quantityLabel(
                    item.comboBetCount,
                    uiStrings.getString(LocalizedR.string.ui_aposta_incluida_115885),
                    uiStrings.getString(LocalizedR.string.ui_apostas_incluidas_cb7197)
                ),
                (Modifier.padding(top = 8.dp)).uiId("cart.0030"),
                color = modalityColor,
                fontWeight = FontWeight.Bold
            )
        }
        item.complementaryLabel?.takeIf(String::isNotBlank)?.let {
            Text(it, (Modifier.padding(top = 7.dp)).uiId("cart.0031"), color = CaixaDS.colors.mutedText)
        }
        item.gameRows.forEach {
            Text(it, (Modifier.padding(top = 5.dp)).uiId("cart.0032"), color = CaixaDS.colors.text)
        }
        if (onDetail != null) {
            Row(
                Modifier.align(Alignment.End).uiId("cart.0033").clickable(onClick = onDetail).padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_ver_detalhes_4a4ed0),
                    color = CaixaDS.colors.brand,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("cart.0034")
                )
                Icon(CaixaDSIcons.Forward, null, (Modifier.size(18.dp)).uiId("cart.0035"), tint = CaixaDS.colors.brand)
            }
        }
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDS.colors.line))
}

@Composable
private fun CartQuantity(label: String, value: String) {
    Column {
        Text(label, color = CaixaDS.colors.mutedText, fontSize = 12.sp, modifier = Modifier.uiId("cart.0036"))
        Text(value, color = CaixaDS.colors.text, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.uiId("cart.0037"))
    }
}

@Composable
private fun CartBadge(badge: CartItemBadge, color: Color) {
    Surface(color = color, shape = RoundedCornerShape(2.dp)) {
        Text(
            badge.label,
            (Modifier.padding(horizontal = 7.dp, vertical = 3.dp)).uiId("cart.0038"),
            color = CaixaDSColorTokens.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
