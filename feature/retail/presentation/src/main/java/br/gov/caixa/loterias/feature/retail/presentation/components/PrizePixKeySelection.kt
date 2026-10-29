package br.gov.caixa.loterias.feature.retail.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.mapper.isValidPixKey
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixActions
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixKeySource
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState

@Composable
internal fun PrizePixKeyChoice(state: PrizeRedemptionUiState, actions: PrizePixActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).padding(top = 8.dp)) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_escolha_a_chave_pix_que_deseja_receb_ad328d),
            (Modifier.padding(24.dp)).uiId("retail.0020"),
            color = CaixaDS.colors.brand,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Row(Modifier.fillMaxWidth().padding(top = 16.dp).height(56.dp)) {
            PrizePixTab("CPF", state.pixKeySource == PrizePixKeySource.CPF, actions.selectCpfPixKey, Modifier.weight(1f))
            PrizePixTab(
                uiStrings.getString(LocalizedR.string.ui_outra_chave_8d9935),
                state.pixKeySource == PrizePixKeySource.OTHER,
                actions.selectOtherPixKey,
                Modifier.weight(1f)
            )
        }
        Text(
            uiStrings.getString(LocalizedR.string.ui_chave_01966c),
            (Modifier.padding(24.dp)).uiId("retail.0021"),
            color = CaixaDSColorTokens.PrizeText,
            fontWeight = FontWeight.Bold
        )
        if (state.pixKeySource == PrizePixKeySource.CPF) {
            Text(state.cpf, (Modifier.padding(start = 24.dp, top = 16.dp)).uiId("retail.0022"), fontWeight = FontWeight.Bold)
        } else {
            Column(Modifier.padding(start = 24.dp, end = 24.dp)) {
                PrizePixOtherKeyField(
                    value = state.pixKey,
                    onValueChange = actions.changePixKey,
                    error = state.pixKey.isNotBlank() && !isValidPixKey(state.pixKey)
                )
                Row(
                    Modifier.fillMaxWidth().padding(
                        top = 16.dp
                    ).uiId("retail.0023").toggleable(value = state.pixOwnershipAccepted, role = Role.Checkbox, onValueChange = {
                        actions.togglePixOwnership()
                    }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(state.pixOwnershipAccepted, onCheckedChange = null, modifier = Modifier.uiId("retail.0024"))
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_estou_ciente_que_o_resgate_de_premio_7aa71e),
                        (Modifier.weight(1f)).uiId("retail.0025"),
                        color = CaixaDS.colors.brand
                    )
                }
            }
        }
    }
}
