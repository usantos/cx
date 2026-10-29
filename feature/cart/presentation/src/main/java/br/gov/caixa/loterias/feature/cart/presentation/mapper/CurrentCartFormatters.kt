package br.gov.caixa.loterias.feature.cart.presentation.mapper

import br.gov.caixa.loterias.feature.cart.presentation.formatting.localizedMessage as messageText

import br.gov.caixa.loterias.feature.cart.R as LocalizedR

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.resolveCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

internal val CartItemKind.sectionTitle: String
    get() = when (this) {
        CartItemKind.POOL_SHARE -> messageText("cotas_bolao_86b8bf")
        CartItemKind.INDIVIDUAL_BET -> "Apostas Individuais"
        CartItemKind.COMBO -> messageText("combo_de_apostas_c1d6d3")
    }

internal val CartItemBadge.label: String
    get() = when (this) {
        CartItemBadge.EXCHANGE -> "TROCA"
        CartItemBadge.POOL -> messageText("bolao_e1fa12")
        CartItemBadge.COMBO -> "COMBO"
        CartItemBadge.MIRROR -> "ESPELHO"
        CartItemBadge.SURPRISE -> "SURPRESINHA"
        CartItemBadge.RECURRING -> "TEIMOSINHA"
    }

@Composable
internal fun cartModalityColor(code: String): Color {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val style = caixaDSLotteryStyle(0, code)
    if (style != resolveCaixaDSLotteryStyle(0, code, CaixaDSCampaign.DEFAULT)) return style.textOnSurface
    return when (code.uppercase(Locale.ROOT)) {
        "MEGA_SENA", "MEGASENA", "MEGA-SENA" -> CaixaDSColorTokens.MegaSena
        "LOTOFACIL", uiStrings.getString(LocalizedR.string.ui_lotofacil_dd4339) -> CaixaDSColorTokens.Lotofacil
        "QUINA" -> CaixaDSColorTokens.Quina
        "LOTOMANIA" -> CaixaDSColorTokens.Lotomania
        "TIMEMANIA" -> CaixaDSColorTokens.Timemania
        "DUPLA_SENA", "DUPLASENA", "DUPLA-SENA" -> CaixaDSColorTokens.DuplaSena
        "DIA_DE_SORTE", "DIADESORTE", "DIA-DE-SORTE" -> CaixaDSColorTokens.DiaDeSorte
        "SUPER_SETE", "SUPERSETE", "SUPER-SETE" -> CaixaDSColorTokens.SuperSete
        "MAIS_MILIONARIA", "MAISMILIONARIA", "+MILIONARIA" -> CaixaDSColorTokens.MaisMilionaria
        else -> CaixaDS.colors.brand
    }
}

internal fun formatCartAmount(value: BigDecimal?): String = NumberFormat
    .getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    .format(value ?: BigDecimal.ZERO)
    .replace('\u00A0', ' ')
    .replace('\u202F', ' ')

internal fun cartText(size: Int = 14, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSFontFamily,
    fontSize = size.sp,
    fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

internal fun currentCartModalityName(item: CartItem): String {
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
