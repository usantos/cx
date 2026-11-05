package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.layout.Row
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
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.feature.cart.presentation.mapper.PoolExpiryState
import br.gov.caixa.loterias.feature.cart.presentation.mapper.poolExpiry
import br.gov.caixa.loterias.feature.cart.ui.R as DeliveryR
import java.time.Instant
import java.util.Locale

@Composable
internal fun CartRowReservation(item: CartItem, now: Instant, expired: Boolean) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    item.poolShare?.let { share ->
        val expiry = poolExpiry(share.expiresAt, now)
        val timerColor = when (expiry.state) {
            PoolExpiryState.EXPIRED -> LoteriasCaixaDSColorTokens.ReservationExpired
            PoolExpiryState.WARNING -> LoteriasCaixaDSColorTokens.ReservationWarning
            else -> LoteriasCaixaDS.colors.mutedText
        }
        Text(
            uiStrings.getString(DeliveryR.string.ui_bolao_1_s_d4b106, share.code),
            (Modifier.padding(top = 10.dp)).uiId("cart.0025"),
            color = LoteriasCaixaDS.colors.text
        )
        Text(
            uiStrings.getString(DeliveryR.string.ui_02d_02d_9c3cfc).format(Locale.ROOT, share.reservedShare, share.totalShares),
            color = if (expired) LoteriasCaixaDSColorTokens.ReservationExpired else LoteriasCaixaDS.colors.text,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.uiId("cart.0026")
        )
        Text(
            if (expired) {
                uiStrings.getString(
                    DeliveryR.string.ui_cota_expirada_90c16c
                )
            } else {
                uiStrings.getString(DeliveryR.string.ui_cotas_ef3c1d)
            },
            color = if (expired) LoteriasCaixaDSColorTokens.ReservationCancelled else timerColor,
            modifier = Modifier.uiId("cart.0027")
        )
        Row(Modifier.padding(top = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                LoteriasCaixaDSIcons.Time,
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
}
