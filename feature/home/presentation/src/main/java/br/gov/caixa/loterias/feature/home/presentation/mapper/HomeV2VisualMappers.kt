package br.gov.caixa.loterias.feature.home.presentation.mapper

import br.gov.caixa.loterias.feature.home.presentation.formatting.localizedMessage as messageText

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.tokens.resolveCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.R

enum class HomeV2Modality(val resultCode: String) {
    MEGA_SENA("megasena"),
    MAIS_MILIONARIA("maismilionaria"),
    LOTECA("loteca"),
    DIA_DE_SORTE("diadesorte"),
    DUPLA_SENA("duplasena"),
    LOTOMANIA("lotomania"),
    QUINA("quina"),
    TIMEMANIA("timemania"),
    LOTOFACIL("lotofacil"),
    SUPER_7("supersete"),
    OTHER(""),
}
internal val HomeLottery.tipoModalidade: HomeV2Modality
    get() = HomeV2Modality.entries.firstOrNull { it.resultCode == resultCode } ?: HomeV2Modality.OTHER

fun HomeV2Presentation.prize(item: HomeLottery) = HomeV2Formatting.prize(item.estimatedPrize)
fun HomeV2Presentation.date(item: HomeLottery) = HomeV2Formatting.date(item.drawDate)
fun HomeV2Presentation.name(type: HomeV2Modality): String = when (type) {
    HomeV2Modality.MEGA_SENA -> "Mega-Sena"
    HomeV2Modality.MAIS_MILIONARIA -> messageText("milionaria_11be79")
    HomeV2Modality.DIA_DE_SORTE -> messageText("dia_de_sorte_b5c7a5")
    HomeV2Modality.DUPLA_SENA -> "Dupla Sena"
    HomeV2Modality.SUPER_7 -> "Super Sete"
    HomeV2Modality.LOTOFACIL -> messageText("lotofacil_d3ed7c")
    else -> type.name.lowercase().replaceFirstChar { it.titlecase() }
}

@Composable internal fun HomeV2Presentation.colors(type: HomeV2Modality): List<Color> {
    val campaign = LocalCaixaDSMegaSenaCampaign.current
    if (type == HomeV2Modality.MEGA_SENA && campaign != CaixaDSCampaign.DEFAULT) {
        val style = resolveCaixaDSLotteryStyle(2, "", campaign)
        return listOf(style.light, style.dark)
    }
    return when (type) {
        HomeV2Modality.MEGA_SENA -> listOf(CxHomeV2Colors.CFF008852, CxHomeV2Colors.CFF006B43)
        HomeV2Modality.MAIS_MILIONARIA -> listOf(CxHomeV2Colors.CFF6533C7, CxHomeV2Colors.CFF4A28A3)
        HomeV2Modality.LOTECA -> listOf(CxHomeV2Colors.CFFD32E49, CxHomeV2Colors.CFFBF2943)
        HomeV2Modality.DUPLA_SENA -> listOf(CxHomeV2Colors.CFFAF2255, CxHomeV2Colors.CFF94214C)
        HomeV2Modality.DIA_DE_SORTE -> listOf(CxHomeV2Colors.CFFFFC52B, CxHomeV2Colors.CFFF5B816)
        HomeV2Modality.LOTOMANIA -> listOf(CxHomeV2Colors.CFFFF852A, CxHomeV2Colors.CFFF06C12)
        HomeV2Modality.QUINA -> listOf(CxHomeV2Colors.CFF075AC0, CxHomeV2Colors.CFF06489D)
        HomeV2Modality.LOTOFACIL -> listOf(CxHomeV2Colors.CFF8B2387, CxHomeV2Colors.CFF731B70)
        else -> listOf(CxHomeV2Colors.CFF6A35C7, CxHomeV2Colors.CFF49279F)
    }
}

@Composable internal fun HomeV2Presentation.icon(type: HomeV2Modality): Int {
    if (type == HomeV2Modality.MEGA_SENA) {
        caixaDSCampaignDrawable(2, "", CaixaDSLotteryArtwork.CLOVER_ON_LIGHT)?.let { return it }
    }
    return when (type) {
        HomeV2Modality.MEGA_SENA -> R.drawable.trevo_megasena_fundo_branco
        HomeV2Modality.MAIS_MILIONARIA -> R.drawable.trevo_mais_milionaria
        HomeV2Modality.DIA_DE_SORTE -> R.drawable.trevo_dia_de_sorte_fundo_branco
        HomeV2Modality.LOTECA -> R.drawable.trevo_loteca_fundo_escuro
        HomeV2Modality.DUPLA_SENA -> R.drawable.trevo_dupla_sena_fundo_escuro
        HomeV2Modality.LOTOMANIA -> R.drawable.trevo_lotomania
        HomeV2Modality.QUINA -> R.drawable.trevo_quina_fundo_branco
        HomeV2Modality.TIMEMANIA -> R.drawable.trevo_timemania_fundo_escuro
        HomeV2Modality.SUPER_7 -> R.drawable.trevo_super_sete_fundo_branco
        else -> R.drawable.trevo_lotofacil_fundo_branco
    }
}
