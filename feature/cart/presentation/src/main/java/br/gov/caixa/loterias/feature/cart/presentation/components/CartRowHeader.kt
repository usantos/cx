package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.presentation.mapper.formatCartAmount

@Composable
internal fun CartRowHeader(item: CartItem, expired: Boolean, modalityColor: Color, onDelete: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
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
                    color = if (expired) LoteriasCaixaDSColorTokens.ReservationExpired else LoteriasCaixaDS.colors.mutedText,
                    fontSize = 12.sp,
                    modifier = Modifier.uiId("cart.0017")
                )
            }
        }
        Text(
            formatCartAmount(item.amount),
            color = if (expired) LoteriasCaixaDSColorTokens.ReservationExpired else LoteriasCaixaDS.colors.text,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.uiId("cart.0018")
        )
        if (onDelete != null) {
            IconButton(onClick = onDelete, modifier = (Modifier.size(32.dp)).uiId("cart.0019")) {
                Icon(
                    LoteriasCaixaDSIcons.Delete,
                    uiStrings.getString(LocalizedR.string.ui_excluir_8b1951),
                    (Modifier.size(20.dp)).uiId("cart.0020"),
                    tint = LoteriasCaixaDS.colors.error
                )
            }
        }
    }
}
