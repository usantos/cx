package br.gov.caixa.loterias.feature.retail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.compose.material3.HorizontalDivider
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCard
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyEmptyState
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyLabelledField
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPrimaryButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold

@Composable
fun RetailSearchRoute(viewModel: RetailSearchViewModel, onBack: () -> Unit, onHelp: () -> Unit = {}) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CaixaDSLegacyScaffold(title = "Buscar lotéricas", onBack = onBack, gradientTopBar = false, topBarTitleSize = 20.sp, topBarAction = {
        IconButton(onClick = onHelp) { Icon(CaixaDSIcons.Help, "Dúvidas", Modifier.size(28.dp), tint = CaixaDS.colors.onBrand) }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Column(Modifier.padding(24.dp)) {
                Text("Lotéricas", color = CaixaDSColorTokens.LegacyRetailSearchText, fontSize = 14.sp, fontWeight = FontWeight.Bold, style = TextStyle(fontFamily = CaixaDSLegacyFuturaFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)))
                BasicTextField(state.query, viewModel::changeQuery,
                    Modifier.fillMaxWidth().padding(top = 4.dp).height(40.dp).border(1.dp, CaixaDS.colors.brand, RoundedCornerShape(4.dp)),
                    singleLine = true, textStyle = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true), fontFamily = CaixaDSLegacyFuturaFontFamily, fontSize = 16.sp, color = CaixaDS.colors.legacyMutedText),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { viewModel.searchNow() }),
                    decorationBox = { field -> Box(Modifier.fillMaxSize().padding(horizontal = 8.dp), contentAlignment = Alignment.CenterStart) {
                        if (state.query.isEmpty()) Text("Nome ou código", color = CaixaDS.colors.legacyMutedText, style = TextStyle(fontFamily = CaixaDSLegacyFuturaFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)), fontSize = 16.sp)
                        field()
                    } },
                )
                Text("Insira o nome da lotérica ou o código somente com os números do segundo campo, com 6 dígitos, Exemplo: E.Lotérico 10.000004-01, digite 000004.", color = CaixaDS.colors.legacyMutedText, style = TextStyle(fontFamily = CaixaDSLegacyFuturaFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)), fontSize = 14.sp)
            }
            HorizontalDivider(Modifier.padding(horizontal = 24.dp), thickness = 2.dp, color = CaixaDS.colors.legacyLine)
            when {
                state.loading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally), color = CaixaDS.colors.brand)
                state.retailers.isEmpty() -> Column(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Text("Nenhuma Lotérica encontrada", color = CaixaDSColorTokens.LegacyRetailSearchText, fontSize = 16.sp, fontWeight = FontWeight.Bold, style = TextStyle(fontFamily = CaixaDSLegacyFuturaFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)))
                    Text("Verifique o nome ou código digitado e tente novamente.", color = CaixaDS.colors.legacyMutedText, fontSize = 16.sp, style = TextStyle(fontFamily = CaixaDSLegacyFuturaFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)))
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(bottom = 8.dp),
                ) {
                    items(state.retailers, key = { it.id }) { retailer ->
                        Column(Modifier.fillMaxWidth().clickable { viewModel.requestToggleFavorite(retailer) }) {
                            Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                                val code = retailer.code.let { if (it.length >= 9) it.substring(3, 9) else it }
                                Text(listOf(code, retailer.fantasyName.trim()).filter(String::isNotBlank).joinToString(" - ").uppercase(), Modifier.weight(1f), color = CaixaDSColorTokens.LegacyRetailSearchText, fontSize = 18.sp, fontWeight = FontWeight.Bold, style = TextStyle(fontFamily = CaixaDSLegacyFuturaFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)))
                                Icon(
                                    painterResource(if (retailer.favorite) br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_ic_orange_heart_filled else br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_ic_orange_heart),
                                    if (retailer.favorite) "Remover dos favoritos" else "Adicionar aos favoritos", Modifier.size(24.dp, 20.dp), tint = androidx.compose.ui.graphics.Color.Unspecified,
                                )
                            }
                            HorizontalDivider(Modifier.padding(horizontal = 24.dp), thickness = 2.dp, color = CaixaDS.colors.legacyLine)
                        }
                    }
                }
            }
        }
    }
    state.favoriteCandidate?.let { retailer ->
        val adding = !retailer.favorite
        CaixaDSAlertDialog(
            title = if (adding) "Favoritar lotérica" else "Remover lotérica favorita",
            message = "Deseja ${if (adding) "incluir" else "excluir"} ${retailer.fantasyName.trim().uppercase()} ${if (adding) "na" else "da"} sua lista de favoritas?",
            confirmText = "Confirmar",
            onConfirm = viewModel::confirmToggleFavorite,
            onDismiss = viewModel::cancelToggleFavorite,
        )
    }
    state.errorMessage?.let { CaixaDSAlertDialog("Atenção", it, "Entendi", viewModel::dismissError) }
}
