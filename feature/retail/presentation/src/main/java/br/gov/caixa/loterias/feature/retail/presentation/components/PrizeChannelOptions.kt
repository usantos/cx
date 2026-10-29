package br.gov.caixa.loterias.feature.retail.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixMark
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPrizeLotteryMark
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPrizeMercadoPagoMark
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSPrizeChevron
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel

@Composable
internal fun PrizeOptionRow(
    option: br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionOption,
    onSelect: (PrizeRedemptionChannel) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 16.dp)
            .border(BorderStroke(2.dp, CaixaDSColorTokens.DrawerDivider), RoundedCornerShape(4.dp))
            .uiId("retail.0001").clickable { onSelect(option.channel) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(44.dp).background(CaixaDSColorTokens.ComboDark, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            when (option.channel) {
                PrizeRedemptionChannel.PIX -> CaixaDSPixMark((Modifier.size(20.dp)).uiId("retail.0002"), CaixaDSColorTokens.White)

                PrizeRedemptionChannel.MERCADO_PAGO -> CaixaDSPrizeMercadoPagoMark((Modifier.size(18.dp, 12.dp)).uiId("retail.0003"))

                PrizeRedemptionChannel.LOTTERY -> CaixaDSPrizeLotteryMark((Modifier.size(20.dp)).uiId("retail.0004"))

                PrizeRedemptionChannel.AGENCY -> Icon(
                    imageVector = CaixaDSIcons.Caixa,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.Unspecified,
                    modifier = (Modifier.size(20.dp)).uiId("retail.0005")
                )
            }
        }
        PrizeOptionText(option.title, option.description, (Modifier.weight(1f).padding(start = 13.dp)).uiId("retail.0006"))
        CaixaDSPrizeChevron((Modifier.size(12.dp, 18.dp)).uiId("retail.0007"))
    }
}

@Composable
internal fun PrizeOptionText(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            title,
            color = CaixaDSColorTokens.SocialGraphite,
            fontFamily = CaixaDSRegularFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified,
            modifier = Modifier.uiId("retail.0008")
        )
        Text(
            subtitle,
            color = CaixaDSColorTokens.SocialGraphite,
            fontFamily = CaixaDSRegularFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified,
            modifier = Modifier.uiId("retail.0009")
        )
    }
}
