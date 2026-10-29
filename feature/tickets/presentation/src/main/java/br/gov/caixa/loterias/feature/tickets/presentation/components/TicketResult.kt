package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.caixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.tickets.R
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/** Entradas de exibição mapeadas do contrato de consulta de bilhete e dos parâmetros de aposta da sessão. */
data class TicketResultVisualData(
    val modalityId: Int? = null,
    val modalityName: String = "",
    val ticketStatusId: Int? = null,
    val netPrize: BigDecimal? = null,
    val awardRows: List<TicketResultAwardRow> = emptyList(),
    val currentEstimate: String? = null,
    val currentClosing: String? = null,
)
data class TicketResultAwardRow(
    val contest: String,
    val status: String,
    val award: String,
    val quantity: String,
    val draw: String,
    val value: String,
)

fun ticketResultVisualData(
    result: br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketResult,
    currentGame: br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters? = null,
): TicketResultVisualData {
    val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    return TicketResultVisualData(
        modalityId = result.modalityId,
        modalityName = result.modalityName,
        ticketStatusId = result.statusId,
        netPrize = result.netPrize,
        awardRows = result.awards.map { row ->
            TicketResultAwardRow(
                row.contest?.toString() ?: "-",
                row.status,
                row.name.ifBlank { "-" },
                row.hitQuantity?.toString() ?: "-",
                row.draw?.toString() ?: "-",
                row.value?.let(currency::format) ?: "-"
            )
        },
        currentEstimate = currentGame?.takeIf { it.hasNextContest }?.estimatedPrize?.let(currency::format),
        currentClosing = currentGame?.takeIf { it.hasNextContest }?.closingDateTime?.takeIf(String::isNotBlank)?.let(::ticketClosingLabel)
    )
}

private fun ticketClosingLabel(value: String): String {
    // ViewUtils.getDateAndHour legado: dd/MM/yyyy HH:mm:ss -> dd/MM | HHhmm.
    val match = Regex("""(\d{2}/\d{2})/\d{4} (\d{2}):(\d{2}).*""").matchEntire(value)
    return match?.let { "${it.groupValues[1]} | ${it.groupValues[2]}h${it.groupValues[3]}" } ?: value
}

private fun resultText(size: androidx.compose.ui.unit.TextUnit, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSFontFamily,
    fontSize = size,
    fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

@Composable
internal fun LegacyTicketResultContent(bets: List<ConfirmedBet>, visual: TicketResultVisualData?, onBetNow: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val bet = bets.firstOrNull()
    val modalityId = visual?.modalityId ?: bet?.modalityId ?: 2
    val modalityName = visual?.modalityName?.takeIf(String::isNotBlank) ?: bet?.modality.orEmpty()
    val style = caixaDSLotteryStyle(modalityId, modalityName)
    val total = visual?.netPrize ?: bets.fold(BigDecimal.ZERO) { amount, entry -> amount + entry.prize }
    val isAwarded = visual?.ticketStatusId?.let { it == 100 || it == 103 } ?: (total.signum() > 0)
    Column(Modifier.fillMaxSize().uiId("tickets.0100").verticalScroll(rememberScrollState())) {
        if (isAwarded || visual?.awardRows?.isNotEmpty() == true) {
            AwardedTicketResult(modalityId, modalityName, style, total, isAwarded, visual)
        } else {
            val label = when (visual?.ticketStatusId) {
                10 -> uiStrings.getString(LocalizedR.string.ui_concurso_prescrito_8021f2)
                11 -> uiStrings.getString(LocalizedR.string.ui_o_premio_deste_bilhete_ja_foi_pago_p_ce473d)
                9, 101 -> uiStrings.getString(LocalizedR.string.ui_nao_premiado_ou_ainda_nao_apurado_d69d0e)
                else -> bet?.status.orEmpty()
            }
            Box(Modifier.fillMaxWidth().height(111.dp).background(style.dark), contentAlignment = Alignment.Center) {
                Text(
                    label,
                    (Modifier.fillMaxWidth()).uiId("tickets.0101"),
                    color = CaixaDSColorTokens.White,
                    style = resultText(20.sp, FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            }
        }
        if (onBetNow != null) {
            Box(
                Modifier.padding(top = 17.dp).width(160.dp).height(61.dp)
                    .align(Alignment.CenterHorizontally).background(CaixaDSColorTokens.PrizeBetNow)
                    .uiId("tickets.0102").clickable(onClick = onBetNow),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_aposte_agora_8c4ff2),
                    color = CaixaDSColorTokens.White,
                    style = resultText(14.2.sp, FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.uiId("tickets.0103")
                )
            }
        }
    }
}

@Composable
private fun TicketAwardTable(rows: List<TicketResultAwardRow>, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 10.dp)) {
        AwardTableRow(
            listOf(
                stringResource(LocalizedR.string.ui_concurso_1997f5),
                uiStrings.getString(LocalizedR.string.ui_situacao_cf7d46),
                stringResource(LocalizedR.string.format_faixa),
                stringResource(LocalizedR.string.format_qtde),
                stringResource(LocalizedR.string.format_sorteio),
                stringResource(LocalizedR.string.format_valor)
            ),
            color,
            true
        )
        rows.forEach { row -> AwardTableRow(listOf(row.contest, row.status, row.award, row.quantity, row.draw, row.value), color, false) }
    }
}

