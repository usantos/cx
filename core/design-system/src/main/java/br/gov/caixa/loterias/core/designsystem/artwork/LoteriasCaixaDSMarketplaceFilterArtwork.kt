package br.gov.caixa.loterias.core.designsystem.artwork

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryKey

/** Recursos exatos de concursos normais usados nas opções de trevo claro e escuro do ListaModalidadesAdapter. */
@Composable
fun loteriasCaixaDSMarketplaceFilterClover(modalityId: Int, modalityName: String = "", selected: Boolean): Int? =
    resolveLoteriasCaixaDSMarketplaceFilterClover(modalityId, modalityName, selected, LocalLoteriasCaixaDSMegaSenaCampaign.current)

fun resolveLoteriasCaixaDSMarketplaceFilterClover(
    modalityId: Int,
    modalityName: String = "",
    selected: Boolean,
    campaign: LoteriasCaixaDSCampaign = LoteriasCaixaDSCampaign.DEFAULT,
): Int? {
    val key = loteriasCaixaDSLotteryKey(modalityId, modalityName)
    if (key == "MEGASENA" && campaign != LoteriasCaixaDSCampaign.DEFAULT) return null
    val (onLight, onDark) = marketplaceFilterClovers(key) ?: return null
    return if (selected) onDark else onLight
}

private fun marketplaceFilterClovers(key: String?): Pair<Int, Int>? = when (key) {
    "MEGASENA" ->
        R.drawable.legacy_marketplace_filter_trevo_megasena_fundo_branco to
            R.drawable.legacy_marketplace_filter_trevo_megasena_fundo_escuro

    "QUINA" ->
        R.drawable.legacy_marketplace_filter_trevo_quina_fundo_branco to
            R.drawable.legacy_marketplace_filter_trevo_quina_fundo_escuro

    "LOTOFACIL" ->
        R.drawable.legacy_marketplace_filter_trevo_lotofacil_fundo_branco to
            R.drawable.legacy_marketplace_filter_trevo_lotofacil_fundo_escuro

    "MAISMILIONARIA" ->
        R.drawable.legacy_marketplace_filter_trevo_milionaria_fundo_branco to
            R.drawable.legacy_marketplace_filter_trevo_milionaria_fundo_escuro

    "DUPLASENA" ->
        R.drawable.legacy_marketplace_filter_trevo_dupla_sena_fundo_branco to
            R.drawable.legacy_marketplace_filter_trevo_dupla_sena_fundo_escuro

    "LOTOMANIA" ->
        R.drawable.legacy_marketplace_filter_trevo_lotomania_fundo_branco to
            R.drawable.legacy_marketplace_filter_trevo_lotomania_fundo_escuro

    "SUPERSETE" ->
        R.drawable.legacy_marketplace_filter_trevo_super_sete_fundo_branco to
            R.drawable.legacy_marketplace_filter_trevo_super_sete_fundo_escuro

    "LOTECA" ->
        R.drawable.legacy_marketplace_filter_trevo_loteca_fundo_branco to
            R.drawable.legacy_marketplace_filter_trevo_loteca_fundo_escuro

    "TIMEMANIA" ->
        R.drawable.legacy_marketplace_filter_trevo_timemania_fundo_branco to
            R.drawable.legacy_marketplace_filter_trevo_timemania_fundo_escuro

    "DIADESORTE" ->
        R.drawable.legacy_marketplace_filter_trevo_dia_de_sorte_fundo_branco to
            R.drawable.legacy_marketplace_filter_trevo_dia_de_sorte_fundo_escuro

    else -> null
}
