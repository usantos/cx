package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText

internal val catalogPreviews: Map<String, CatalogPreview> =
    artworkPreviews0 +
        artworkPreviews1 +
        componentsPreviews0 +
        componentsPreviews1 +
        componentsPreviews2 +
        componentsPreviews3 +
        componentsPreviews4 +
        basePreviews0 +
        basePreviews1 +
        basePreviews2 +
        buttonPreviews0 +
        cartPreviews0 +
        dialogPreviews0 +
        inputPreviews0 +
        navigationPreviews0 +
        navigationPreviews1 +
        statePreviews0 +
        htmlPreviews0 +
        iconPreviews0 +
        themePreviews0 +
        webPreviews0 +
        layoutPreviews0

class PreviewState {
    var richText by mutableStateOf(androidx.compose.ui.text.input.TextFieldValue())
    var text by mutableStateOf("")
    var checked by mutableStateOf(false)
    var selection by mutableIntStateOf(0)
    var shares by mutableIntStateOf(1)
    var count by mutableIntStateOf(0)
    var show by mutableStateOf(false)
    val increaseShares: () -> Unit = {
        shares++
        count++
    }
    val decreaseShares: () -> Unit = {
        shares = (shares - 1).coerceAtLeast(1)
        count++
    }
    val action: () -> Unit = { count++ }
    val toggle: () -> Unit = {
        checked = !checked
        count++
    }
    val dismiss: () -> Unit = {
        show = false
        count++
    }
    val sample: String @Composable get() = stringResource(R.string.sample_text)
    val hint: String @Composable get() = stringResource(R.string.sample_hint)
    val actionLabel: String @Composable get() = stringResource(R.string.sample_action)
    val closeLabel: String @Composable get() = stringResource(R.string.sample_close)
    val city: String @Composable get() = stringResource(R.string.sample_city)
    val amount: String @Composable get() = stringResource(R.string.sample_amount)
    val html: String @Composable get() = stringResource(R.string.sample_html)
    val options: List<String> @Composable get() = stringResource(R.string.sample_options).split('|')
}

/** Estado local identificado por API, sem autenticação, repositórios ou grafo de serviços. */
@Composable
fun CatalogDemo(entry: CatalogEntry) {
    val state = remember(entry.id) { PreviewState() }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(
            Modifier.fillMaxWidth().semantics { contentDescription = entry.name },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            catalogPreviews.getValue(entry.id)(state)
        }
        CaixaDSText(stringResource(R.string.interaction_count, state.count))
    }
}

@Composable
internal fun OpenPreview(state: PreviewState, content: @Composable () -> Unit) {
    CaixaDSButton(onClick = { state.show = true }) { CaixaDSText(stringResource(R.string.open_preview)) }
    if (state.show) content()
}
