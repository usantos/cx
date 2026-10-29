package br.gov.caixa.loterias.core.designsystem.artwork

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryKey
import br.gov.caixa.loterias.core.designsystem.R

/** Geometria inalterada das camadas selecionadas pelo EstiloModalidadeMKP nativo para concursos normais. */
@Composable
fun caixaDSMarketplaceTrapeze(modalityId: Int, modalityName: String = ""): Int? =
    resolveCaixaDSMarketplaceTrapeze(modalityId, modalityName, LocalCaixaDSMegaSenaCampaign.current)

fun resolveCaixaDSMarketplaceTrapeze(modalityId: Int, modalityName: String = "", campaign: CaixaDSCampaign = CaixaDSCampaign.DEFAULT): Int? {
    val key = caixaDSLotteryKey(modalityId, modalityName)
    if (key == "MEGASENA" && campaign != CaixaDSCampaign.DEFAULT) return null
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
