package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSRadioButton as RadioButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption

@Composable
internal fun FilterOptions(title: String, options: List<TicketFilterOption>, selected: Long, onSelect: (Long) -> Unit) {
    if (options.isEmpty()) return
    FilterSection(title) {
        options.forEach { option ->
            FilterRadio(option.description, selected == option.value) { onSelect(option.value) }
        }
    }
}

@Composable
internal fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        if (title.isNotEmpty()) {
            Text(
                title,
                color = CaixaDS.colors.brand,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .uiId("tickets.0133")
            )
        }
        content()
    }
}

@Composable
internal fun FilterRadio(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().uiId("tickets.0134").clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick, modifier = Modifier.uiId("tickets.0135"))
        Text(label, modifier = Modifier.uiId("tickets.0136"))
    }
}

@Composable
internal fun FilterCheck(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().uiId("tickets.0137").clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = selected, onCheckedChange = { onClick() }, modifier = Modifier.uiId("tickets.0138"))
        Text(label, modifier = Modifier.uiId("tickets.0139"))
    }
}
