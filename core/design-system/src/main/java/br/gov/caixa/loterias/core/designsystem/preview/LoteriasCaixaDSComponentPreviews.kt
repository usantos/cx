package br.gov.caixa.loterias.core.designsystem.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSChoiceRow
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSEmptyState
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSLabelledField
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSectionHeader
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSPrimaryButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme

@Preview(showBackground = true)
@Composable
private fun FormPreview() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSTheme {
        Column {
            LoteriasCaixaDSSectionHeader(
                title = uiStrings.getString(LocalizedR.string.ui_dados_da_aposta_848ea6),
                modifier = Modifier
                    .uiId("ds.0139")
            )
            LoteriasCaixaDSLabelledField(
                label = uiStrings.getString(LocalizedR.string.ui_nome_508690),
                value = uiStrings.getString(LocalizedR.string.ui_minha_aposta_bae24c),
                onValueChange = {},
                modifier = Modifier.uiId("ds.0140")
            )
            LoteriasCaixaDSChoiceRow(
                title = uiStrings.getString(LocalizedR.string.ui_salvar_nos_favoritos_3d0e8b),
                selected = true,
                onClick = {},
                modifier = Modifier.uiId("ds.0141")
            )
            LoteriasCaixaDSPrimaryButton(
                text = uiStrings.getString(LocalizedR.string.ui_continuar_63e2bb),
                onClick = {},
                modifier = Modifier
                    .uiId("ds.0142")
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSTheme {
        LoteriasCaixaDSEmptyState(
            title = uiStrings.getString(LocalizedR.string.ui_nenhuma_aposta_f36be8),
            message = uiStrings.getString(LocalizedR.string.ui_suas_apostas_aparecerao_aqui_0b6281),
            modifier = Modifier.uiId("ds.0143")
        )
    }
}
