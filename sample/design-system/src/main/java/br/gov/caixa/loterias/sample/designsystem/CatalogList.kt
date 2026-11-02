package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSectionHeader
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS

@Composable
internal fun CatalogList(
    query: String,
    onQuery: (String) -> Unit,
    onSelect: (CatalogEntry) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    type: CatalogType? = null,
    onType: (CatalogType?) -> Unit = {},
    typeListState: LazyListState = rememberLazyListState(),
) {
    val groups = CatalogRegistry.groups(query, type)
    Column(modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LoteriasCaixaDSText(stringResource(R.string.catalog_intro), Modifier.padding(top = 16.dp))
        LoteriasCaixaDSTextField(
            value = query,
            onValueChange = onQuery,
            label = stringResource(R.string.search_label),
            modifier = Modifier.fillMaxWidth().testTag("catalog_search")
        )
        CatalogTypeFilters(type, onType, typeListState)
        LazyColumn(Modifier.testTag("catalog_list"), state = listState, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (groups.isEmpty()) {
                item { LoteriasCaixaDSText(stringResource(R.string.empty_results)) }
            }
            groups.forEach { (group, entries) ->
                item(key = group.key) {
                    LoteriasCaixaDSSectionHeader(stringResource(group.title), modifier = Modifier.padding(top = 12.dp))
                }
                items(entries, key = { it.id }) { entry -> CatalogListItem(entry, onSelect) }
            }
            item { LoteriasCaixaDSText(stringResource(R.string.preview_note), Modifier.padding(vertical = 16.dp)) }
        }
    }
}

@Composable
private fun CatalogListItem(entry: CatalogEntry, onSelect: (CatalogEntry) -> Unit) {
    LoteriasCaixaDSCard(onClick = { onSelect(entry) }, modifier = Modifier.testTag(entry.id)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            LoteriasCaixaDSText(entry.name, color = LoteriasCaixaDS.colors.brand, style = LoteriasCaixaDS.typography.titleMedium)
            LoteriasCaixaDSText(entry.id.substringBeforeLast('.'), style = LoteriasCaixaDS.typography.bodySmall)
        }
    }
}
