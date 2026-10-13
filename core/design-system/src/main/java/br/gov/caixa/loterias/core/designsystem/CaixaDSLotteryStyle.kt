package br.gov.caixa.loterias.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

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
)

fun caixaDSLotteryStyle(modalityId: Int, modalityName: String = ""): CaixaDSLotteryStyle {
    val normalized = modalityName.uppercase().replace('-', '_').replace(' ', '_')
    return when {
        normalized == "COMBO" -> style(CaixaDSColorTokens.LegacyComboLight, CaixaDSColorTokens.LegacyComboDark)
        // Simulation parameters use Quina=1 and Lotomania=5; result catalogs
        // also expose the historical IDs 3 and 16 for the same modalities.
        modalityId in setOf(1, 3) || normalized == "QUINA" -> style(
            CaixaDSColorTokens.LegacyQuinaLight,
            CaixaDSColorTokens.LegacyQuinaDark,
        )
        modalityId == 7 || normalized in setOf("SUPER_7", "SUPER_SETE") -> style(
            CaixaDSColorTokens.LegacySuperSeteLight,
            CaixaDSColorTokens.LegacySuperSeteDark,
            CaixaDSColorTokens.LegacySuperSeteContent,
        )
        modalityId == 8 || normalized in setOf("LOTOFACIL", "LOTOFÁCIL") -> style(
            CaixaDSColorTokens.LegacyLotofacilLight,
            CaixaDSColorTokens.LegacyLotofacilDark,
        )
        modalityId == 9 || normalized in setOf("MAIS_MILIONARIA", "MAIS_MILIONÁRIA", "+MILIONARIA", "+MILIONÁRIA") -> style(
            CaixaDSColorTokens.LegacyMaisMilionariaLight,
            CaixaDSColorTokens.LegacyMaisMilionariaDark,
        )
        modalityId == 11 || normalized == "DIA_DE_SORTE" -> style(
            CaixaDSColorTokens.LegacyDiaDeSorteLight,
            CaixaDSColorTokens.LegacyDiaDeSorteDark,
            CaixaDSColorTokens.LegacyDiaDeSorteContent,
        )
        modalityId == 14 || normalized == "LOTOGOL" -> style(
            CaixaDSColorTokens.Lotogol,
            CaixaDSColorTokens.LotogolDark,
        )
        modalityId in setOf(5, 16) || normalized == "LOTOMANIA" -> style(
            CaixaDSColorTokens.LegacyLotomaniaLight,
            CaixaDSColorTokens.LegacyLotomaniaDark,
        )
        modalityId == 18 || normalized == "DUPLA_SENA" -> style(
            CaixaDSColorTokens.LegacyDuplaSenaLight,
            CaixaDSColorTokens.LegacyDuplaSenaDark,
        )
        modalityId == 19 || normalized == "LOTECA" -> style(
            CaixaDSColorTokens.Loteca,
            CaixaDSColorTokens.LotecaDark,
        )
        modalityId == 20 || normalized == "TIMEMANIA" -> style(
            CaixaDSColorTokens.LegacyTimemaniaLight,
            CaixaDSColorTokens.LegacyTimemaniaDark,
            CaixaDSColorTokens.LegacyTimemaniaContent,
            darkContent = CaixaDSColorTokens.LegacyTimemaniaContent,
        )
        else -> style(CaixaDSColorTokens.LegacyMegaSenaLight, CaixaDSColorTokens.LegacyMegaSenaDark)
    }
}

private fun style(
    light: Color,
    dark: Color,
    content: Color = CaixaDSColorTokens.White,
    darkContent: Color = CaixaDSColorTokens.White,
) = CaixaDSLotteryStyle(light, dark, content, darkContent)
