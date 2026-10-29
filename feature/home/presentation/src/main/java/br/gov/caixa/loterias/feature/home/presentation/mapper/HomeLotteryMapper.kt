package br.gov.caixa.loterias.feature.home.presentation.mapper

import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import java.math.BigDecimal
import java.text.Normalizer
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.home.R

private data class HomeLotteryStyle(
    val color: Color,
    val darkColor: Color,
    @DrawableRes val cloverRes: Int,
    val headerContentColor: Color = CaixaDSColorTokens.White,
    @DrawableRes val calendarRes: Int = R.drawable.icon_data_megasena,
    @DrawableRes val clockRes: Int = R.drawable.icon_horario_megasena,
    val listText: Color = color,
    val darkContent: Color = CaixaDSColorTokens.White,
)

private val homeLotteryStyles = mapOf(
    "MEGASENA" to HomeLotteryStyle(CaixaDSColorTokens.MegaSenaLight, CaixaDSColorTokens.MegaSenaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_megasena_fundo_escuro),
    "LOTOFACIL" to HomeLotteryStyle(CaixaDSColorTokens.LotofacilLight, CaixaDSColorTokens.LotofacilDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_lotofacil_fundo_escuro),
    "QUINA" to HomeLotteryStyle(CaixaDSColorTokens.QuinaLight, CaixaDSColorTokens.QuinaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_quina_fundo_escuro),
    "LOTOMANIA" to HomeLotteryStyle(CaixaDSColorTokens.LotomaniaLight, CaixaDSColorTokens.LotomaniaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_lotomania_fundo_escuro, calendarRes = R.drawable.icon_data_lotomania, clockRes = R.drawable.icon_horario_lotomania),
    "TIMEMANIA" to HomeLotteryStyle(CaixaDSColorTokens.TimemaniaLight, CaixaDSColorTokens.TimemaniaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_timemania_fundo_escuro, headerContentColor = CaixaDSColorTokens.TimemaniaContent, calendarRes = R.drawable.icon_data_timemania, clockRes = R.drawable.icon_horario_timemania, listText = CaixaDSColorTokens.TimemaniaContent, darkContent = CaixaDSColorTokens.TimemaniaContent),
    "DUPLASENA" to HomeLotteryStyle(CaixaDSColorTokens.DuplaSenaLight, CaixaDSColorTokens.DuplaSenaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_dupla_sena_fundo_escuro, calendarRes = R.drawable.icon_data_duplasena, clockRes = R.drawable.icon_horario_duplasena),
    "DIADESORTE" to HomeLotteryStyle(CaixaDSColorTokens.DiaDeSorteLight, CaixaDSColorTokens.DiaDeSorteDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_dia_de_sorte_fundo_escuro, CaixaDSColorTokens.DiaDeSorteContent, listText = CaixaDSColorTokens.DiaDeSorteContent),
    "SUPER7" to HomeLotteryStyle(CaixaDSColorTokens.SuperSeteLight, CaixaDSColorTokens.SuperSeteDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_super_sete_fundo_escuro, CaixaDSColorTokens.SuperSeteContent, listText = CaixaDSColorTokens.SuperSeteContent),
    "MAISMILIONARIA" to HomeLotteryStyle(CaixaDSColorTokens.MaisMilionariaLight, CaixaDSColorTokens.MaisMilionariaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_milionaria_fundo_escuro),
    "LOTECA" to HomeLotteryStyle(CaixaDSColorTokens.Loteca, CaixaDSColorTokens.LotecaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_loteca_fundo_escuro, calendarRes = R.drawable.icon_data_loteca, clockRes = R.drawable.icon_horario_loteca),
    // EstiloModalidadeMKP não substitui Lotogol: herda o estilo base da Mega-Sena.
    "LOTOGOL" to HomeLotteryStyle(CaixaDSColorTokens.MegaSenaLight, CaixaDSColorTokens.MegaSenaDark, R.drawable.legacy_home_trevo_megasena_fundo_escuro),
    "INSTANTANEA" to HomeLotteryStyle(CaixaDSColorTokens.InstantaneaLight, CaixaDSColorTokens.InstantaneaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevos_instantanea_fundo_escuro),
    "COMBO" to HomeLotteryStyle(CaixaDSColorTokens.ComboLight, CaixaDSColorTokens.ComboDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevos_combo_fundo_escuro),
)


