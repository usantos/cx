package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSHorizontalDivider as HorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.state.RetailSearchUiState
import br.gov.caixa.loterias.feature.retail.ui.R as DeliveryR

@Composable
internal fun DeliveredRetailSearchScreen(state: RetailSearchUiState, actions: RetailSearchActions, onBack: () -> Unit, onHelp: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(DeliveryR.string.ui_buscar_lotericas_c8f302),
        modifier = (Modifier.imePadding()).uiId("retail.0033"),
        onBack = onBack,
        gradientTopBar = false,
        topBarAction = {
            IconButton(onClick = onHelp, modifier = Modifier.uiId("retail.0034")) {
                Icon(
                    LoteriasCaixaDSIcons.Help,
                    uiStrings.getString(LocalizedR.string.ui_duvidas_10e026),
                    (Modifier.size(28.dp)).uiId("retail.0035"),
                    tint = LoteriasCaixaDS.colors.onBrand
                )
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).legacyAutomationId("retail-search-content")) {
            Column(Modifier.fillMaxSize()) {
                RetailSearchField(state.query, actions.changeQuery, actions.search)
                HorizontalDivider(
                    (Modifier.padding(horizontal = 24.dp)).uiId("retail.0036"),
                    thickness = 2.dp,
                    color = LoteriasCaixaDS.colors.line
                )
                RetailSearchResults(state, actions)
            }
            if (state.loading) {
                CircularProgressIndicator(
                    Modifier.align(Alignment.Center).legacyAutomationId("retail-search-loader"),
                    color = LoteriasCaixaDS.colors.brand
                )
            }
        }
    }
    RetailFavoriteConfirmation(state.favoriteCandidate, actions.confirmFavorite, actions.cancelFavorite)
    state.errorMessage?.let {
        LoteriasCaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            actions.dismissError
        )
    }
}

@Composable
private fun RetailFavoriteConfirmation(
    candidate: br.gov.caixa.loterias.core.domain.contract.retail.model.Retailer?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    candidate?.let { retailer ->
        val adding = !retailer.favorite
        LoteriasCaixaDSAlertDialog(
            title = if (adding) {
                uiStrings.getString(LocalizedR.string.ui_favoritar_loterica_9b9006)
            } else {
                uiStrings.getString(DeliveryR.string.ui_remover_loterica_favorita_27587c)
            },
            message = uiStrings.getString(
                if (adding) {
                    DeliveryR.string.format_add_favorite_retailer
                } else {
                    DeliveryR.string.format_remove_favorite_retailer
                },
                retailer.fantasyName.trim().uppercase()
            ),
            confirmText = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
}
