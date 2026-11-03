package br.gov.caixa.loterias.feature.payment.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentBrandMark
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPixBrand
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCard as Card
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCardDefaults as CardDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSDeleteIcon
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.ui.R as DeliveryR

@Composable
fun PixPaymentMethodCard(onPix: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 35.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = LoteriasCaixaDS.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LoteriasCaixaDSPixBrand((Modifier.padding(vertical = 16.dp).width(200.dp).height(90.dp)).uiId("pay.0029"))
            Button(
                onClick = onPix,
                modifier = (Modifier.fillMaxWidth().height(52.dp)).uiId("pay.0030"),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LoteriasCaixaDS.colors.pix)
            ) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_gerar_codigo_pix_copia_e_cola_1ee337),
                    color = LoteriasCaixaDSColorTokens.White,
                    modifier = Modifier.uiId("pay.0031")
                )
            }
        }
    }
}

@Composable
fun InlinePaymentCard(card: SavedPaymentCard, onOpen: () -> Unit, onDelete: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().height(58.dp), verticalAlignment = Alignment.CenterVertically) {
        Card(
            Modifier.weight(1f).padding(start = 16.dp, end = 8.dp, bottom = 8.dp).height(50.dp)
                .uiId("pay.0035").clickable(onClick = onOpen),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = LoteriasCaixaDS.colors.surface)
        ) {
            Row(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                LoteriasCaixaDSPaymentBrandMark(card.brand, (Modifier.size(50.dp)).uiId("pay.0036"))
                Spacer(Modifier.weight(1f))
                Text(
                    uiStrings.getString(DeliveryR.string.ui_1_s_2fc810, card.lastDigits),
                    color = LoteriasCaixaDS.colors.text,
                    modifier = Modifier.uiId("pay.0037")
                )
            }
        }
        Icon(
            LoteriasCaixaDSDeleteIcon,
            contentDescription = uiStrings.getString(DeliveryR.string.ui_excluir_cartao_final_1_s_228aca, card.lastDigits),
            modifier = (Modifier.size(42.dp).uiId("pay.0038").clickable(onClick = onDelete).padding(9.dp)).uiId("pay.0039"),
            tint = LoteriasCaixaDS.colors.brand
        )
    }
}

@Composable
fun PaymentDataHeading(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 32.dp)) {
        Text(
            title,
            color = LoteriasCaixaDS.colors.cartDark,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 20.sp,
            modifier = Modifier.uiId("pay.0041")
        )
        Text(
            subtitle,
            (Modifier.padding(start = 16.dp, top = 8.dp)).uiId("pay.0042"),
            color = LoteriasCaixaDS.colors.graphite,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
    }
}
