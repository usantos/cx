package br.gov.caixa.loterias.feature.account.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSDropdownMenu as DropdownMenu
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSDropdownMenuItem as DropdownMenuItem
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment
import br.gov.caixa.loterias.feature.account.R as LocalizedR
import br.gov.caixa.loterias.feature.account.presentation.state.EnvironmentUiState

@Composable
fun EnvironmentPicker(
    selected: TestEnvironment,
    state: EnvironmentUiState,
    enabled: Boolean,
    onSelect: (TestEnvironment) -> Unit,
    onRetry: () -> Unit,
    onClose: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(
            onClick = { expanded = true },
            enabled = enabled && !state.saving && state.pending == null,
            modifier = (Modifier.height(48.dp).legacyAutomationId("environment-picker")).uiId("account.0001")
        ) {
            Text(
                (state.pending?.name ?: selected.name) + uiStrings.getString(LocalizedR.string.ui_texto_da106a),
                color = CaixaDS.colors.onBrand,
                modifier = Modifier.uiId("account.0002")
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.uiId("account.0003")) {
            TestEnvironment.entries.forEach { environment ->
                DropdownMenuItem(text = { Text(environment.name, modifier = Modifier.uiId("account.0004")) }, onClick = {
                    expanded = false
                    if (environment != selected) onSelect(environment)
                }, modifier = Modifier.uiId("account.0005"))
            }
        }
    }
    if (state.pending != null && !state.saving && (state.persisted || state.failed)) {
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_mudanca_de_ambiente_83353f),
            message = if (state.failed) {
                uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_salvar_a_mudanca_de_a13520)
            } else {
                uiStrings.getString(LocalizedR.string.ui_o_aplicativo_sera_encerrado_para_apl_9d8ad5)
            },
            positiveLabel = if (state.failed) {
                uiStrings.getString(
                    LocalizedR.string.ui_tentar_novamente_b9e106
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_entendi_9f9373)
            },
            onPositive = {
                if (state.failed) {
                    onRetry()
                } else if (state.persisted) {
                    onClose()
                }
            }
        )
    }
}
