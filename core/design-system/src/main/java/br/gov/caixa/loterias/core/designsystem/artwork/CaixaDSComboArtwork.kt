package br.gov.caixa.loterias.core.designsystem.artwork

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryKey

/** Recursos vetoriais originais de EstiloModalidadeMKP para cartões de combo. */
@Composable
fun caixaDSComboDetailClover(modalityId: Int, modalityName: String): Int =
    caixaDSCampaignDrawable(modalityId, modalityName, CaixaDSLotteryArtwork.COMBO_DETAIL_CLOVER)
        ?: when (caixaDSLotteryKey(modalityId, modalityName)) {
            "MEGASENA" -> R.drawable.legacy_combo_trevo_mega_sena_detalhe_combo
            "LOTOFACIL" -> R.drawable.legacy_combo_trevo_lotofacil_detalhe_combo
            "DUPLASENA" -> R.drawable.legacy_combo_trevo_dupla_sena_detalhe_combo
            "LOTOMANIA" -> R.drawable.legacy_combo_trevo_lotomania_detalhe_combo
            "MAISMILIONARIA" -> R.drawable.legacy_combo_trevo_mais_milionaria_detalhe_combo
            "SUPERSETE" -> R.drawable.legacy_combo_trevo_super_sete_detalhe_combo
            "LOTECA" -> R.drawable.legacy_combo_trevo_loteca_detalhe_combo
            "TIMEMANIA" -> R.drawable.legacy_combo_trevo_timemania_detalhe_combo
            "DIADESORTE" -> R.drawable.legacy_combo_trevo_dia_de_sorte_detalhe_combo
            else -> R.drawable.legacy_combo_trevo_quina_detalhe_combo
        }

@Composable
fun caixaDSComboSpecialStrip(modalityId: Int, modalityName: String): Int? =
    caixaDSCampaignDrawable(modalityId, modalityName, CaixaDSLotteryArtwork.COMBO_STRIP)
        ?: when (caixaDSLotteryKey(modalityId, modalityName)) {
            "MEGASENA" -> R.drawable.legacy_combo_tarja_trevo_mega_especial_combos
            "QUINA" -> R.drawable.legacy_combo_tarja_trevo_quina_especial_combos
            "DUPLASENA" -> R.drawable.legacy_combo_tarja_trevo_dupla_especial_combos
            "LOTOFACIL" -> R.drawable.legacy_combo_tarja_trevo_lotofacil_especial_combos
            else -> null
        }
