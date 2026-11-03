package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

val LegacyHomeEasing = androidx.compose.animation.core.Easing {
    ((kotlin.math.cos((it + 1f) * kotlin.math.PI) / 2.0) + .5).toFloat()
}

const val QUICK_ACCESS_ANIMATION_DURATION_MILLIS = 500
val QuickAccessContentOffset = 404.dp
val OctoberRoseContentOffset = 534.dp
val OctoberRosePink = LoteriasCaixaDSColorTokens.OctoberRose
