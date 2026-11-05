package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR
import java.util.Locale

@Composable
fun LegacyNumericPurchaseBetRowDelivered(bet: ConfirmedBet) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val style = loteriasCaixaDSLotteryStyle(bet.modalityId, bet.modality)
    Column(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().background(style.light)) {
            NumericPurchaseLabels(bet, style)
            Row(Modifier.fillMaxWidth().padding(top = 17.dp).padding(horizontal = 13.dp, vertical = 15.dp)) {
                Column(Modifier.weight(BET_METADATA_WEIGHT)) {
                    Text(
                        bet.date,
                        color = style.content,
                        style = purchaseText(PURCHASE_BET_BODY_SIZE),
                        modifier = Modifier.uiId("tickets.0233")
                    )
                    Text(
                        buildAnnotatedString {
                            append("Conc. ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(bet.contest.toString()) }
                        },
                        color = style.content,
                        style = purchaseText(PURCHASE_BET_BODY_SIZE),
                        modifier = Modifier.uiId("tickets.0234")
                    )
                    if (bet.recurringDraws > 0) {
                        Text(
                            uiStrings.getQuantityString(
                                DeliveryR.plurals.format_purchase_recurring_draws,
                                bet.recurringDraws,
                                bet.recurringDraws
                            ),
                            (Modifier.padding(top = 30.dp)).uiId("tickets.0235"),
                            color = style.content,
                            style = purchaseText(RECURRING_LABEL_SIZE)
                        )
                    }
                }
                Column(Modifier.weight(BET_NUMBERS_WEIGHT)) {
                    NumericPurchaseNumbersDelivered(bet.numbers)
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(65.dp).background(style.dark).padding(start = 13.dp, top = 7.dp)) {
            Text(
                bet.status.ifBlank { uiStrings.getString(DeliveryR.string.ui_em_acompanhamento_9afa47) },
                color = style.darkContent,
                style = purchaseText(PURCHASE_BET_BODY_SIZE),
                modifier = Modifier.uiId("tickets.0237")
            )
        }
    }
}

@Composable
private fun NumericPurchaseLabels(bet: ConfirmedBet, style: br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 15.dp)) {
        Text(
            bet.modality.lowercase(Locale.forLanguageTag("pt-BR")),
            (Modifier.weight(BET_HEADING_WEIGHT)).uiId("tickets.0231"),
            color = style.content,
            style = purchaseText(PURCHASE_BET_BODY_SIZE, FontWeight.Bold).copy(fontFamily = LoteriasCaixaDSFuturaFontFamily)
        )
        Text(
            uiStrings.getString(DeliveryR.string.ui_seus_numeros_ac6cc9),
            (Modifier.weight(BET_HEADING_WEIGHT)).uiId("tickets.0232"),
            color = style.content,
            style = purchaseText(NUMBERS_LABEL_SIZE)
        )
    }
}

private const val PURCHASE_BET_BODY_SIZE = 14.2f

private const val RECURRING_LABEL_SIZE = 12.2f

private const val NUMBERS_LABEL_SIZE = 12.1f

private const val BET_METADATA_WEIGHT = .3f

private const val BET_NUMBERS_WEIGHT = .7f

private const val BET_HEADING_WEIGHT = .5f
