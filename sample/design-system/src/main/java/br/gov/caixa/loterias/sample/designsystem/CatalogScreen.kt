package br.gov.caixa.loterias.sample.designsystem

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSTopBar
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme

@Composable
fun DesignSystemCatalog() {
    val listState = rememberLazyListState()
    val typeListState = rememberLazyListState()
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    var typeKey by rememberSaveable { mutableStateOf<String?>(null) }
    val type = CatalogType.entries.find { it.key == typeKey }
    val selected = CatalogRegistry.findEntry(selectedId)
    val back: () -> Unit = { selectedId = null }
    BackHandler(enabled = selected != null, onBack = back)
    LoteriasCaixaDSTheme(darkTheme = false) {
        LoteriasCaixaDSScaffold(
            topBar = {
                LoteriasCaixaDSTopBar(
                    title = selected?.name ?: stringResource(R.string.catalog_title),
                    onBack = if (selected == null) null else back
                )
            },
            containerColor = LoteriasCaixaDS.colors.canvas
        ) { padding ->
            val contentModifier = Modifier.fillMaxSize().padding(padding).navigationBarsPadding()
            if (selected == null) {
                CatalogList(
                    query,
                    { query = it },
                    { selectedId = it.id },
                    contentModifier,
                    listState,
                    type = type,
                    onType = { typeKey = it?.key },
                    typeListState = typeListState
                )
            } else {
                CatalogDetail(selected, contentModifier)
            }
        }
    }
}
