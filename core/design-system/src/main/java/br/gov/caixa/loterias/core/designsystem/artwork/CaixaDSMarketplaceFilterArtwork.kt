package br.gov.caixa.loterias.core.designsystem.artwork

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryKey
import br.gov.caixa.loterias.core.designsystem.R

/** Recursos exatos de concursos normais usados nas opções de trevo claro e escuro do ListaModalidadesAdapter. */
@Composable
fun caixaDSMarketplaceFilterClover(modalityId: Int, modalityName: String = "", selected: Boolean): Int? =
    resolveCaixaDSMarketplaceFilterClover(modalityId, modalityName, selected, LocalCaixaDSMegaSenaCampaign.current)

fun resolveCaixaDSMarketplaceFilterClover(modalityId: Int, modalityName: String = "", selected: Boolean, campaign: CaixaDSCampaign = CaixaDSCampaign.DEFAULT): Int? {
    val key = caixaDSLotteryKey(modalityId, modalityName)
    if (key == "MEGASENA" && campaign != CaixaDSCampaign.DEFAULT) return null
    return when (key) {
        "MEGASENA" -> if (selected) R.drawable.legacy_marketplace_filter_trevo_megasena_fundo_escuro else R.drawable.legacy_marketplace_filter_trevo_megasena_fundo_branco
        "QUINA" -> if (selected) R.drawable.legacy_marketplace_filter_trevo_quina_fundo_escuro else R.drawable.legacy_marketplace_filter_trevo_quina_fundo_branco
        "LOTOFACIL" -> if (selected) R.drawable.legacy_marketplace_filter_trevo_lotofacil_fundo_escuro else R.drawable.legacy_marketplace_filter_trevo_lotofacil_fundo_branco
        "MAISMILIONARIA" -> if (selected) R.drawable.legacy_marketplace_filter_trevo_milionaria_fundo_escuro else R.drawable.legacy_marketplace_filter_trevo_milionaria_fundo_branco
        "DUPLASENA" -> if (selected) R.drawable.legacy_marketplace_filter_trevo_dupla_sena_fundo_escuro else R.drawable.legacy_marketplace_filter_trevo_dupla_sena_fundo_branco
        "LOTOMANIA" -> if (selected) R.drawable.legacy_marketplace_filter_trevo_lotomania_fundo_escuro else R.drawable.legacy_marketplace_filter_trevo_lotomania_fundo_branco
        "SUPERSETE" -> if (selected) R.drawable.legacy_marketplace_filter_trevo_super_sete_fundo_escuro else R.drawable.legacy_marketplace_filter_trevo_super_sete_fundo_branco
        "LOTECA" -> if (selected) R.drawable.legacy_marketplace_filter_trevo_loteca_fundo_escuro else R.drawable.legacy_marketplace_filter_trevo_loteca_fundo_branco
        "TIMEMANIA" -> if (selected) R.drawable.legacy_marketplace_filter_trevo_timemania_fundo_escuro else R.drawable.legacy_marketplace_filter_trevo_timemania_fundo_branco
        "DIADESORTE" -> if (selected) R.drawable.legacy_marketplace_filter_trevo_dia_de_sorte_fundo_escuro else R.drawable.legacy_marketplace_filter_trevo_dia_de_sorte_fundo_branco
        else -> null
    }
}
