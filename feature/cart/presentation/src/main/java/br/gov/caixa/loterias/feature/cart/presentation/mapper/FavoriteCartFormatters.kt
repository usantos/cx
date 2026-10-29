package br.gov.caixa.loterias.feature.cart.presentation.mapper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.presentation.formatting.localizedMessage as messageText

internal val favoriteTextStyle = TextStyle(
    fontFamily = CaixaDSFontFamily,
    fontSize = 14.sp,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

/** Dimensionamento automático uniforme do XML: linha única de 150dp, mínimo de 8sp, passos de 1sp e máximo de 14dp. */
@Composable
internal fun favoriteNameSize(name: String): androidx.compose.ui.unit.TextUnit {
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val width = with(density) { 150.dp.roundToPx() }
    return remember(name, density, measurer) {
        // Preserva o rótulo legado em escala normal; texto ampliado se reorganiza no tamanho base legível.
        if (density.fontScale > 1f) {
            14.sp
        } else {
            var size = FAVORITE_NAME_BASE_SP
            while (size > 8f && measurer.measure(
                    name,
                    favoriteTextStyle.copy(fontSize = size.sp),
                    maxLines = 1,
                    softWrap = false
                ).size.width > width
            ) {
                size -= 1f
            }
            size.coerceAtLeast(8f).sp
        }
    }
}

internal fun favoriteSummaryModalityName(bet: FavoriteCartBet): String = if (bet.modalityId ==
    9
) {
    messageText("milionaria_c791e4")
} else {
    bet.modality.lowercase()
}

internal fun favoriteDetailModalityName(bet: FavoriteCartBet): String = if (bet.modalityId ==
    9
) {
    messageText("milionaria_c791e4")
} else if (bet.modalityId == 20) {
    bet.modality
} else {
    bet.modality.lowercase()
}

internal fun favoriteBetSummaryText(bet: FavoriteCartBet): String = buildString {
    append(bet.numberCount)
    append(" num(s)")
    if (bet.cloverCount > 0) append(" ${bet.cloverCount} trev(s)")
}

internal fun favoriteBetComplement(bet: FavoriteCartBet): String? = when (bet.modalityId) {
    20 -> if (bet.surprise) "???" else bet.favoriteTeam
    11 -> if (bet.surprise) "???" else bet.luckyMonth
    else -> null
}

internal fun favoriteBetNumbers(bet: FavoriteCartBet): String = if (bet.surprise) {
    List(bet.numberCount) { "XX" }.joinToString(" - ")
} else {
    bet.numbers.joinToString(" - ") { if (it == 100) "00" else it.toString().padStart(2, '0') }
}

internal fun favoriteBetClovers(bet: FavoriteCartBet): String = "Trevos:  " + if (bet.surprise) {
    List(bet.cloverCount) { "XX" }.joinToString(" - ")
} else {
    bet.clovers.joinToString(" - ")
}

private const val FAVORITE_NAME_BASE_SP = 14f
