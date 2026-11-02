package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR

@Composable
internal fun ShakeTutorialTitle(page: Int) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val titles =
        listOf(
            uiStrings.getString(LocalizedR.string.ui_conheca_a_aposta_shake_e9534b),
            uiStrings.getString(LocalizedR.string.ui_abra_o_volante_de_apostas_f199e2),
            uiStrings.getString(LocalizedR.string.ui_agite_o_celular_para_completar_fc362b),
            uiStrings.getString(LocalizedR.string.ui_aposta_completada_automaticamente_32ab90),
            uiStrings.getString(LocalizedR.string.ui_ative_ou_desative_no_menu_lateral_9558e3),
            uiStrings.getString(LocalizedR.string.ui_vamos_testar_364c25)
        )
    val title = buildAnnotatedString {
        val highlights = listOf(
            uiStrings.getString(LocalizedR.string.ui_aposta_shake_b51943),
            "Abra o volante",
            "Agite o celular",
            uiStrings.getString(LocalizedR.string.ui_aposta_completada_cc22e0),
            uiStrings.getString(LocalizedR.string.ui_menu_lateral_d42910),
            ""
        )
        append(titles[page])
        val text = highlights[page]
        val start = titles[page].indexOf(text)
        if (text.isNotEmpty() &&
            start >= 0
        ) {
            addStyle(
                SpanStyle(color = LoteriasCaixaDSColorTokens.CaixaBlue, fontWeight = FontWeight.Bold),
                start,
                start + text.length
            )
        }
    }
    Box(Modifier.fillMaxWidth().heightIn(min = 64.dp).padding(horizontal = 32.dp), contentAlignment = Alignment.Center) {
        Text(
            title,
            (Modifier.fillMaxWidth().semantics { heading() }).uiId("bet.0212"),
            fontFamily = LoteriasCaixaDSRegularFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            color = LoteriasCaixaDSColorTokens.SocialGraphite,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun ShakeTutorialPagination(page: Int, onPage: (Int) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().height(24.dp).semantics {
            contentDescription = uiStrings.getString(LocalizedR.string.format_step_description, page + 1, TUTORIAL_PAGE_COUNT)
        },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(TUTORIAL_PAGE_COUNT) { index ->
            Box(
                Modifier.padding(horizontal = 3.dp).width(
                    if (page ==
                        index
                    ) {
                        24.dp
                    } else {
                        7.dp
                    }
                ).height(7.dp).background(
                    if (page ==
                        index
                    ) {
                        LoteriasCaixaDSColorTokens.CaixaBlue
                    } else {
                        LoteriasCaixaDSColorTokens.White
                    },
                    RoundedCornerShape(8.dp)
                ).uiId("bet.0217").clickable { onPage(index) }
            )
        }
    }
}

private const val TUTORIAL_PAGE_COUNT = 6
