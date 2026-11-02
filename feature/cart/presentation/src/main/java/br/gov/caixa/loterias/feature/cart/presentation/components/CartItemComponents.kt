package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.presentation.mapper.PoolExpiryState
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartModalityColor
import br.gov.caixa.loterias.feature.cart.presentation.mapper.poolExpiry
import br.gov.caixa.loterias.feature.cart.presentation.mapper.quantityLabel
import java.time.Instant

@Composable
internal fun CartRow(item: CartItem, now: Instant, onDelete: (() -> Unit)?, onDetail: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val expiry = item.poolShare?.let { poolExpiry(it.expiresAt, now) }
    val expired = expiry?.state == PoolExpiryState.EXPIRED
    val modalityColor = if (expired) LoteriasCaixaDSColorTokens.ReservationExpired else cartModalityColor(item.modalityCode)
    Column(
        Modifier.fillMaxWidth().background(LoteriasCaixaDS.colors.surface)
            .padding(horizontal = 20.dp, vertical = 12.dp).cartWholeBlockReadout(item, now)
    ) {
        CartRowHeader(item, expired, modalityColor, onDelete)
        CartRowSelections(item, modalityColor)
        CartRowReservation(item, now, expired)
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
            Text(it, (Modifier.padding(top = 7.dp)).uiId("cart.0031"), color = LoteriasCaixaDS.colors.mutedText)
        }
        item.gameRows.forEach {
            Text(it, (Modifier.padding(top = 5.dp)).uiId("cart.0032"), color = LoteriasCaixaDS.colors.text)
        }
        if (onDetail != null) {
            Row(
                Modifier.align(Alignment.End).uiId("cart.0033").clickable(onClick = onDetail).padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_ver_detalhes_4a4ed0),
                    color = LoteriasCaixaDS.colors.brand,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("cart.0034")
                )
                Icon(LoteriasCaixaDSIcons.Forward, null, (Modifier.size(18.dp)).uiId("cart.0035"), tint = LoteriasCaixaDS.colors.brand)
            }
        }
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(LoteriasCaixaDS.colors.line))
}
