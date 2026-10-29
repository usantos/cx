package br.gov.caixa.loterias.feature.home.presentation.components

import br.gov.caixa.loterias.feature.home.presentation.formatting.localizedMessage as messageText

fun infiniteCarouselInitialPage(itemCount: Int): Int {
    require(itemCount > 0) { messageText("o_carousel_precisa_ter_ao_menos_d656c9") }
    val middle = Int.MAX_VALUE / 2
    return middle - middle.mod(itemCount)
}

fun loopedCarouselIndex(page: Int, itemCount: Int): Int {
    require(itemCount > 0) { messageText("o_carousel_precisa_ter_ao_menos_d656c9") }
    return page.mod(itemCount)
}

fun carouselCardScale(distanceFromSelectedPage: Float): Float {
    val distance = distanceFromSelectedPage.coerceIn(0f, 1f)
    val easedDistance = distance * distance * (SMOOTHSTEP_CUBIC_COEFFICIENT - 2f * distance)
    return 1f - easedDistance * 0.20f
}

private const val SMOOTHSTEP_CUBIC_COEFFICIENT = 3f
