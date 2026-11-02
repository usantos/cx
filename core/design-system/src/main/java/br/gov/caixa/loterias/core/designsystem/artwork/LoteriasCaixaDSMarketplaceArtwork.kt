package br.gov.caixa.loterias.core.designsystem.artwork

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryKey

/** Geometria inalterada das camadas selecionadas pelo EstiloModalidadeMKP nativo para concursos normais. */
@Composable
fun loteriasCaixaDSMarketplaceTrapeze(modalityId: Int, modalityName: String = ""): Int? =
    resolveLoteriasCaixaDSMarketplaceTrapeze(modalityId, modalityName, LocalLoteriasCaixaDSMegaSenaCampaign.current)

fun resolveLoteriasCaixaDSMarketplaceTrapeze(
    modalityId: Int,
    modalityName: String = "",
    campaign: LoteriasCaixaDSCampaign = LoteriasCaixaDSCampaign.DEFAULT,
): Int? {
    val key = loteriasCaixaDSLotteryKey(modalityId, modalityName)
    if (key == "MEGASENA" && campaign != LoteriasCaixaDSCampaign.DEFAULT) return null
    return when (key) {
        "MEGASENA" -> R.drawable.legacy_marketplace_trapeze_megasena
        "QUINA" -> R.drawable.legacy_marketplace_trapeze_quina
        "LOTOFACIL" -> R.drawable.legacy_marketplace_trapeze_lotofacil
        "MAISMILIONARIA" -> R.drawable.legacy_marketplace_trapeze_milionaria
        "DUPLASENA" -> R.drawable.legacy_marketplace_trapeze_dupla_sena
        "LOTOMANIA" -> R.drawable.legacy_marketplace_trapeze_lotomanial
        "SUPERSETE" -> R.drawable.legacy_marketplace_trapeze_super_sete
        "LOTECA" -> R.drawable.legacy_marketplace_trapeze_loteca
        "TIMEMANIA" -> R.drawable.legacy_marketplace_trapeze_timemania
        "DIADESORTE" -> R.drawable.legacy_marketplace_trapeze_dia_sorte
        else -> null
    }
}
