package br.gov.caixa.loterias.feature.retail.presentation.route

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.R as DsR
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSHorizontalDivider as HorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.RetailSearchViewModel

@Composable
fun RetailSearchRoute(viewModel: RetailSearchViewModel, onBack: () -> Unit, onHelp: () -> Unit = {}) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_buscar_lotericas_c8f302),
        modifier = (Modifier.imePadding()).uiId("retail.0033"),
        onBack = onBack,
        gradientTopBar = false,
        topBarAction = {
            IconButton(onClick = onHelp, modifier = Modifier.uiId("retail.0034")) {
                Icon(
                    CaixaDSIcons.Help,
                    uiStrings.getString(LocalizedR.string.ui_duvidas_10e026),
                    (Modifier.size(28.dp)).uiId("retail.0035"),
                    tint = CaixaDS.colors.onBrand
                )
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).legacyAutomationId("retail-search-content")) {
            Column(Modifier.fillMaxSize()) {
                RetailSearchField(state.query, viewModel::changeQuery, viewModel::searchNow)
                HorizontalDivider((Modifier.padding(horizontal = 24.dp)).uiId("retail.0036"), thickness = 2.dp, color = CaixaDS.colors.line)
                when {
                    state.loading -> Unit

                    state.retailers.isEmpty() -> Column(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_nenhuma_loterica_encontrada_8bfcf1),
                            color = CaixaDSColorTokens.RetailSearchText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(
                                fontFamily = CaixaDSFuturaFontFamily,
                                platformStyle = PlatformTextStyle(includeFontPadding = true)
                            ),
                            modifier = Modifier.uiId("retail.0037")
                        )
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_verifique_o_nome_ou_codigo_digitado_4d0aaa),
                            color = CaixaDS.colors.mutedText,
                            fontSize = 16.sp,
                            style = TextStyle(
                                fontFamily = CaixaDSFuturaFontFamily,
                                platformStyle = PlatformTextStyle(includeFontPadding = true)
                            ),
                            modifier = Modifier.uiId("retail.0038")
                        )
                    }

                    else -> LazyColumn(contentPadding = PaddingValues(bottom = 8.dp), modifier = Modifier.uiId("retail.0039")) {
                        items(state.retailers, key = { it.id }) { retailer ->
                            RetailerResultRow(retailer, viewModel::requestToggleFavorite)
                        }
                    }
                }
            }
            if (state.loading) {
                CircularProgressIndicator(
                    Modifier.align(Alignment.Center).legacyAutomationId("retail-search-loader"),
                    color = CaixaDS.colors.brand
                )
            }
        }
    }
    RetailFavoriteConfirmation(state.favoriteCandidate, viewModel)
    state.errorMessage?.let {
        CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissError
        )
    }
}

@Composable
private fun RetailFavoriteConfirmation(
    candidate: br.gov.caixa.loterias.feature.retail.domain.model.Retailer?,
    viewModel: RetailSearchViewModel,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    candidate?.let { retailer ->
        val adding = !retailer.favorite
        CaixaDSAlertDialog(
            title = if (adding) {
                uiStrings.getString(LocalizedR.string.ui_favoritar_loterica_9b9006)
            } else {
                uiStrings.getString(LocalizedR.string.ui_remover_loterica_favorita_27587c)
            },
            message = uiStrings.getString(
                if (adding) {
                    LocalizedR.string.format_add_favorite_retailer
                } else {
                    LocalizedR.string.format_remove_favorite_retailer
                },
                retailer.fantasyName.trim().uppercase()
            ),
            confirmText = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
            onConfirm = viewModel::confirmToggleFavorite,
            onDismiss = viewModel::cancelToggleFavorite
        )
    }
}

@Composable
private fun RetailSearchField(query: String, onQueryChange: (String) -> Unit, onSearch: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.padding(24.dp)) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_lotericas_8c948f),
            color = CaixaDSColorTokens.RetailSearchText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                fontFamily = CaixaDSFuturaFontFamily,
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
                }.border(1.dp, CaixaDS.colors.brand, RoundedCornerShape(4.dp))
                ).uiId("retail.0041"),
            singleLine = true,
            textStyle = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = true),
                fontFamily = CaixaDSFuturaFontFamily,
                fontSize = 16.sp,
                color = CaixaDS.colors.mutedText
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
                            color = CaixaDS.colors.mutedText,
                            style = TextStyle(
                                fontFamily = CaixaDSFuturaFontFamily,
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
            color = CaixaDS.colors.mutedText,
            style = TextStyle(
                fontFamily = CaixaDSFuturaFontFamily,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            ),
            fontSize = 14.sp,
            modifier = Modifier.uiId("retail.0043")
        )
    }
}

@Composable
private fun RetailerResultRow(
    retailer: br.gov.caixa.loterias.feature.retail.domain.model.Retailer,
    onToggle: (br.gov.caixa.loterias.feature.retail.domain.model.Retailer) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().uiId("retail.0044").clickable { onToggle(retailer) }) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val code = retailer.code.let { if (it.length >= 9) it.substring(3, 9) else it }
            Text(
                listOf(
                    code,
                    retailer.fantasyName.trim()
                ).filter(String::isNotBlank).joinToString(" - ").uppercase(),
                (Modifier.weight(1f)).uiId("retail.0045"),
                color = CaixaDSColorTokens.RetailSearchText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    fontFamily = CaixaDSFuturaFontFamily,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                )
            )
            Icon(
                painterResource(
                    if (retailer.favorite) {
                        DsR.drawable.legacy_ic_orange_heart_filled
                    } else {
                        DsR.drawable.legacy_ic_orange_heart
                    }
                ),
                if (retailer.favorite) {
                    uiStrings.getString(LocalizedR.string.ui_remover_dos_favoritos_903370)
                } else {
                    uiStrings.getString(LocalizedR.string.ui_adicionar_aos_favoritos_5eb5af)
                },
                (Modifier.size(24.dp, 20.dp)).uiId("retail.0046"),
                tint = androidx.compose.ui.graphics.Color.Unspecified
            )
        }
        HorizontalDivider((Modifier.padding(horizontal = 24.dp)).uiId("retail.0047"), thickness = 2.dp, color = CaixaDS.colors.line)
    }
}

private val retailerSearchLineHeight = 24.sp
