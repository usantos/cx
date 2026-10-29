package br.gov.caixa.loterias.feature.cart.presentation.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.presentation.components.CartRow
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.LocalCartReviewViewModel
import java.time.Instant

@Composable
fun LocalCartReviewRoute(viewModel: LocalCartReviewViewModel, onBack: () -> Unit, onRemoteCart: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    var confirm by rememberSaveable { mutableStateOf(false) }
    LocalCartReviewContent(state, onBack, onRemoteCart, viewModel::load, onConfirmDiscard = { confirm = true })
    if (state.loading) CaixaDSAuthenticationLoadingDialog()
    if (confirm) {
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_deseja_excluir_as_apostas_locais_mos_cd1897),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onPositive = {
                confirm = false
                viewModel.discard()
            },
            onNegative = { confirm = false }
        )
    }
}

@Composable
private fun LocalCartReviewContent(
    state: br.gov.caixa.loterias.feature.cart.presentation.state.LocalCartReviewState,
    onBack: () -> Unit,
    onRemoteCart: () -> Unit,
    onLoad: () -> Unit,
    onConfirmDiscard: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_apostas_preservadas_39e62c),
        onBack = onBack,
        modifier = Modifier
            .uiId("cart.0079")
    ) { padding ->
        LazyColumn(
            (Modifier.fillMaxSize().padding(padding))
                .uiId("cart.0080"),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_estas_apostas_ficaram_sem_confirmaca_ca5e91),
                    style = CaixaDS.typography.bodyMedium,
                    modifier = Modifier.uiId("cart.0081")
                )
            }
            if (state.discarded) {
                item {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_as_apostas_preservadas_foram_excluid_558d60),
                        style = CaixaDS.typography.bodyMedium,
                        modifier = Modifier.uiId("cart.0082")
                    )
                }
            }
            state.error?.let { message ->
                item {
                    Text(
                        message,
                        color = CaixaDS.colors.error,
                        style = CaixaDS.typography.bodyMedium,
                        modifier = Modifier
                            .uiId("cart.0083")
                    )
                    CaixaDSButton(
                        uiStrings.getString(LocalizedR.string.ui_atualizar_consulta_996a29),
                        onClick = onLoad,
                        modifier = Modifier.uiId("cart.0084")
                    )
                }
            }
            if (!state.loading && state.error == null && !state.discarded) {
                localCartReviewItems(state, onConfirmDiscard)
            }
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CaixaDSButton(
                        uiStrings.getString(LocalizedR.string.ui_conferir_carrinho_da_conta_9fe601),
                        onClick = onRemoteCart,
                        enabled = !state.loading,
                        modifier = Modifier.uiId("cart.0090")
                    )
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.localCartReviewItems(
    state: br.gov.caixa.loterias.feature.cart.presentation.state.LocalCartReviewState,
    onConfirmDiscard: () -> Unit,
) {
    item {
        Text(
            androidx.compose.ui.res.stringResource(LocalizedR.string.ui_carrinho_da_conta_2b6e20),
            style = CaixaDS.typography.titleMedium,
            modifier = Modifier.uiId("cart.0085")
        )
    }
    items(state.remote?.items.orEmpty(), key = { "remote-${it.id}" }) { CartRow(it, Instant.now(), null, null) }
    if (state.remote?.items.isNullOrEmpty()) {
        item {
            Text(
                androidx.compose.ui.res.stringResource(
                    LocalizedR.string.ui_o_carrinho_da_conta_esta_vazio_03261b
                ),
                style = CaixaDS.typography.bodyMedium,
                modifier = Modifier.uiId("cart.0086")
            )
        }
    }
    item {
        Text(
            androidx.compose.ui.res.stringResource(LocalizedR.string.ui_apostas_locais_preservadas_070a46),
            style = CaixaDS.typography.titleMedium,
            modifier = Modifier.uiId("cart.0087")
        )
    }
    items(state.local?.items.orEmpty(), key = { "local-${it.id}" }) { CartRow(it, Instant.now(), null, null) }
    if (state.review == null) {
        item {
            Text(
                androidx.compose.ui.res.stringResource(LocalizedR.string.ui_nao_ha_lote_pendente_disponivel_para_8d1b5e),
                style = CaixaDS.typography.bodyMedium,
                modifier = Modifier.uiId("cart.0088")
            )
        }
    } else {
        item {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CaixaDSButton(
                    androidx.compose.ui.res.stringResource(LocalizedR.string.ui_excluir_apostas_preservadas_904b4c),
                    onClick = onConfirmDiscard,
                    modifier = Modifier.uiId("cart.0089")
                )
            }
        }
    }
}
