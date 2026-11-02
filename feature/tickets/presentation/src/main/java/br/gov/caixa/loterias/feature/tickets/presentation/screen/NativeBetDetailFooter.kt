package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.cart.LoteriasCaixaDSCartSummaryTokens
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSBetCartIcon
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSPurchaseFavoriteIcon
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import java.math.BigDecimal

@Composable
internal fun NativeBetDetailFooter(
    style: LoteriasCaixaDSLotteryStyle,
    canRepeat: Boolean,
    onEdit: (() -> Unit)?,
    onCart: () -> Unit,
    onAddToCart: (() -> Unit)?,
    onFavorite: () -> Unit,
    saved: Boolean,
    total: BigDecimal,
    count: Int,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val gray = LoteriasCaixaDSColorTokens.SocialGraphite
    Column(
        Modifier.fillMaxWidth().height(if (canRepeat) 172.dp else 70.dp).background(LoteriasCaixaDSColorTokens.White)
            .legacyAutomationId("bet-detail-footer")
    ) {
        if (canRepeat) {
            NativeBetRepeatActions(style, saved, onFavorite, onEdit, onAddToCart)
        }
        Spacer(Modifier.weight(1f))
        Row(
            Modifier.fillMaxWidth().height(54.dp).padding(start = 60.dp, end = 40.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = uiStrings.getString(
                        LocalizedR.string.ui_carrinho_de_apostas_1_s_itens_e4b649,
                        count.toString()
                    )
                }
                .uiId("tickets.0200").clickable(onClick = onCart),
            verticalAlignment = Alignment.Top
        ) {
            Column(Modifier.weight(1f).padding(end = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_total_do_carrinho_b01305),
                    color = gray,
                    fontSize = 11.sp,
                    modifier = Modifier.uiId("tickets.0201")
                )
                Text(
                    formatBrazilianCurrency(total),
                    color = gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .uiId("tickets.0202")
                )
            }
            Box(Modifier.width(1.dp).height(32.dp).background(LoteriasCaixaDSColorTokens.SimulationMuted))
            Box(Modifier.width(120.dp).height(42.dp).padding(start = 30.dp)) {
                Icon(LoteriasCaixaDSBetCartIcon, null, (Modifier.size(34.dp, 30.dp)).uiId("tickets.0203"), tint = gray)
                Box(
                    Modifier.padding(start = 19.dp).size(15.dp).background(LoteriasCaixaDSCartSummaryTokens.BadgeColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        count.toString(),
                        color = gray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .uiId("tickets.0204")
                    )
                }
            }
        }
    }
}

@Composable
private fun NativeFooterOutline(label: String, modifier: Modifier, action: (() -> Unit)?, star: Boolean = false) {
    val gray = LoteriasCaixaDSColorTokens.SocialGraphite
    Row(
        modifier.height(44.dp).border(1.dp, LoteriasCaixaDSColorTokens.BetIcon, RoundedCornerShape(8.dp))
            .uiId("tickets.0205").clickable(enabled = action != null) { action?.invoke() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (star) {
            Icon(
                LoteriasCaixaDSPurchaseFavoriteIcon,
                null,
                (Modifier.padding(end = 6.dp).size(18.dp)).uiId("tickets.0206"),
                tint = gray
            )
        }
        Text(label, color = gray, fontSize = 13.sp, modifier = Modifier.uiId("tickets.0207"))
    }
}

@Composable
private fun NativeBetRepeatActions(
    style: LoteriasCaixaDSLotteryStyle,
    saved: Boolean,
    onFavorite: () -> Unit,
    onEdit: (() -> Unit)?,
    onAddToCart: (() -> Unit)?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NativeFooterOutline(
                if (saved) {
                    uiStrings.getString(
                        LocalizedR.string.ui_aposta_favorita_5aae3e
                    )
                } else {
                    uiStrings.getString(
                        LocalizedR.string.ui_favoritar_aposta_a34b1e
                    )
                },
                (Modifier.weight(1f)).uiId("tickets.0196"),
                onFavorite,
                star = true
            )
            NativeFooterOutline(
                uiStrings.getString(LocalizedR.string.ui_editar_aposta_d694dd),
                (Modifier.weight(1f))
                    .uiId("tickets.0197"),
                onEdit
            )
        }
        Box(
            Modifier.padding(top = 8.dp).fillMaxWidth().height(44.dp).background(style.dark, RoundedCornerShape(5.dp))
                .uiId("tickets.0198").clickable(enabled = onAddToCart != null) { onAddToCart?.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_adicionar_ao_carrinho_bf86b8),
                color = style.darkContent,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.uiId("tickets.0199")
            )
        }
    }
}
