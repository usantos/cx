package br.gov.caixa.loterias.feature.results.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.results.R as LocalizedR

@Composable
internal fun ResultContestSearchAction(searchVisible: Boolean, onToggle: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Icon(
        if (searchVisible) CaixaDSIcons.Close else CaixaDSIcons.Search,
        contentDescription = if (searchVisible) {
            uiStrings.getString(LocalizedR.string.ui_fechar_pesquisa_13432a)
        } else {
            uiStrings.getString(
                LocalizedR.string.ui_pesquisar_concurso_b0b4bb
            )
        },
        modifier = (Modifier.padding(end = 16.dp).size(24.dp).uiId("results.0034").clickable(onClick = onToggle)).uiId("results.0035"),
        tint = CaixaDSColorTokens.White
    )
}

@Composable
internal fun ResultContestSearchField(
    contestInput: String,
    searchFocusRequester: FocusRequester,
    onContestChange: (String) -> Unit,
    onSearch: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    BasicTextField(
        value = contestInput,
        onValueChange = onContestChange,
        modifier = (
            Modifier.fillMaxWidth().height(maxOf(36.dp, with(LocalDensity.current) { contestSearchLineHeight.toDp() })).semantics {
                contentDescription =
                    uiStrings.getString(LocalizedR.string.ui_numero_do_concurso_eb635d)
            }.focusRequester(searchFocusRequester)
                .background(CaixaDSColorTokens.White, RoundedCornerShape(4.dp)).padding(horizontal = 10.dp)
            ).uiId("results.0036"),
        singleLine = true,
        textStyle = CaixaDS.typography.bodyMedium.copy(color = CaixaDSColorTokens.Black),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onSearch() }),
        decorationBox = { field ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                if (contestInput.isEmpty()) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_numero_do_concurso_eb635d),
                        color = CaixaDSColorTokens.Neutral600,
                        fontSize = 14.sp,
                        modifier = Modifier.uiId("results.0037")
                    )
                }
                field()
            }
        }
    )
}

private val contestSearchLineHeight = 24.sp
