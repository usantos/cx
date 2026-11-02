package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSectionHeader
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS

@Composable
internal fun CatalogDetail(entry: CatalogEntry, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.testTag("catalog_detail"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { LoteriasCaixaDSText(entry.id, style = LoteriasCaixaDS.typography.bodySmall) }
        item {
            LoteriasCaixaDSSectionHeader(stringResource(R.string.preview_title))
        }
        item {
            LoteriasCaixaDSCard { CatalogDemo(entry) }
        }
        item {
            CatalogCodePanel(
                title = stringResource(R.string.usage_title),
                code = stringResource(entry.snippet),
                copyId = "catalog.copy.usage",
                copyLabel = stringResource(R.string.catalog_copy_usage),
                note = stringResource(R.string.snippet_note)
            )
        }
        item { LoteriasCaixaDSText(stringResource(entry.description)) }
        item {
            CatalogCodePanel(
                title = stringResource(R.string.parameters_title),
                code = stringResource(entry.signatures),
                copyId = "catalog.copy.signature",
                copyLabel = stringResource(R.string.catalog_copy_signature)
            )
        }
        item { LoteriasCaixaDSText(stringResource(R.string.preview_note)) }
    }
}
