package br.gov.caixa.loterias.feature.support.presentation.mapper

private const val NATIVE_QUINA_ID = 3
private const val BETTING_QUINA_ID = 1
private const val NATIVE_LOTOMANIA_ID = 16
private const val BETTING_LOTOMANIA_ID = 5
private const val NATIVE_LOTECA_ID = 13
private const val BETTING_LOTECA_ID = 19

internal fun bettingModalityId(id: Int): Int = when (id) {
    NATIVE_QUINA_ID -> BETTING_QUINA_ID
    NATIVE_LOTOMANIA_ID -> BETTING_LOTOMANIA_ID
    NATIVE_LOTECA_ID -> BETTING_LOTECA_ID
    else -> id
}
