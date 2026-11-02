package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSCheckbox
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSRadioButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.responsiblegaming.R as LocalizedR
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState

@Composable
internal fun SuspensionPeriods(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(start = 3.dp, top = 4.dp).offset(y = (-39).dp).selectableGroup()) {
        listOf(listOf("24 horas", "48 horas", "7 dias"), listOf("15 dias", "30 dias")).forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { option ->
                    Row(
                        Modifier.width(120.dp).heightIn(min = 48.dp).uiId("rg.0074").selectable(
                            selected = state.selectedSuspensionPeriod == option,
                            role = Role.RadioButton,
                            onClick = { onAction(ResponsibleGamingAction.SelectSuspensionPeriod(option)) }
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LoteriasCaixaDSRadioButton(
                            selected = state.selectedSuspensionPeriod == option,
                            onClick = null,
                            modifier = Modifier.uiId("rg.0075")
                        )
                        Text(
                            option,
                            color = LoteriasCaixaDS.colors.primaryButton,
                            fontSize = 13.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            modifier = Modifier.uiId("rg.0076")
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun SuspensionAcknowledgement(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(top = 8.dp).offset(y = (-6).dp)
            .uiId("rg.0077").toggleable(
                value = state.suspensionAcknowledged,
                role = Role.Checkbox,
                onValueChange = { onAction(ResponsibleGamingAction.ToggleSuspensionAcknowledgement) }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LoteriasCaixaDSCheckbox(
            checked = state.suspensionAcknowledged,
            onClick = null,
            uncheckedColor = LoteriasCaixaDSColorTokens.SuspensionCheckboxUnchecked,
            modifier = Modifier.uiId("rg.0078")
        )
        Spacer(Modifier.width(4.dp))
        Text(
            uiStrings.getString(LocalizedR.string.ui_estou_ciente_e_concordo_com_as_infor_97e8b0),
            color = LoteriasCaixaDS.colors.primaryButton,
            fontSize = 14.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.uiId("rg.0079")
        )
    }
}
