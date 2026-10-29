package br.gov.caixa.loterias.feature.home.presentation.mapper

import androidx.compose.runtime.getValue
import androidx.annotation.DrawableRes
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.home.R

@DrawableRes
internal fun homeTrapezoidResource(lottery: HomeLottery): Int = when {
    lottery.kind == HomeLotteryKind.INSTANT -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_instantanea
    lottery.kind == HomeLotteryKind.COMBO -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_combo
    lottery.octoberRoseTheme -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_megasena_outubro_rosa
    else -> when (canonicalModality(lottery.resultCode)) {
        "QUINA" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_quina_mkp
        "SUPER7", "SUPERSETE" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_super_sete_mkp
        "LOTOFACIL" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_lotofacil_mkp
        "MAISMILIONARIA" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_milionaria_mkp
        "DIADESORTE" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_dia_sorte_mkp
        "LOTOMANIA" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_lotomanial_mkp
        "DUPLASENA" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_dupla_sena_mkp
        "LOTECA" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_loteca_mkp
        "TIMEMANIA" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_timemania_mkp
        "LOTOGOL" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_megasena_mkp
        else -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_megasena_mkp
    }
}

/** EstiloModalidadeMKP.getImagemEspecialCarrossel, condicionado ao ramo ESPECIAL de HomeViewHolder. */
@DrawableRes
internal fun homeSpecialBannerResource(lottery: HomeLottery): Int? {
    if (lottery.kind != HomeLotteryKind.LOTTERY || !lottery.contestType.equals("ESPECIAL", true)) return null
    when (lottery.specialBanner) {
        br.gov.caixa.loterias.feature.home.presentation.state.HomeSpecialBanner.MEGA_THIRTY_YEARS ->
            return R.drawable.legacy_home_special_cabecalho_mega_trinta_anos_carrossel
        br.gov.caixa.loterias.feature.home.presentation.state.HomeSpecialBanner.LOTECA_FATHERS_DAY ->
            return R.drawable.legacy_home_special_cabecalho_loteca_pais_carrossel_alta
        null -> Unit
    }
    return when (canonicalModality(lottery.resultCode)) {
        "MEGASENA" -> R.drawable.legacy_home_special_mega_virada_novo
        "QUINA" -> R.drawable.legacy_home_special_quina_sao_joao_novo
        "DUPLASENA" -> R.drawable.legacy_home_special_dupla_pascoa
        "LOTOFACIL" -> R.drawable.legacy_home_special_lotofacil_independencia
        "LOTECA" -> R.drawable.legacy_home_special_loteca_copa
        else -> null
    }
}
