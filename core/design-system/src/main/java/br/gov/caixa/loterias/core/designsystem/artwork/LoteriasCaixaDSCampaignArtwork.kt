package br.gov.caixa.loterias.core.designsystem.artwork

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryKey

enum class LoteriasCaixaDSLotteryArtwork {
    TRAPEZE,
    SPECIAL_TRAPEZE,
    CLOVER_ON_LIGHT,
    CLOVER_ON_DARK,
    FILTER_SELECTED_CLOVER,
    COMBO_STRIP,
    COMBO_DETAIL_CLOVER,
    TAG,
    TAG_OUTLINE,
    SPECIAL_BACKGROUND,
    SPECIAL_CAROUSEL,
    SPECIAL_SINGLE,
    SPECIAL_DOUBLE,
    SPECIAL_SQUARE,
    SPECIAL_DETAIL_HEADER,
    SPECIAL_SUBHEADER,
}

/** Retorna um recurso do DS somente quando a campanha legada realmente substitui aquela modalidade e função. */
@Composable
fun loteriasCaixaDSCampaignDrawable(modalityId: Int, modalityName: String = "", role: LoteriasCaixaDSLotteryArtwork): Int? =
    resolveLoteriasCaixaDSCampaignDrawable(modalityId, modalityName, role, LocalLoteriasCaixaDSMegaSenaCampaign.current)

fun resolveLoteriasCaixaDSCampaignDrawable(
    modalityId: Int,
    modalityName: String,
    role: LoteriasCaixaDSLotteryArtwork,
    campaign: LoteriasCaixaDSCampaign,
): Int? {
    if (loteriasCaixaDSLotteryKey(modalityId, modalityName) != "MEGASENA" ||
        campaign != LoteriasCaixaDSCampaign.OCTOBER_ROSE
    ) {
        return null
    }
    return when (role) {
        LoteriasCaixaDSLotteryArtwork.TRAPEZE -> R.drawable.legacy_campaign_background_trapeze_megasena_outubro_rosa
        LoteriasCaixaDSLotteryArtwork.SPECIAL_TRAPEZE -> R.drawable.legacy_campaign_mega_sena_trapezio_outubro_rosa
        LoteriasCaixaDSLotteryArtwork.CLOVER_ON_LIGHT -> R.drawable.legacy_campaign_trevo_megasena_outubro_rosa_fundo_branco
        LoteriasCaixaDSLotteryArtwork.CLOVER_ON_DARK -> R.drawable.legacy_campaign_trevo_megasena_outubro_rosa_fundo_escuro
        LoteriasCaixaDSLotteryArtwork.FILTER_SELECTED_CLOVER -> R.drawable.legacy_campaign_trevo_megasena_outubro_rosa_ativado
        LoteriasCaixaDSLotteryArtwork.COMBO_STRIP -> R.drawable.legacy_campaign_tarja_trevo_mega_outubro_rosa_combos
        LoteriasCaixaDSLotteryArtwork.COMBO_DETAIL_CLOVER -> R.drawable.legacy_campaign_trevo_mega_sena_detalhe_combo_outubro_rosa
        LoteriasCaixaDSLotteryArtwork.TAG -> R.drawable.legacy_campaign_ic_tag_bolao_outubro_rosa
        LoteriasCaixaDSLotteryArtwork.TAG_OUTLINE -> R.drawable.legacy_campaign_ic_tag_bolao_rosa_outline
        else -> octoberSpecialArtwork(role)
    }
}

private fun octoberSpecialArtwork(role: LoteriasCaixaDSLotteryArtwork): Int? = when (role) {
    // Estes recursos de concursos especiais permanecem deliberadamente idênticos aos da campanha normal legada.
    LoteriasCaixaDSLotteryArtwork.SPECIAL_BACKGROUND -> R.drawable.legacy_campaign_mega_virada_background

    LoteriasCaixaDSLotteryArtwork.SPECIAL_CAROUSEL -> R.drawable.legacy_campaign_mega_virada_novo

    LoteriasCaixaDSLotteryArtwork.SPECIAL_SINGLE -> R.drawable.legacy_campaign_mega_virada_simples

    LoteriasCaixaDSLotteryArtwork.SPECIAL_DOUBLE -> R.drawable.legacy_campaign_mega_virada_dupla

    LoteriasCaixaDSLotteryArtwork.SPECIAL_SQUARE -> R.drawable.legacy_campaign_mega_virada_quadrado

    LoteriasCaixaDSLotteryArtwork.SPECIAL_DETAIL_HEADER -> R.drawable.legacy_campaign_cabecalho_mega_da_virada_detalhe_aposta

    LoteriasCaixaDSLotteryArtwork.SPECIAL_SUBHEADER -> R.drawable.legacy_campaign_rodape_mega_da_virada_inferior

    else -> null
}

/** Arte selecionada para cartões de modalidade de concursos especiais do ListaModalidadesAdapter. */
fun loteriasCaixaDSLotteryFilterSpecialBackground(modalityId: Int, modalityName: String, contestName: String): Int? {
    val contest = contestName.uppercase()
    return when (loteriasCaixaDSLotteryKey(modalityId, modalityName)) {
        "MEGASENA" -> if ("30" in contest) R.drawable.legacy_campaign_mega_30_quadrado else R.drawable.legacy_campaign_mega_virada_quadrado

        "QUINA" -> R.drawable.legacy_campaign_quina_sao_joao_quadrado

        "LOTOFACIL" -> R.drawable.legacy_campaign_lotofacil_independencia_quadrado

        "DUPLASENA" -> R.drawable.legacy_campaign_dupla_pascoa_quadrado

        "LOTECA" -> if ("PAIS" in contest ||
            "PAÍS" in contest
        ) {
            R.drawable.legacy_campaign_loteca_pais_quadrado_alta
        } else {
            R.drawable.legacy_campaign_loteca_copa_quadrado
        }

        else -> null
    }
}
