package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplaceDetailMoney
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplaceDetailPrizeInWords
import kotlin.math.roundToInt

@Composable
internal fun detailText(size: Int, weight: FontWeight = FontWeight.Normal): TextStyle {
    val nativeSize = with(LocalDensity.current) { size.sp.toPx().roundToInt().toFloat().toSp() }
    return TextStyle(
        fontFamily = CaixaDSFontFamily,
        fontSize = nativeSize,
        fontWeight = weight,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    )
}

@Composable
internal fun DetailDivider(modifier: Modifier = Modifier) =
    Box(modifier.fillMaxWidth().height(.8.dp).background(CaixaDSColorTokens.PrizeDivider))

@Composable
internal fun DetailEstimatedPrize(detail: MarketplacePoolDetail, colors: MarketplaceColors) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            marketplaceDetailMoney(detail.estimatedPrize),
            style = detailText(24, FontWeight.Bold),
            color = colors.textOnSurface,
            modifier = Modifier.uiId("pool.0028")
        )
        marketplaceDetailPrizeInWords(detail.estimatedPrize)?.let {
            Text(
                uiStrings.getString(LocalizedR.string.ui_1_s_4c75be, it),
                style = detailText(19, FontWeight.Bold),
                color = colors.textOnSurface,
                modifier = Modifier.uiId("pool.0029")
            )
        }
        Text(
            uiStrings.getString(LocalizedR.string.ui_premio_estimado_699ac4),
            style = detailText(18),
            color = CaixaDSColorTokens.TicketChoiceMuted,
            modifier = Modifier.uiId("pool.0030")
        )
    }
}

@Composable
internal fun DetailPoolInformation(detail: MarketplacePoolDetail) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)) { append(detail.betQuantity.toString()) }
            append(
                if (detail.betQuantity == 1) {
                    uiStrings.getString(LocalizedR.string.ui_aposta_de_9a14cf)
                } else {
                    uiStrings.getString(
                        LocalizedR.string.ui_apostas_de_11b8ee
                    )
                }
            )
            withStyle(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)) {
                append(
                    if (detail.modality.contains("MILION", true) &&
                        detail.bets.firstOrNull()?.numbers?.isNotEmpty() == true
                    ) {
                        detail.bets.first().numbers.size.toString()
                    } else {
                        detail.numberQuantity.toString()
                    }
                )
            }
            append(if (detail.modality.contains("LOTECA", true)) " palpites" else " dezenas")
        },
        style = detailText(18).copy(fontSize = 18.sp),
        color = CaixaDSColorTokens.TicketChoiceMuted,
        modifier = Modifier.uiId("pool.0031")
    )
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)) { append(detail.availableShares.toString()) }
            append(
                if (detail.availableShares == 1) {
                    uiStrings.getString(LocalizedR.string.ui_cota_disponivel_de_fae690)
                } else {
                    uiStrings.getString(
                        LocalizedR.string.ui_cotas_disponiveis_de_010d84
                    )
                }
            )
            withStyle(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)) { append(detail.totalShares.toString()) }
        },
        style = detailText(18).copy(fontSize = 18.sp),
        color = CaixaDSColorTokens.TicketChoiceMuted,
        modifier = Modifier.uiId("pool.0032")
    )
    Text(
        uiStrings.getString(LocalizedR.string.ui_valor_da_cota_1_s_89868c, marketplaceDetailMoney(detail.sharePriceWithoutFee)),
        style = detailText(18),
        color = CaixaDSColorTokens.TicketChoiceMuted,
        modifier = Modifier.uiId("pool.0033")
    )
    Text(
        uiStrings.getString(LocalizedR.string.ui_tarifa_de_servico_1_s_1a2f5a, marketplaceDetailMoney(detail.lastShareFee)),
        style = detailText(18),
        color = CaixaDSColorTokens.TicketChoiceMuted,
        modifier = Modifier.uiId("pool.0034")
    )
    Text(
        uiStrings.getString(LocalizedR.string.ui_total_1_s_038bb5, marketplaceDetailMoney(detail.lastSharePriceWithFee)),
        style = detailText(16, FontWeight.Bold),
        color = CaixaDSColorTokens.RetailSearchText,
        modifier = Modifier.uiId("pool.0035")
    )
    Text(
        uiStrings.getString(LocalizedR.string.ui_devido_a_regras_de_arredondamento_o_a7f150),
        (Modifier.padding(end = 80.dp)).uiId("pool.0036"),
        style = detailText(12),
        color = CaixaDSColorTokens.TicketChoiceMuted
    )
}
