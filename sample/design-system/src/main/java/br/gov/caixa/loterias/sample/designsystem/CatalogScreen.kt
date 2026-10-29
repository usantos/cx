package br.gov.caixa.loterias.sample.designsystem

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSSectionHeader
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSTopBar
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme

@Composable
fun DesignSystemCatalog() {
    val listState = rememberLazyListState()
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    val selected = CatalogRegistry.entries.find { it.id == selectedId }
    val back: () -> Unit = { selectedId = null }
    BackHandler(enabled = selected != null, onBack = back)
    CaixaDSTheme(darkTheme = false) {
        CaixaDSScaffold(
            topBar = {
                CaixaDSTopBar(
                    title = selected?.name ?: stringResource(R.string.catalog_title),
                    onBack = if (selected == null) null else back
                )
            },
            containerColor = CaixaDS.colors.canvas
        ) { padding ->
            val contentModifier = Modifier.fillMaxSize().padding(padding).navigationBarsPadding()
            if (selected == null) {
                CatalogList(query, { query = it }, { selectedId = it.id }, contentModifier, listState)
            } else {
                CatalogDetail(selected, contentModifier)
            }
        }
    }
}

@Composable
internal fun CatalogList(
    query: String,
    onQuery: (String) -> Unit,
    onSelect: (CatalogEntry) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val results = CatalogRegistry.search(query)
    Column(modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CaixaDSText(stringResource(R.string.catalog_intro), Modifier.padding(top = 16.dp))
        CaixaDSTextField(
            value = query,
            onValueChange = onQuery,
            label = stringResource(R.string.search_label),
            modifier = Modifier.fillMaxWidth().testTag("catalog_search")
        )
        LazyColumn(Modifier.testTag("catalog_list"), state = listState, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (results.isEmpty()) {
                item { CaixaDSText(stringResource(R.string.empty_results)) }
            }
            results.groupBy { it.category }.forEach { (category, entries) ->
                item(key = category) {
                    CaixaDSSectionHeader(stringResource(category), modifier = Modifier.padding(top = 12.dp))
                }
                items(entries, key = { it.id }) { entry -> CatalogListItem(entry, onSelect) }
            }
            item { CaixaDSText(stringResource(R.string.preview_note), Modifier.padding(vertical = 16.dp)) }
        }
    }
}

@Composable
private fun CatalogListItem(entry: CatalogEntry, onSelect: (CatalogEntry) -> Unit) {
    CaixaDSCard(onClick = { onSelect(entry) }, modifier = Modifier.testTag(entry.id)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            CaixaDSText(entry.name, color = CaixaDS.colors.brand, style = CaixaDS.typography.titleMedium)
            CaixaDSText(entry.id.substringBeforeLast('.'), style = CaixaDS.typography.bodySmall)
        }
    }
}

@Composable
internal fun CatalogDetail(entry: CatalogEntry, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.testTag("catalog_detail"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { CaixaDSText(entry.id, style = CaixaDS.typography.bodySmall) }
        item {
            CaixaDSSectionHeader(stringResource(R.string.preview_title))
        }
        item {
            CaixaDSCard { CatalogDemo(entry) }
        }
        item {
            CaixaDSSectionHeader(stringResource(R.string.usage_title), stringResource(R.string.snippet_note))
        }
        item {
            SelectionContainer {
                CaixaDSText(stringResource(entry.snippet), fontFamily = FontFamily.Monospace, style = CaixaDS.typography.bodySmall)
            }
        }
        item {
            CaixaDSSectionHeader(stringResource(R.string.parameters_title))
        }
        item { CaixaDSText(stringResource(entry.description)) }
        item {
            SelectionContainer {
                CaixaDSText(stringResource(entry.signatures), fontFamily = FontFamily.Monospace)
            }
        }
        item { CaixaDSText(stringResource(R.string.preview_note)) }
    }
}