fun BettingParameters?.toHomeLotteries(
    hasCombos: Boolean,
    specialContests: br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests = br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests(),
    poolSalesEnabled: Boolean = false,
    resaleOffers: List<br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability> = emptyList(),
): List<HomeLottery> {
    val games = this?.games.orEmpty().mapNotNull { game ->
        val canonical = canonicalModality(game.modality.code.ifBlank { game.modality.name })
        val style = homeLotteryStyles[canonical] ?: return@mapNotNull null
        val lottery = game.toHomeLottery(canonical, style)
        val specialOverride = homeSpecialContestOverride(canonical, game.contestType, game.contestNumber, specialContests)
        val pool = if (poolSalesEnabled) this?.availablePoolModalities?.firstOrNull {
            it.modalityId == game.modality.id && it.contestNumber == game.contestNumber &&
                it.contestType.equals(game.contestType, true)
        } else null
        lottery.copy(specialBanner = specialOverride,
            poolResaleClosingDateTime = pool?.let { "${it.resaleClosingDate} ${it.resaleClosingTime}" }.orEmpty())
    }.toMutableList()
    resaleOffers.forEach { pool ->
        val number = pool.contestNumber ?: return@forEach
        val canonical = canonicalModality(pool.modalityCode.ifBlank { pool.modality?.code.orEmpty() })
        val style = homeLotteryStyles[canonical] ?: return@forEach
        if (games.any { it.resultCode == homeResultCode(canonical) && it.contestType.equals(pool.contestType, true) && it.contestNumber == number }) return@forEach
        val index = games.indexOfFirst { it.resultCode == homeResultCode(canonical) && it.contestType.equals(pool.contestType, true) }
        val title = pool.modality?.let {
            if (pool.contestType.equals("ESPECIAL", true) && it.specialName.isNotBlank()) it.specialName else it.name
        }.orEmpty().ifBlank { pool.modalityCode }
        val temporary = HomeLottery(
            modalityId = pool.modalityId, contestType = pool.contestType, resultCode = homeResultCode(canonical),
            name = title, contestNumber = number, estimatedPrize = pool.estimatedPrize ?: BigDecimal.ZERO,
            minimumBetAmount = BigDecimal.ZERO, drawDate = pool.drawDateTime,
            // EncerramentoUtil.criarCardTemporario marca as vendas individuais como já encerradas com este instante.
            closingDateTime = "01/01/2025 12:00:00", open = false, kind = HomeLotteryKind.LOTTERY,
            color = style.color, darkColor = style.darkColor, headerContentColor = style.headerContentColor,
            cloverRes = style.cloverRes, calendarRes = style.calendarRes, clockRes = style.clockRes,
            listText = style.listText, darkContent = style.darkContent,
            specialBanner = homeSpecialContestOverride(canonical, pool.contestType, number, specialContests),
            poolResaleClosingDateTime = "${pool.resaleClosingDate} ${pool.resaleClosingTime}",
        )
        when {
            index >= 0 -> games[index] = temporary
            pool.contestType.equals("NORMAL", true) -> games.add(temporary)
            else -> games.add(0, temporary)
        }
    }
    if (!hasCombos || games.any { it.kind == HomeLotteryKind.COMBO }) return games
    val style = homeLotteryStyles.getValue("COMBO")
    return games + HomeLottery(
        modalityId = 0,
        contestType = "NORMAL",
        resultCode = "",
        name = "Combo",
        contestNumber = 0,
        estimatedPrize = BigDecimal.ZERO,
        minimumBetAmount = BigDecimal.ZERO,
        drawDate = "",
        closingDateTime = "",
        open = true,
        kind = HomeLotteryKind.COMBO,
        color = style.color,
        darkColor = style.darkColor,
        headerContentColor = style.headerContentColor,
        cloverRes = style.cloverRes,
        calendarRes = style.calendarRes,
        clockRes = style.clockRes,
    )
}

