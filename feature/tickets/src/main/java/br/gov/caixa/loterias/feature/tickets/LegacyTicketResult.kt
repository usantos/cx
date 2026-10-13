package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import br.gov.caixa.loterias.core.designsystem.*
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/** Display inputs mapped from the ticket query contract and session betting parameters. */
data class TicketResultVisualData(
    val modalityId: Int? = null,
    val modalityName: String = "",
    val ticketStatusId: Int? = null,
    val netPrize: BigDecimal? = null,
    val awardRows: List<TicketResultAwardRow> = emptyList(),
    val currentEstimate: String? = null,
    val currentClosing: String? = null,
)
data class TicketResultAwardRow(val contest: String, val status: String, val award: String,
    val quantity: String, val draw: String, val value: String)

fun ticketResultVisualData(
    result: br.gov.caixa.loterias.core.domain.tickets.CheckedTicketResult,
    currentGame: br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters? = null,
): TicketResultVisualData {
    val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    return TicketResultVisualData(
        modalityId = result.modalityId, modalityName = result.modalityName, ticketStatusId = result.statusId, netPrize = result.netPrize,
        awardRows = result.awards.map { row -> TicketResultAwardRow(
            row.contest?.toString() ?: "-", row.status, row.name.ifBlank { "-" },
            row.hitQuantity?.toString() ?: "-", row.draw?.toString() ?: "-",
            row.value?.let(currency::format) ?: "-",
        ) },
        currentEstimate = currentGame?.takeIf { it.hasNextContest }?.estimatedPrize?.let(currency::format),
        currentClosing = currentGame?.takeIf { it.hasNextContest }?.closingDateTime?.takeIf(String::isNotBlank)?.let(::ticketClosingLabel),
    )
}

private fun ticketClosingLabel(value: String): String {
    // Legacy ViewUtils.getDateAndHour: dd/MM/yyyy HH:mm:ss -> dd/MM | HHhmm.
    val match = Regex("""(\d{2}/\d{2})/\d{4} (\d{2}):(\d{2}).*""").matchEntire(value)
    return match?.let { "${it.groupValues[1]} | ${it.groupValues[2]}h${it.groupValues[3]}" } ?: value
}

private fun resultText(size: androidx.compose.ui.unit.TextUnit, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSFontFamily, fontSize = size, fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true),
)

