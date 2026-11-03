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
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPixMark
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPrizeLotteryMark
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPrizeMercadoPagoMark
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSPrizeChevron
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel

@Composable
internal fun PrizeOptionRow(
    option: br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionOption,
    onSelect: (PrizeRedemptionChannel) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 16.dp)
            .border(BorderStroke(2.dp, LoteriasCaixaDSColorTokens.DrawerDivider), RoundedCornerShape(4.dp))
            .uiId("retail.0001").clickable { onSelect(option.channel) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(44.dp).background(LoteriasCaixaDSColorTokens.ComboDark, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            when (option.channel) {
                PrizeRedemptionChannel.PIX -> LoteriasCaixaDSPixMark(
                    (Modifier.size(20.dp)).uiId("retail.0002"),
                    LoteriasCaixaDSColorTokens.White
                )

                PrizeRedemptionChannel.MERCADO_PAGO -> LoteriasCaixaDSPrizeMercadoPagoMark(
                    (Modifier.size(18.dp, 12.dp)).uiId("retail.0003")
                )

                PrizeRedemptionChannel.LOTTERY -> LoteriasCaixaDSPrizeLotteryMark((Modifier.size(20.dp)).uiId("retail.0004"))

                PrizeRedemptionChannel.AGENCY -> Icon(
                    imageVector = LoteriasCaixaDSIcons.Caixa,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.Unspecified,
                    modifier = (Modifier.size(20.dp)).uiId("retail.0005")
                )
            }
        }
        PrizeOptionText(option.title, option.description, (Modifier.weight(1f).padding(start = 13.dp)).uiId("retail.0006"))
        LoteriasCaixaDSPrizeChevron((Modifier.size(12.dp, 18.dp)).uiId("retail.0007"))
    }
}

@Composable
internal fun PrizeOptionText(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            title,
            color = LoteriasCaixaDSColorTokens.SocialGraphite,
            fontFamily = LoteriasCaixaDSRegularFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified,
            modifier = Modifier.uiId("retail.0008")
        )
        Text(
            subtitle,
            color = LoteriasCaixaDSColorTokens.SocialGraphite,
            fontFamily = LoteriasCaixaDSRegularFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified,
            modifier = Modifier.uiId("retail.0009")
        )
    }
}
