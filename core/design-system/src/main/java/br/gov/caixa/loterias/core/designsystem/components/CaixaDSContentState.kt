package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS

/** Apresentação compartilhada de carregamento, erro e conteúdo; carregamento tem precedência sobre um erro existente. */
@Composable
fun CaixaDSContentState(loading: Boolean, error: String?, padding: PaddingValues, onRetry: () -> Unit, content: @Composable () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    when {
        loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CaixaDS.colors.teal)
        }

        error != null -> Column(
            Modifier.fillMaxSize().padding(padding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(error, color = CaixaDS.colors.error)
            Text(
                uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                Modifier.uiId("ds.0009").clickable(onClick = onRetry).padding(20.dp),
                color = CaixaDS.colors.brand
            )
        }

        else -> content()
    }
}
