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
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountChoice
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountCopy
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountField
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountInlineWarning
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountSaveFooter
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSOptionDialog
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSAccountCheckbox
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

/** Variantes legadas exatas: compara estas antes de alterar um padrão compartilhado. */
@Preview(name = "Paridade: conta e volante", showBackground = true)
@Composable
private fun LoteriasCaixaDSParityPreview() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LoteriasCaixaDSAccountCopy(
                uiStrings.getString(LocalizedR.string.ui_dados_pessoais_28bc56),
                ParityCatalogMetrics.SUBTITLE_SIZE_SP,
                true,
                modifier = Modifier
                    .uiId("ds.0144")
            )
            LoteriasCaixaDSAccountField(
                "CEP",
                "",
                {},
                emptyMask = uiStrings.getString(LocalizedR.string.ui_somente_numeros_a4bde0),
                error = true,
                modifier = Modifier.uiId("ds.0145")
            )
            LoteriasCaixaDSAccountInlineWarning(
                uiStrings.getString(LocalizedR.string.ui_cep_invalido_8b6688),
                modifier = Modifier.uiId("ds.0146")
            )
            LoteriasCaixaDSAccountChoice("Masculino", listOf("Masculino" to "Masculino", "Feminino" to "Feminino"), {})
            LoteriasCaixaDSAccountCheckbox(true, {}, 32.dp)
            LoteriasCaixaDSBettingButton(
                "Teimosinha",
                {},
                Modifier.fillMaxWidth().height(45.dp),
                LoteriasCaixaDSColorTokens.White,
                LoteriasCaixaDSColorTokens.ComboDark
            )
            LoteriasCaixaDSAccountSaveFooter("Atualizar", {}, enabled = false)
        }
    }
}

@Preview(name = "Paridade: seletor sem escolha", showBackground = true)
@Composable
private fun LoteriasCaixaDSOptionPreview() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSTheme {
        LoteriasCaixaDSOptionDialog(
            "TEIMOSINHA",
            listOf(0, 2, ParityCatalogMetrics.THIRD_SAMPLE_SELECTION),
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

private object ParityCatalogMetrics {
    const val SUBTITLE_SIZE_SP = 18f
    const val THIRD_SAMPLE_SELECTION = 3
}
