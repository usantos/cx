package br.gov.caixa.loterias.core.designsystem.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Composable
import java.text.Normalizer
import java.util.Locale
import androidx.compose.ui.graphics.Color
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSMegaSenaCampaign

/**
 * Cores semânticas das modalidades preservadas do tema do aplicativo legado.
 * Centralizar esta resolução evita que cada feature interprete nomes e IDs de forma diferente.
 */
@Immutable
data class CaixaDSLotteryStyle(
    val light: Color,
    val dark: Color,
    val content: Color,
    val darkContent: Color = CaixaDSColorTokens.White,
    val listText: Color = light,
    val numeral: Color = light,
    val textOnSurface: Color = if (content == CaixaDSColorTokens.White) dark else content,

)

@Composable
fun caixaDSLotteryStyle(modalityId: Int, modalityName: String = ""): CaixaDSLotteryStyle =
    resolveCaixaDSLotteryStyle(modalityId, modalityName, LocalCaixaDSMegaSenaCampaign.current)

fun resolveCaixaDSLotteryStyle(
    modalityId: Int, modalityName: String = "", campaign: CaixaDSCampaign = CaixaDSCampaign.DEFAULT,
): CaixaDSLotteryStyle {
    val key = caixaDSLotteryKey(modalityId, modalityName)
    if (key == "MEGASENA" && campaign == CaixaDSCampaign.OCTOBER_ROSE) {
        return CaixaDSLotteryStyle(
            CaixaDSColorTokens.OctoberRoseLight, CaixaDSColorTokens.OctoberRose,
            CaixaDSColorTokens.OctoberRoseDark, CaixaDSColorTokens.White,
            listText = CaixaDSColorTokens.OctoberRose, numeral = CaixaDSColorTokens.OctoberRose,
            textOnSurface = CaixaDSColorTokens.OctoberRoseDark,
        )
    }
    if (key == "MEGASENA" && campaign == CaixaDSCampaign.SEPTEMBER_BLUE) {
        return CaixaDSLotteryStyle(CaixaDSColorTokens.Blue500, CaixaDSColorTokens.Blue900, CaixaDSColorTokens.White)
    }
    val normalized = key.orEmpty()
    return when {
        normalized == "COMBO" -> style(CaixaDSColorTokens.ComboLight, CaixaDSColorTokens.ComboDark)
        // Os parâmetros de simulação usam Quina=1 e Lotomania=5; os catálogos de resultados
        // também expõem os IDs históricos 3 e 16 para as mesmas modalidades.
        normalized == "QUINA" -> style(
            CaixaDSColorTokens.QuinaLight,
            CaixaDSColorTokens.QuinaDark,
        )
        normalized == "SUPERSETE" -> style(
            CaixaDSColorTokens.SuperSeteLight,
            CaixaDSColorTokens.SuperSeteDark,
            CaixaDSColorTokens.SuperSeteContent,
        )
        normalized == "LOTOFACIL" -> style(
            CaixaDSColorTokens.LotofacilLight,
            CaixaDSColorTokens.LotofacilDark,
        )
        normalized == "MAISMILIONARIA" -> style(
            CaixaDSColorTokens.MaisMilionariaLight,
            CaixaDSColorTokens.MaisMilionariaDark,
        )
        normalized == "DIADESORTE" -> style(
            CaixaDSColorTokens.DiaDeSorteLight,
            CaixaDSColorTokens.DiaDeSorteDark,
            CaixaDSColorTokens.DiaDeSorteContent,
        )
        normalized == "LOTOGOL" -> style(
            CaixaDSColorTokens.Lotogol,
            CaixaDSColorTokens.LotogolDark,
        )
        normalized == "LOTOMANIA" -> style(
            CaixaDSColorTokens.LotomaniaLight,
            CaixaDSColorTokens.LotomaniaDark,
        )
        normalized == "DUPLASENA" -> style(
            CaixaDSColorTokens.DuplaSenaLight,
            CaixaDSColorTokens.DuplaSenaDark,
        )
        normalized == "LOTECA" -> style(
            CaixaDSColorTokens.Loteca,
            CaixaDSColorTokens.LotecaDark,
        )
        normalized == "TIMEMANIA" -> style(
            CaixaDSColorTokens.TimemaniaLight,
            CaixaDSColorTokens.TimemaniaDark,
            CaixaDSColorTokens.TimemaniaContent,
            darkContent = CaixaDSColorTokens.TimemaniaContent,
        )
        else -> style(CaixaDSColorTokens.MegaSenaLight, CaixaDSColorTokens.MegaSenaDark)
    }
}

private fun style(
    light: Color,
    dark: Color,
    content: Color = CaixaDSColorTokens.White,
    darkContent: Color = CaixaDSColorTokens.White,
) = CaixaDSLotteryStyle(light, dark, content, darkContent,
    listText = if (content == CaixaDSColorTokens.White) light else content,
    numeral = if (content == CaixaDSColorTokens.SuperSeteContent) content else light,
)

/** Identidade canônica compartilhada por estilos e artes; entradas desconhecidas nunca recebem uma campanha. */
fun caixaDSLotteryKey(modalityId: Int, modalityName: String = ""): String? {
    val name = Normalizer.normalize(modalityName, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "").uppercase(Locale.ROOT).replace(Regex("[^A-Z0-9]"), "")
    val canonical = when (name) {
        "MEGASENA", "MEGADAVIRADA" -> "MEGASENA"
        "SUPER7", "SUPERSETE" -> "SUPERSETE"
        "MILIONARIA", "MAISMILIONARIA" -> "MAISMILIONARIA"
        "QUINA", "LOTOFACIL", "DIADESORTE", "LOTOGOL", "LOTOMANIA", "DUPLASENA", "LOTECA", "TIMEMANIA", "COMBO" -> name
        else -> null
    }
    if (canonical != null) return canonical
    return when (modalityId) {
        1, 3 -> "QUINA"; 2 -> "MEGASENA"; 7 -> "SUPERSETE"; 8 -> "LOTOFACIL"
        9 -> "MAISMILIONARIA"; 11 -> "DIADESORTE"; 14 -> "LOTOGOL"; 5, 16 -> "LOTOMANIA"
        18 -> "DUPLASENA"; 19 -> "LOTECA"; 20 -> "TIMEMANIA"; else -> null
    }
}