private fun LotteryGameParameters.toHomeLottery(
    canonical: String,
    defaultStyle: HomeLotteryStyle,
): HomeLottery {
    return HomeLottery(
    modalityId = modality.id,
    contestType = contestType,
    resultCode = homeResultCode(canonical),
    name = if (contestType.equals("ESPECIAL", true) && modality.specialName.isNotBlank()) modality.specialName else modality.name,
    contestNumber = contestNumber,
    estimatedPrize = estimatedPrize,
    minimumBetAmount = prices.minOfOrNull { it.amount } ?: BigDecimal.ZERO,
    drawDate = drawDate,
    closingDateTime = closingDateTime,
    open = open,
    kind = when (canonical) {
        "INSTANTANEA" -> HomeLotteryKind.INSTANT
        "COMBO" -> HomeLotteryKind.COMBO
        else -> HomeLotteryKind.LOTTERY
    },
    // A campanha ativa da Mega-Sena é aplicada depois por withHomeMegaSenaCampaign.
    color = defaultStyle.color,
    darkColor = defaultStyle.darkColor,
    headerContentColor = defaultStyle.headerContentColor,
    cloverRes = defaultStyle.cloverRes,
    calendarRes = defaultStyle.calendarRes,
    clockRes = defaultStyle.clockRes,
    octoberRoseTheme = false,
    listText = defaultStyle.listText,
    darkContent = defaultStyle.darkContent,
    )
}

fun shouldShowPoolButton(
    poolSalesEnabled: Boolean,
    parameters: BettingParameters?,
    lottery: HomeLottery,
): Boolean = poolSalesEnabled && lottery.kind == HomeLotteryKind.LOTTERY &&
    parameters?.availablePoolModalities.orEmpty().any { available ->
        available.modalityId == lottery.modalityId ||
            canonicalModality(available.modalityCode) == canonicalModality(lottery.resultCode)
    }

internal fun canonicalModality(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFD)
    .replace(Regex("\\p{Mn}+"), "")
    .filter(Char::isLetterOrDigit)
    .uppercase()


fun List<HomeLottery>.withHomeMegaSenaCampaign(
    megaCampaign: br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign,
): List<HomeLottery> =
    map { lottery ->
        if (lottery.resultCode == "megasena" && megaCampaign != br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign.DEFAULT) {
            val style = br.gov.caixa.loterias.core.designsystem.tokens.resolveCaixaDSLotteryStyle(2, "", megaCampaign)
            lottery.copy(color = style.light, darkColor = style.dark, headerContentColor = style.content,
                listText = style.listText, darkContent = style.darkContent,
                cloverRes = br.gov.caixa.loterias.core.designsystem.artwork.resolveCaixaDSCampaignDrawable(2, "",
                    br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryArtwork.CLOVER_ON_DARK, megaCampaign) ?: lottery.cloverRes,
                octoberRoseTheme = megaCampaign == br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign.OCTOBER_ROSE)
        } else lottery
    }

private fun homeResultCode(canonical: String): String = when (canonical) {
    "SUPER7" -> "supersete"
    else -> canonical.lowercase()
}

private fun homeSpecialContestOverride(
    canonical: String, type: String, number: Int,
    configuration: br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests,
): br.gov.caixa.loterias.feature.home.presentation.state.HomeSpecialBanner? =
    if (!type.equals("ESPECIAL", true)) null else when {
        canonical == "MEGASENA" && configuration.megaThirtyYearsContest == number ->
            br.gov.caixa.loterias.feature.home.presentation.state.HomeSpecialBanner.MEGA_THIRTY_YEARS
        canonical == "LOTECA" && configuration.lotecaFathersDayContest == number ->
            br.gov.caixa.loterias.feature.home.presentation.state.HomeSpecialBanner.LOTECA_FATHERS_DAY
        else -> null
    }
