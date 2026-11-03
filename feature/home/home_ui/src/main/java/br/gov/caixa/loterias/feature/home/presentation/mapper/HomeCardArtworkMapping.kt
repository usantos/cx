package br.gov.caixa.loterias.feature.home.presentation.mapper

import androidx.annotation.DrawableRes
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR

@DrawableRes
internal fun homeTrapezoidResource(lottery: HomeLottery): Int = when {
    lottery.kind == HomeLotteryKind.INSTANT -> DeliveryR.drawable.legacy_home_background_trapeze_instantanea

    lottery.kind == HomeLotteryKind.COMBO -> DeliveryR.drawable.legacy_home_background_trapeze_combo

    lottery.octoberRoseTheme -> DeliveryR.drawable.legacy_home_background_trapeze_megasena_outubro_rosa

    else -> when (canonicalModality(lottery.resultCode)) {
        "QUINA" -> DeliveryR.drawable.legacy_home_background_trapeze_quina_mkp
        "SUPER7", "SUPERSETE" -> DeliveryR.drawable.legacy_home_background_trapeze_super_sete_mkp
        "LOTOFACIL" -> DeliveryR.drawable.legacy_home_background_trapeze_lotofacil_mkp
        "MAISMILIONARIA" -> DeliveryR.drawable.legacy_home_background_trapeze_milionaria_mkp
        "DIADESORTE" -> DeliveryR.drawable.legacy_home_background_trapeze_dia_sorte_mkp
        "LOTOMANIA" -> DeliveryR.drawable.legacy_home_background_trapeze_lotomanial_mkp
        "DUPLASENA" -> DeliveryR.drawable.legacy_home_background_trapeze_dupla_sena_mkp
        "LOTECA" -> DeliveryR.drawable.legacy_home_background_trapeze_loteca_mkp
        "TIMEMANIA" -> DeliveryR.drawable.legacy_home_background_trapeze_timemania_mkp
        "LOTOGOL" -> DeliveryR.drawable.legacy_home_background_trapeze_megasena_mkp
        else -> DeliveryR.drawable.legacy_home_background_trapeze_megasena_mkp
    }
}

/** EstiloModalidadeMKP.getImagemEspecialCarrossel, condicionado ao ramo ESPECIAL de HomeViewHolder. */
@DrawableRes
internal fun homeSpecialBannerResource(lottery: HomeLottery): Int? {
    if (lottery.kind != HomeLotteryKind.LOTTERY || !lottery.contestType.equals("ESPECIAL", true)) return null
    when (lottery.specialBanner) {
        br.gov.caixa.loterias.feature.home.presentation.state.HomeSpecialBanner.MEGA_THIRTY_YEARS ->
            return DeliveryR.drawable.legacy_home_special_cabecalho_mega_trinta_anos_carrossel

        br.gov.caixa.loterias.feature.home.presentation.state.HomeSpecialBanner.LOTECA_FATHERS_DAY ->
            return DeliveryR.drawable.legacy_home_special_cabecalho_loteca_pais_carrossel_alta

        null -> Unit
    }
    return when (canonicalModality(lottery.resultCode)) {
        "MEGASENA" -> DeliveryR.drawable.legacy_home_special_mega_virada_novo
        "QUINA" -> DeliveryR.drawable.legacy_home_special_quina_sao_joao_novo
        "DUPLASENA" -> DeliveryR.drawable.legacy_home_special_dupla_pascoa
        "LOTOFACIL" -> DeliveryR.drawable.legacy_home_special_lotofacil_independencia
        "LOTECA" -> DeliveryR.drawable.legacy_home_special_loteca_copa
        else -> null
    }
}
