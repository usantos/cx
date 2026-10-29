package br.gov.caixa.loterias.core.designsystem.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountChoice
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountCopy
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountField
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountInlineWarning
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountSaveFooter
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSOptionDialog
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSAccountCheckbox
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

/** Variantes legadas exatas: compara estas antes de alterar um padrão compartilhado. */
@Preview(name = "Paridade: conta e volante", showBackground = true)
@Composable
private fun CaixaDSParityPreview() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CaixaDSAccountCopy(
                uiStrings.getString(LocalizedR.string.ui_dados_pessoais_28bc56),
                18f,
                true,
                modifier = Modifier
                    .uiId("ds.0144")
            )
            CaixaDSAccountField(
                "CEP",
                "",
                {},
                emptyMask = uiStrings.getString(LocalizedR.string.ui_somente_numeros_a4bde0),
                error = true,
                modifier = Modifier.uiId("ds.0145")
            )
            CaixaDSAccountInlineWarning(uiStrings.getString(LocalizedR.string.ui_cep_invalido_8b6688), modifier = Modifier.uiId("ds.0146"))
            CaixaDSAccountChoice("Masculino", listOf("Masculino" to "Masculino", "Feminino" to "Feminino"), {})
            CaixaDSAccountCheckbox(true, {}, 32.dp)
            CaixaDSBettingButton(
                "Teimosinha",
                {},
                Modifier.fillMaxWidth().height(45.dp),
                CaixaDSColorTokens.White,
                CaixaDSColorTokens.ComboDark
            )
            CaixaDSAccountSaveFooter("Atualizar", {}, enabled = false)
        }
    }
}

@Preview(name = "Paridade: seletor sem escolha", showBackground = true)
@Composable
private fun CaixaDSOptionPreview() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSTheme {
        CaixaDSOptionDialog(
            "TEIMOSINHA",
            listOf(0, 2, 3),
            0,
            label = {
                if (it == 0) {
                    uiStrings.getString(LocalizedR.string.ui_nao_repetir_7c3a4f)
                } else {
                    uiStrings.getString(
                        LocalizedR.string.ui_1_s_concursos_8cb664,
                        it.toString()
                    )
                }
            },
            onSelect = {},
            onDismiss = {}
        )
    }
}