@Composable
private fun AwardTableRow(values: List<String>, color: Color, header: Boolean) {
    val weights = listOf(2.3f, 2.5f, 1.4f, 1.5f, 1.8f, 2.5f)
    val headers = listOf("Concurso", "Situação", "Faixa", "Qtde", "Sorteio", "Valor")
    Row(
        Modifier.fillMaxWidth().then(
            if (header) {
                Modifier
            } else {
                Modifier.semantics(mergeDescendants = true) {
                    contentDescription = headers.zip(values).joinToString("; ") { (label, value) -> "$label: $value" }
                }
            }
        )
    ) {
        values.forEachIndexed { index, value ->
            Text(
                value,
                (Modifier.weight(weights[index])).uiId("tickets.0104"),
                color = color,
                style = resultText(12.sp, if (header) FontWeight.Bold else FontWeight.Normal),
                textAlign = TextAlign.Center
            )
        }
    }
}

/** Renderiza o LayerDrawable Android original, incluindo os recuos negativos exatos e a rotação de 20 graus. */
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

@Composable
private fun resultTrapezoid(id: Int): Int = caixaDSCampaignDrawable(id, role = CaixaDSLotteryArtwork.TRAPEZE) ?: when (id) {
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

@Composable
private fun AwardedTicketResult(
    modalityId: Int,
    modalityName: String,
    style: br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSLotteryStyle,
    total: BigDecimal,
    isAwarded: Boolean,
    visual: TicketResultVisualData?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().height(56.dp).background(style.light), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(90.dp).height(56.dp)) {
            Image(rememberResultDrawablePainter(resultTrapezoid(modalityId)), null, Modifier.fillMaxSize())
            Image(caixaDSLotteryClover(modalityId), null, Modifier.align(Alignment.CenterStart).padding(start = 16.dp).size(22.dp))
        }
        Text(
            modalityName.lowercase(Locale.forLanguageTag("pt-BR")),
            color = style.content,
            style = resultText(16.sp, FontWeight.Bold).copy(fontFamily = CaixaDSFuturaFontFamily),
            modifier = Modifier.uiId("tickets.0105")
        )
    }
    Column(Modifier.fillMaxWidth().background(style.dark), horizontalAlignment = Alignment.CenterHorizontally) {
        if (isAwarded) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_pode_comemorar_f533cc),
                (Modifier.fillMaxWidth().padding(top = 15.dp, bottom = 10.dp)).uiId("tickets.0106"),
                color = style.darkContent,
                style = resultText(14.sp),
                textAlign = TextAlign.Center
            )
            Text(
                if (total.signum() > 0) {
                    uiStrings.getString(
                        LocalizedR.string.ui_voce_foi_premiado_com_total_de_1_s_2bb4e9,
                        formatBrazilianCurrency(total)
                    )
                } else {
                    uiStrings.getString(LocalizedR.string.ui_voce_foi_premiado_9441db)
                },
                (Modifier.fillMaxWidth()).uiId("tickets.0107"),
                color = style.darkContent,
                style = resultText(20.1.sp, FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }
        if (visual?.awardRows?.isNotEmpty() == true) {
            TicketAwardTable(visual.awardRows, style.darkContent)
        }
    }
    CurrentTicketContest(visual)
}

@Composable
private fun CurrentTicketContest(visual: TicketResultVisualData?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (visual?.currentEstimate != null || visual?.currentClosing != null) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_informacoes_do_concurso_atual_ce38ae),
                color = CaixaDSColorTokens.BetAction,
                style = resultText(14.2.sp, FontWeight.Bold),
                modifier = Modifier.uiId("tickets.0108")
            )
            visual.currentEstimate?.let {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_estimativa_de_premio_04eb68),
                    (Modifier.padding(top = 10.dp)).uiId("tickets.0109"),
                    color = CaixaDSColorTokens.BetAction,
                    style = resultText(14.sp, FontWeight.Bold)
                )
                Text(
                    it,
                    (Modifier.padding(top = 5.dp)).uiId("tickets.0110"),
                    color = CaixaDSColorTokens.BetAction,
                    style = resultText(14.sp)
                )
            }
            visual.currentClosing?.let {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_encerramento_das_apostas_6f9fb5),
                    (Modifier.padding(top = 10.dp)).uiId("tickets.0111"),
                    color = CaixaDSColorTokens.BetAction,
                    style = resultText(14.2.sp, FontWeight.Bold)
                )
                Text(
                    it,
                    (Modifier.padding(top = 5.dp)).uiId("tickets.0112"),
                    color = CaixaDSColorTokens.BetAction,
                    style = resultText(14.2.sp)
                )
            }
        }
    }
}
