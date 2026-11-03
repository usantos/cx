package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSHorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSNumberPicker
import br.gov.caixa.loterias.feature.tickets.R
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption

@Composable
internal fun BetFilterWheel(label: String, options: List<TicketFilterOption>, selected: Long, onSelect: (Long) -> Unit) {
    LoteriasCaixaDSNumberPicker(
        label = label,
        options = options.map { it.description },
        selectedIndex = options.indexOfFirst { it.value == selected },
        onSelect = { index -> options.getOrNull(index)?.let { onSelect(it.value) } },
        modifier = Modifier.uiId("tickets.0122")
    )
}

@Composable
internal fun BetFilterGroup(title: String, content: @Composable () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var expanded by rememberSaveable(title) { mutableStateOf(false) }
    val description = stringResource(
        if (expanded) R.string.tickets_filter_expanded else R.string.tickets_filter_collapsed
    )
    Column {
        Row(
            Modifier.fillMaxWidth().semantics { stateDescription = description }
                .uiId("tickets.0123").clickable { expanded = !expanded }.padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, modifier = Modifier.uiId("tickets.0124"))
            Text(
                if (expanded) {
                    uiStrings.getString(
                        LocalizedR.string.ui_texto_afba24
                    )
                } else {
                    uiStrings.getString(LocalizedR.string.ui_texto_a318c2)
                },
                modifier = Modifier.uiId("tickets.0125")
            )
        }
        if (expanded) content()
        LoteriasCaixaDSHorizontalDivider(modifier = Modifier.uiId("tickets.0126"))
    }
}
