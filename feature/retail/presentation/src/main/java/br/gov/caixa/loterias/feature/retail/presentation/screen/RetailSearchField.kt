package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR

@Composable
internal fun RetailSearchField(query: String, onQueryChange: (String) -> Unit, onSearch: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.padding(24.dp)) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_lotericas_8c948f),
            color = LoteriasCaixaDSColorTokens.RetailSearchText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                fontFamily = LoteriasCaixaDSFuturaFontFamily,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            ),
            modifier = Modifier.uiId("retail.0040")
        )
        BasicTextField(
            query,
            onQueryChange,
            (
                Modifier.fillMaxWidth().padding(top = 4.dp).height(
                    maxOf(
                        40.dp,
                        with(LocalDensity.current) {
                            retailerSearchLineHeight.toDp()
                        }
                    )
                ).semantics {
                    contentDescription =
                        uiStrings.getString(LocalizedR.string.ui_nome_ou_codigo_47d071)
                }.border(1.dp, LoteriasCaixaDS.colors.brand, RoundedCornerShape(4.dp))
                ).uiId("retail.0041"),
            singleLine = true,
            textStyle = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = true),
                fontFamily = LoteriasCaixaDSFuturaFontFamily,
                fontSize = 16.sp,
                color = LoteriasCaixaDS.colors.mutedText
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                onSearch()
            }),
            decorationBox = { field ->
                Box(Modifier.fillMaxSize().padding(horizontal = 8.dp), contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) {
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_nome_ou_codigo_47d071),
                            color = LoteriasCaixaDS.colors.mutedText,
                            style = TextStyle(
                                fontFamily = LoteriasCaixaDSFuturaFontFamily,
                                platformStyle = PlatformTextStyle(includeFontPadding = true)
                            ),
                            fontSize = 16.sp,
                            modifier = Modifier.uiId("retail.0042")
                        )
                    }
                    field()
                }
            }
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_insira_o_nome_da_loterica_ou_o_codig_9bea3e),
            color = LoteriasCaixaDS.colors.mutedText,
            style = TextStyle(
                fontFamily = LoteriasCaixaDSFuturaFontFamily,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            ),
            fontSize = 14.sp,
            modifier = Modifier.uiId("retail.0043")
        )
    }
}

private val retailerSearchLineHeight = 24.sp
