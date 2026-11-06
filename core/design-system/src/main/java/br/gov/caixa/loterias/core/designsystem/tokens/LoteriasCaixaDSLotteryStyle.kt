package br.gov.caixa.loterias.core.designsystem.tokens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import java.text.Normalizer
import java.util.Locale

/**
 * Cores semânticas das modalidades preservadas do tema do aplicativo legado.
 * Centralizar esta resolução evita que cada feature interprete nomes e IDs de forma diferente.
 */
@Immutable
data class LoteriasCaixaDSLotteryStyle(
    val light: Color,
    val dark: Color,
    val content: Color,
    val darkContent: Color = LoteriasCaixaDSColorTokens.White,
    val listText: Color = light,
    val numeral: Color = light,
    val textOnSurface: Color = if (content == LoteriasCaixaDSColorTokens.White) dark else content,

)

@Composable
fun loteriasCaixaDSLotteryStyle(modalityId: Int, modalityName: String = ""): LoteriasCaixaDSLotteryStyle =
    resolveLoteriasCaixaDSLotteryStyle(modalityId, modalityName, LocalLoteriasCaixaDSMegaSenaCampaign.current)

fun resolveLoteriasCaixaDSLotteryStyle(
    modalityId: Int,
    modalityName: String = "",
    campaign: LoteriasCaixaDSCampaign = LoteriasCaixaDSCampaign.DEFAULT,
): LoteriasCaixaDSLotteryStyle {
    val key = loteriasCaixaDSLotteryKey(modalityId, modalityName)
    if (key == "MEGASENA") {
        megaSenaCampaignStyle(campaign)?.let { return it }
    }
    return standardLotteryStyle(key.orEmpty())
}

private fun megaSenaCampaignStyle(campaign: LoteriasCaixaDSCampaign): LoteriasCaixaDSLotteryStyle? {
    if (campaign == LoteriasCaixaDSCampaign.OCTOBER_ROSE) {
        return LoteriasCaixaDSLotteryStyle(
            LoteriasCaixaDSColorTokens.OctoberRoseLight,
            LoteriasCaixaDSColorTokens.OctoberRose,
            LoteriasCaixaDSColorTokens.OctoberRoseDark,
            LoteriasCaixaDSColorTokens.White,
            listText = LoteriasCaixaDSColorTokens.OctoberRose,
            numeral = LoteriasCaixaDSColorTokens.OctoberRose,
            textOnSurface = LoteriasCaixaDSColorTokens.OctoberRoseDark
        )
    }
    if (campaign == LoteriasCaixaDSCampaign.SEPTEMBER_BLUE) {
        return LoteriasCaixaDSLotteryStyle(
            LoteriasCaixaDSColorTokens.Blue500,
            LoteriasCaixaDSColorTokens.Blue900,
            LoteriasCaixaDSColorTokens.White
        )
    }
    return null
}

private fun standardLotteryStyle(normalized: String): LoteriasCaixaDSLotteryStyle = when {
    normalized == "COMBO" -> style(LoteriasCaixaDSColorTokens.ComboLight, LoteriasCaixaDSColorTokens.ComboDark)

    // Os parâmetros de simulação usam Quina=1 e Lotomania=5
    // os catálogos de resultados
    // também expõem os IDs históricos 3 e 16 para as mesmas modalidades.
    normalized == "QUINA" -> style(
        LoteriasCaixaDSColorTokens.QuinaLight,
        LoteriasCaixaDSColorTokens.QuinaDark
    )

    normalized == "SUPERSETE" -> style(
        LoteriasCaixaDSColorTokens.SuperSeteLight,
        LoteriasCaixaDSColorTokens.SuperSeteDark,
        LoteriasCaixaDSColorTokens.SuperSeteContent
    )

    normalized == "LOTOFACIL" -> style(
        LoteriasCaixaDSColorTokens.LotofacilLight,
        LoteriasCaixaDSColorTokens.LotofacilDark
    )

    normalized == "MAISMILIONARIA" -> style(
        LoteriasCaixaDSColorTokens.MaisMilionariaLight,
        LoteriasCaixaDSColorTokens.MaisMilionariaDark
    )

    normalized == "DIADESORTE" -> style(
        LoteriasCaixaDSColorTokens.DiaDeSorteLight,
        LoteriasCaixaDSColorTokens.DiaDeSorteDark,
        LoteriasCaixaDSColorTokens.DiaDeSorteContent
    )

    normalized == "LOTOGOL" -> style(
        LoteriasCaixaDSColorTokens.Lotogol,
        LoteriasCaixaDSColorTokens.LotogolDark
    )

    normalized == "LOTOMANIA" -> style(
        LoteriasCaixaDSColorTokens.LotomaniaLight,
        LoteriasCaixaDSColorTokens.LotomaniaDark
    )

    normalized == "DUPLASENA" -> style(
        LoteriasCaixaDSColorTokens.DuplaSenaLight,
        LoteriasCaixaDSColorTokens.DuplaSenaDark
    )

    normalized == "LOTECA" -> style(
        LoteriasCaixaDSColorTokens.Loteca,
        LoteriasCaixaDSColorTokens.LotecaDark
    )

    normalized == "TIMEMANIA" -> style(
        LoteriasCaixaDSColorTokens.TimemaniaLight,
        LoteriasCaixaDSColorTokens.TimemaniaDark,
        LoteriasCaixaDSColorTokens.TimemaniaContent,
        darkContent = LoteriasCaixaDSColorTokens.TimemaniaContent
    )

    else -> style(LoteriasCaixaDSColorTokens.MegaSenaLight, LoteriasCaixaDSColorTokens.MegaSenaDark)
}

