package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as DsR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSHorizontalDivider as HorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.ui.R as DeliveryR

@Composable
internal fun RetailerResultRow(
    retailer: br.gov.caixa.loterias.feature.retail.domain.model.Retailer,
    onToggle: (br.gov.caixa.loterias.feature.retail.domain.model.Retailer) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().uiId("retail.0044").clickable { onToggle(retailer) }) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val code = retailer.code.let {
                if (it.length >=
                    RETAILER_DISPLAY_CODE_END
                ) {
                    it.substring(RETAILER_DISPLAY_CODE_START, RETAILER_DISPLAY_CODE_END)
                } else {
                    it
                }
            }
            Text(
                listOf(
                    code,
                    retailer.fantasyName.trim()
                ).filter(String::isNotBlank).joinToString(" - ").uppercase(),
                (Modifier.weight(1f)).uiId("retail.0045"),
                color = LoteriasCaixaDSColorTokens.RetailSearchText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    fontFamily = LoteriasCaixaDSFuturaFontFamily,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                )
            )
            Icon(
                painterResource(
                    if (retailer.favorite) {
                        DsR.drawable.legacy_ic_orange_heart_filled
                    } else {
                        DsR.drawable.legacy_ic_orange_heart
                    }
                ),
                if (retailer.favorite) {
                    uiStrings.getString(DeliveryR.string.ui_remover_dos_favoritos_903370)
                } else {
                    uiStrings.getString(DeliveryR.string.ui_adicionar_aos_favoritos_5eb5af)
                },
                (Modifier.size(24.dp, 20.dp)).uiId("retail.0046"),
                tint = androidx.compose.ui.graphics.Color.Unspecified
            )
        }
        HorizontalDivider((Modifier.padding(horizontal = 24.dp)).uiId("retail.0047"), thickness = 2.dp, color = LoteriasCaixaDS.colors.line)
    }
}

private const val RETAILER_DISPLAY_CODE_START = 3
private const val RETAILER_DISPLAY_CODE_END = 9
