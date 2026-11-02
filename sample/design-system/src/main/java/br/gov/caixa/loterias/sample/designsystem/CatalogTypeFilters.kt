package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSButtonVariant

@Composable
internal fun CatalogTypeFilters(type: CatalogType?, onType: (CatalogType?) -> Unit, listState: LazyListState = rememberLazyListState()) {
    LazyRow(state = listState, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item(key = "all") {
            CatalogTypeButton(
                "all",
                stringResource(R.string.catalog_all_types),
                CatalogRegistry.entries.size,
                type == null,
                { onType(null) }
            )
        }
        items(CatalogType.entries, key = { it.key }) { option ->
            CatalogTypeButton(
                option.key,
                stringResource(option.title),
                CatalogRegistry.entries.count { it.category == option.title },
                type == option,
                { onType(option) }
            )
        }
    }
}

@Composable
private fun CatalogTypeButton(key: String, label: String, count: Int, isSelected: Boolean, onClick: () -> Unit) {
    LoteriasCaixaDSButton(
        text = stringResource(R.string.catalog_type_count, label, count),
        onClick = onClick,
        modifier = Modifier.uiId("catalog.type.$key").semantics { selected = isSelected },
        variant = if (isSelected) LoteriasCaixaDSButtonVariant.Primary else LoteriasCaixaDSButtonVariant.Secondary
    )
}
