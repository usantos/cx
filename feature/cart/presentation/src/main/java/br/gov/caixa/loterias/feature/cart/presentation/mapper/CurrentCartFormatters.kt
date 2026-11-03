package br.gov.caixa.loterias.feature.cart.presentation.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.resolveLoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.presentation.formatting.localizedMessage as messageText
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

val CartItemKind.sectionTitle: String
    get() = when (this) {
        CartItemKind.POOL_SHARE -> messageText("cotas_bolao_86b8bf")
        CartItemKind.INDIVIDUAL_BET -> "Apostas Individuais"
        CartItemKind.COMBO -> messageText("combo_de_apostas_c1d6d3")
    }

val CartItemBadge.label: String
    get() = when (this) {
        CartItemBadge.EXCHANGE -> "TROCA"
        CartItemBadge.POOL -> messageText("bolao_e1fa12")
        CartItemBadge.COMBO -> "COMBO"
        CartItemBadge.MIRROR -> "ESPELHO"
        CartItemBadge.SURPRISE -> "SURPRESINHA"
        CartItemBadge.RECURRING -> "TEIMOSINHA"
    }

@Composable
fun cartModalityColor(code: String): Color {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val style = loteriasCaixaDSLotteryStyle(0, code)
    if (style != resolveLoteriasCaixaDSLotteryStyle(0, code, LoteriasCaixaDSCampaign.DEFAULT)) return style.textOnSurface
    return when (code.uppercase(Locale.ROOT)) {
        "MEGA_SENA", "MEGASENA", "MEGA-SENA" -> LoteriasCaixaDSColorTokens.MegaSena
        "LOTOFACIL", uiStrings.getString(LocalizedR.string.ui_lotofacil_dd4339) -> LoteriasCaixaDSColorTokens.Lotofacil
        "QUINA" -> LoteriasCaixaDSColorTokens.Quina
        "LOTOMANIA" -> LoteriasCaixaDSColorTokens.Lotomania
        "TIMEMANIA" -> LoteriasCaixaDSColorTokens.Timemania
        "DUPLA_SENA", "DUPLASENA", "DUPLA-SENA" -> LoteriasCaixaDSColorTokens.DuplaSena
        "DIA_DE_SORTE", "DIADESORTE", "DIA-DE-SORTE" -> LoteriasCaixaDSColorTokens.DiaDeSorte
        "SUPER_SETE", "SUPERSETE", "SUPER-SETE" -> LoteriasCaixaDSColorTokens.SuperSete
        "MAIS_MILIONARIA", "MAISMILIONARIA", "+MILIONARIA" -> LoteriasCaixaDSColorTokens.MaisMilionaria
        else -> LoteriasCaixaDS.colors.brand
    }
}

fun formatCartAmount(value: BigDecimal?): String = NumberFormat
    .getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    .format(value ?: BigDecimal.ZERO)
    .replace('\u00A0', ' ')
    .replace('\u202F', ' ')

fun cartText(size: Int = 14, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = LoteriasCaixaDSFontFamily,
    fontSize = size.sp,
    fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

fun currentCartModalityName(item: CartItem): String {
    val title = item.title.lowercase(Locale.ROOT)
    // Descrições de concursos especiais fornecidas pelo servidor preservam seus próprios rótulos.
    if (listOf(
            "virada",
            "independência",
            messageText("sao_joao_395cc6"),
            messageText("pascoa_64b52e"),
            "especial"
        ).any(title::contains)
    ) {
        return title
    }
    return when (item.modalityCode.uppercase(Locale.ROOT)) {
        "MEGA_SENA", "MEGASENA", "MEGA-SENA" -> "mega-sena"
        "LOTOFACIL", "LOTOFÁCIL" -> messageText("lotofacil_dbb2e4")
        "MAIS_MILIONARIA", "MAISMILIONARIA", "+MILIONARIA" -> messageText("milionaria_c791e4")
        "DUPLA_SENA", "DUPLASENA", "DUPLA-SENA" -> "dupla sena"
        "DIA_DE_SORTE", "DIADESORTE", "DIA-DE-SORTE" -> messageText("dia_de_sorte_71bc30")
        "SUPER_7", "SUPER_SETE", "SUPERSETE", "SUPER-SETE" -> "super sete"
        "QUINA" -> "quina"
        "LOTOMANIA" -> "lotomania"
        "TIMEMANIA" -> "timemania"
        "LOTECA" -> "loteca"
        else -> title
    }
}
