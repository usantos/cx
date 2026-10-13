package br.gov.caixa.loterias.feature.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.*
import java.time.Instant

@Composable
fun LocalCartReviewRoute(viewModel: LocalCartReviewViewModel, onBack: () -> Unit, onRemoteCart: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var confirm by rememberSaveable { mutableStateOf(false) }
    CaixaDSLegacyScaffold(title = "Apostas preservadas", onBack = onBack) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { Text("Estas apostas ficaram sem confirmação de envio. Compare com o carrinho da sua conta. Nenhuma aposta será enviada novamente por esta tela.", style = CaixaDS.typography.bodyMedium) }
            if (state.discarded) item { Text("As apostas preservadas foram excluídas. O carrinho da conta foi mantido.", style = CaixaDS.typography.bodyMedium) }
            state.error?.let { message -> item { Text(message, color = CaixaDS.colors.error, style = CaixaDS.typography.bodyMedium); CaixaDSButton("Atualizar consulta", onClick = viewModel::load) } }
            if (!state.loading && state.error == null && !state.discarded) {
                item { Text("Carrinho da conta", style = CaixaDS.typography.titleMedium) }
                items(state.remote?.items.orEmpty(), key = { "remote-${it.id}" }) { CartRow(it, Instant.now(), null, null) }
                if (state.remote?.items.isNullOrEmpty()) item { Text("O carrinho da conta está vazio.", style = CaixaDS.typography.bodyMedium) }
                item { Text("Apostas locais preservadas", style = CaixaDS.typography.titleMedium) }
                items(state.local?.items.orEmpty(), key = { "local-${it.id}" }) { CartRow(it, Instant.now(), null, null) }
                if (state.review == null) item { Text("Não há lote pendente disponível para esta conta. Um lote de outra conta só pode ser revisado pelo usuário que iniciou o envio.", style = CaixaDS.typography.bodyMedium) }
                else item { CaixaDSButton("Excluir apostas preservadas", onClick = { confirm = true }) }
            }
            item { CaixaDSButton("Conferir carrinho da conta", onClick = onRemoteCart, enabled = !state.loading) }
        }
    }
    if (state.loading) CaixaDSAuthenticationLoadingDialog()
    if (confirm) CaixaDSLegacyDialog(
        title = "Atenção", message = "Deseja excluir as apostas locais mostradas nesta tela? Esta ação não exclui as apostas do carrinho da sua conta.",
        positiveLabel = "Sim", negativeLabel = "Não",
        onPositive = { confirm = false; viewModel.discard() }, onNegative = { confirm = false },
    )
}
