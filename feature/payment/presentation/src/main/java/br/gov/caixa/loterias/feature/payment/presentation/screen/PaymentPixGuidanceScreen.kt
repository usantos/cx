package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.presentation.mapper.legacyPaymentTextStyle
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentTerminalUiState

private val pixInstructions = listOf(
    messageText("copie_o_codigo_gerado_e_pague_de_28e040"),
    messageText("entre_no_aplicativo_da_sua_insti_f16eb7"),
    messageText("escolha_a_opcao_pix_copia_e_cola_61c86b"),
    messageText("cole_o_codigo_pix_14fe16"),
    messageText("confirme_as_informacoes_e_realiz_bceb56")
)

@Composable
internal fun PixGuidanceScreen(
    spec: WorkflowSpec,
    state: PaymentTerminalUiState,
    onBack: () -> Unit,
    onTogglePixTerms: () -> Unit,
    onSubmit: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(title = spec.title, onBack = onBack, modifier = Modifier.uiId("pay.0102")) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).background(CaixaDS.colors.surface)
                .uiId("pay.0103").verticalScroll(rememberScrollState()).padding(top = 16.dp, bottom = 64.dp)
        ) {
            Text(
                spec.description,
                (Modifier.fillMaxWidth().padding(start = 17.dp, top = 9.dp, end = 17.dp, bottom = 8.dp)).uiId("pay.0104"),
                color = CaixaDS.colors.cartDark,
                fontSize = 14.sp,
                style = legacyPaymentTextStyle
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_como_pagar_com_pix_47fd8f),
                (Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)).uiId("pay.0105"),
                color = CaixaDS.colors.cartDark,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                style = legacyPaymentTextStyle
            )
            PixInstructionRows()
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 16.dp)
                    .uiId("pay.0108").toggleable(
                        value = state.pixTermsAccepted,
                        role = Role.Checkbox,
                        onValueChange = { onTogglePixTerms() }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(state.pixTermsAccepted, onCheckedChange = null, modifier = Modifier.uiId("pay.0109"))
                Text(
                    uiStrings.getString(LocalizedR.string.ui_estou_ciente_que_somente_sera_aceito_3c9f45),
                    color = CaixaDS.colors.cartDark,
                    fontSize = 14.sp,
                    style = legacyPaymentTextStyle,
                    lineHeight = 18.sp,
                    modifier = Modifier.uiId("pay.0110")
                )
            }
            Button(
                onClick = onSubmit,
                enabled = state.pixTermsAccepted,
                modifier = (
                    Modifier.padding(top = 16.dp).width(177.dp).height(48.dp)
                        .align(Alignment.CenterHorizontally)
                    ).uiId("pay.0111"),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CaixaDS.colors.pix,
                    contentColor = CaixaDSColorTokens.White,
                    disabledContainerColor = CaixaDS.colors.pixDisabled,
                    disabledContentColor = CaixaDSColorTokens.White
                )
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_pagar_agora_80942b),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    style = legacyPaymentTextStyle,
                    modifier = Modifier.uiId("pay.0112")
                )
            }
        }
    }
}

@Composable
private fun PixInstructionRows() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    pixInstructions.forEachIndexed { index, instruction ->
        Row(Modifier.fillMaxWidth().padding(start = 24.dp)) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_1_s_65e020, (index + 1).toString()),
                fontSize = 14.sp,
                style = legacyPaymentTextStyle,
                color = CaixaDS.colors.cartDark,
                modifier = Modifier.uiId("pay.0106")
            )
            Text(
                instruction,
                fontSize = 14.sp,
                style = legacyPaymentTextStyle,
                color = CaixaDS.colors.cartDark,
                modifier = Modifier.uiId("pay.0107")
            )
        }
    }
}