private fun style(
    light: Color,
    dark: Color,
    content: Color = LoteriasCaixaDSColorTokens.White,
    darkContent: Color = LoteriasCaixaDSColorTokens.White,
) = LoteriasCaixaDSLotteryStyle(
    light,
    dark,
    content,
    darkContent,
    listText = if (content == LoteriasCaixaDSColorTokens.White) light else content,
    numeral = if (content == LoteriasCaixaDSColorTokens.SuperSeteContent) content else light
)

/** Identidade canônica compartilhada por estilos e artes; entradas desconhecidas nunca recebem uma campanha. */
fun loteriasCaixaDSLotteryKey(modalityId: Int, modalityName: String = ""): String? {
    val name = Normalizer.normalize(modalityName, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "").uppercase(Locale.ROOT).replace(Regex("[^A-Z0-9]"), "")
    val canonical = canonicalLotteryName(name)
    if (canonical != null) return canonical
    return lotteryKeyById(modalityId)
}

private fun canonicalLotteryName(name: String): String? = when (name) {
    "MEGASENA", "MEGADAVIRADA" -> "MEGASENA"
    "SUPER7", "SUPERSETE" -> "SUPERSETE"
    "MILIONARIA", "MAISMILIONARIA" -> "MAISMILIONARIA"
    "QUINA", "LOTOFACIL", "DIADESORTE", "LOTOGOL", "LOTOMANIA", "DUPLASENA", "LOTECA", "TIMEMANIA", "COMBO" -> name
    else -> null
}

private fun lotteryKeyById(modalityId: Int): String? = when (modalityId) {
    1, HISTORICAL_QUINA_ID -> "QUINA"
    2 -> "MEGASENA"
    SUPER_SETE_ID -> "SUPERSETE"
    LOTOFACIL_ID -> "LOTOFACIL"
    MAIS_MILIONARIA_ID -> "MAISMILIONARIA"
    DIA_DE_SORTE_ID -> "DIADESORTE"
    LOTOGOL_ID -> "LOTOGOL"
    LOTOMANIA_ID, HISTORICAL_LOTOMANIA_ID -> "LOTOMANIA"
    DUPLA_SENA_ID -> "DUPLASENA"
    LOTECA_ID -> "LOTECA"
    TIMEMANIA_ID -> "TIMEMANIA"
    else -> null
}

private const val HISTORICAL_QUINA_ID = 3
private const val SUPER_SETE_ID = 7
private const val LOTOFACIL_ID = 8
private const val MAIS_MILIONARIA_ID = 9
private const val DIA_DE_SORTE_ID = 11
private const val LOTOGOL_ID = 14
private const val LOTOMANIA_ID = 5
private const val HISTORICAL_LOTOMANIA_ID = 16
private const val DUPLA_SENA_ID = 18
private const val LOTECA_ID = 19
private const val TIMEMANIA_ID = 20
