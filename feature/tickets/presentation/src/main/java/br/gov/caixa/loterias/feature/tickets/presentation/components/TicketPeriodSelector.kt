package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSDropdownMenu as DropdownMenu
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSDropdownMenuItem as DropdownMenuItem
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.R

/** spinner_date/content_lista_compras; a seleção mantém os callbacks existentes do ViewModel. */
@Composable
internal fun LegacyTicketPeriodSelector(
    label: String,
    value: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val largeFont = androidx.compose.ui.platform.LocalDensity.current.fontScale > 1f
    Column(modifier.fillMaxWidth().then(if (largeFont) Modifier.heightIn(min = 48.dp) else Modifier.height(48.dp))) {
        Text(
            label,
            color = LoteriasCaixaDSColorTokens.TicketPeriodLabel,
            style = ticketListText(12.sp),
            modifier = Modifier.uiId("tickets.0072")
        )
        Box(
            Modifier.then(if (largeFont) Modifier else Modifier.weight(1f)).fillMaxWidth()
                .uiId("tickets.0073").clickable { expanded = true }
        ) {
            Row(if (largeFont) Modifier.fillMaxWidth() else Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    value,
                    (Modifier.weight(1f)).uiId("tickets.0074"),
                    color = LoteriasCaixaDSColorTokens.TicketPeriodSelection,
                    style = ticketListText(16.sp)
                )
                Image(painterResource(R.drawable.legacy_tickets_combo), null)
            }
            DropdownMenu(expanded, onDismissRequest = { expanded = false }, modifier = Modifier.uiId("tickets.0075")) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                style = ticketListText(16.sp),
                                color = ListTextColor,
                                modifier = Modifier
                                    .uiId("tickets.0076")
                            )
                        },
                        onClick = {
                            expanded = false
                            onSelect(index)
                        },
                        modifier = Modifier.uiId("tickets.0077")
                    )
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(LoteriasCaixaDSColorTokens.TicketPeriodDivider))
    }
}