@Composable
internal fun LegacyTicketResultContent(bets: List<ConfirmedBet>, visual: TicketResultVisualData?,
    onBetNow: (() -> Unit)?, onSave: () -> Unit) {
    val bet = bets.firstOrNull()
    val modalityId = visual?.modalityId ?: bet?.modalityId ?: 2
    val modalityName = visual?.modalityName?.takeIf(String::isNotBlank) ?: bet?.modality.orEmpty()
    val style = caixaDSLotteryStyle(modalityId, modalityName)
    val total = visual?.netPrize ?: bets.fold(BigDecimal.ZERO) { amount, entry -> amount + entry.prize }
    val isAwarded = visual?.ticketStatusId?.let { it == 100 || it == 103 } ?: (total.signum() > 0)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        if (isAwarded || visual?.awardRows?.isNotEmpty() == true) {
            Row(Modifier.fillMaxWidth().height(56.dp).background(style.light), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(90.dp).height(56.dp)) {
                    Image(rememberResultDrawablePainter(resultTrapezoid(modalityId)), null, Modifier.fillMaxSize())
                    Image(caixaDSLegacyLotteryClover(modalityId), null,
                        Modifier.align(Alignment.CenterStart).padding(start = 16.dp).size(22.dp))
                }
                Text(modalityName.lowercase(Locale.forLanguageTag("pt-BR")), color = style.content,
                    style = resultText(16.sp, FontWeight.Bold).copy(fontFamily = CaixaDSLegacyFuturaFontFamily))
            }
            Column(Modifier.fillMaxWidth().background(style.dark), horizontalAlignment = Alignment.CenterHorizontally) {
                if (isAwarded) {
                    Text("Pode comemorar!", Modifier.fillMaxWidth().padding(top = 15.dp, bottom = 10.dp),
                        color = style.content, style = resultText(14.sp), textAlign = TextAlign.Center)
                    Text(if (total.signum() > 0) "você foi premiado com total de\n ${NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(total)}" else "você foi premiado!",
                        Modifier.fillMaxWidth(), color = style.content, style = resultText(20.1.sp, FontWeight.Bold), textAlign = TextAlign.Center)
                }
                if (visual?.awardRows?.isNotEmpty() == true) {
                    Column(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 10.dp)) {
                        AwardTableRow(listOf("Concurso", "Situação", "Faixa", "Qtde", "Sorteio", "Valor"), style.content, true)
                        visual.awardRows.forEach { row -> AwardTableRow(listOf(row.contest, row.status, row.award, row.quantity, row.draw, row.value), style.content, false) }
                    }
                }
            }
            if (visual?.currentEstimate != null || visual?.currentClosing != null) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Informações do concurso atual", color = CaixaDSColorTokens.LegacyBetAction, style = resultText(14.2.sp, FontWeight.Bold))
                    visual.currentEstimate?.let {
                        Text("Estimativa de prêmio", Modifier.padding(top = 10.dp), color = CaixaDSColorTokens.LegacyBetAction, style = resultText(14.sp, FontWeight.Bold))
                        Text(it, Modifier.padding(top = 5.dp), color = CaixaDSColorTokens.LegacyBetAction, style = resultText(14.sp))
                    }
                    visual.currentClosing?.let {
                        Text("Encerramento das apostas", Modifier.padding(top = 10.dp), color = CaixaDSColorTokens.LegacyBetAction, style = resultText(14.2.sp, FontWeight.Bold))
                        Text(it, Modifier.padding(top = 5.dp), color = CaixaDSColorTokens.LegacyBetAction, style = resultText(14.2.sp))
                    }
                }
            }
        } else {
            val label = when (visual?.ticketStatusId) {
                10 -> "Concurso prescrito"
                11 -> "O prêmio deste bilhete já foi pago. Parabéns :)"
                9, 101 -> "Não premiado ou ainda não apurado"
                else -> bet?.status.orEmpty()
            }
            Box(Modifier.fillMaxWidth().height(111.dp).background(style.dark), contentAlignment = Alignment.Center) {
                Text(label, Modifier.fillMaxWidth(), color = CaixaDSColorTokens.White, style = resultText(20.sp, FontWeight.Bold), textAlign = TextAlign.Center)
            }
        }
        if (onBetNow != null) Box(Modifier.padding(top = 17.dp).width(160.dp).height(61.dp)
            .align(Alignment.CenterHorizontally).background(CaixaDSColorTokens.LegacyPrizeBetNow).clickable(onClick = onBetNow),
            contentAlignment = Alignment.Center) {
            Text("Aposte\nagora", color = CaixaDSColorTokens.White, style = resultText(14.2.sp, FontWeight.Bold), textAlign = TextAlign.Center)
        }
        // Preserve the existing Compose save action until navigation exposes the legacy save-before-result contract.
        Box(Modifier.padding(top = 24.dp, bottom = 16.dp).width(184.dp).height(84.dp).align(Alignment.CenterHorizontally)
            .background(CaixaDSColorTokens.LegacyTealAction).clickable(onClick = onSave), contentAlignment = Alignment.Center) {
            Text("Salvar", color = CaixaDSColorTokens.White, style = resultText(17.7.sp, FontWeight.Bold))
        }
    }
}

@Composable
private fun AwardTableRow(values: List<String>, color: Color, header: Boolean) {
    val weights = listOf(2.3f, 2.5f, 1.4f, 1.5f, 1.8f, 2.5f)
    Row(Modifier.fillMaxWidth()) {
        values.forEachIndexed { index, value -> Text(value, Modifier.weight(weights[index]), color = color,
            style = resultText(12.sp, if (header) FontWeight.Bold else FontWeight.Normal), textAlign = TextAlign.Center) }
    }
}

/** Render the original Android LayerDrawable, including its exact negative insets and20deg rotation. */
@Composable
private fun rememberResultDrawablePainter(resource: Int): Painter {
    val context = LocalContext.current
    return remember(context, resource) {
        val drawable = requireNotNull(ContextCompat.getDrawable(context, resource))
        object : Painter() {
            override val intrinsicSize: Size = Size.Unspecified
            override fun DrawScope.onDraw() {
                drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())
                drawIntoCanvas { drawable.draw(it.nativeCanvas) }
            }
        }
    }
}

private fun resultTrapezoid(id: Int): Int = when (id) {
    3 -> R.drawable.legacy_result_background_trapeze_quina_mkp
    7 -> R.drawable.legacy_result_background_trapeze_super_sete_mkp
    8 -> R.drawable.legacy_result_background_trapeze_lotofacil_mkp
    9 -> R.drawable.legacy_result_background_trapeze_milionaria_mkp
    11 -> R.drawable.legacy_result_background_trapeze_dia_sorte_mkp
    16 -> R.drawable.legacy_result_background_trapeze_lotomanial_mkp
    18 -> R.drawable.legacy_result_background_trapeze_dupla_sena_mkp
    19 -> R.drawable.legacy_result_background_trapeze_loteca_mkp
    20 -> R.drawable.legacy_result_background_trapeze_timemania_mkp
    else -> R.drawable.legacy_result_background_trapeze_megasena_mkp
}
