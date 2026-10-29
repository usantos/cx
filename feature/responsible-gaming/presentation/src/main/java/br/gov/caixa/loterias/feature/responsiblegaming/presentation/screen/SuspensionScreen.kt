package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSAttentionIcon
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSPrimaryButton
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSCheckbox
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSRadioButton
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.responsiblegaming.R as LocalizedR
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.ErrorState
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.LegacyTealButton
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.LoadingState
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.canSubmitSuspension
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState

@Composable
internal fun SuspensionScreen(
    state: ResponsibleGamingUiState,
    onAction: (ResponsibleGamingAction) -> Unit,
    onSubmitSuspension: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_suspensao_temporaria_3a6e3c),
        onBack = { onAction(ResponsibleGamingAction.Back) },
        modifier = Modifier.uiId("rg.0064")
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding)
                .background(CaixaDSColorTokens.BolaoGrayStroke).padding(12.dp)
        ) {
            Column(
                Modifier.fillMaxSize().background(CaixaDS.colors.surface).uiId("rg.0065").verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.loading) {
                    LoadingState()
                    return@Column
                }
                if (state.errorMessage != null) {
                    ErrorState(state.errorMessage) { onAction(ResponsibleGamingAction.Retry) }
                    return@Column
                }
                SuspensionDetails(state, onAction, onSubmitSuspension)
            }
        }
    }
}

@Composable
private fun ColumnScope.SuspensionDetails(
    state: ResponsibleGamingUiState,
    onAction: (ResponsibleGamingAction) -> Unit,
    onSubmitSuspension: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.suspension != null) {
        br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(
                LocalizedR.string.ui_voce_foi_deslogado_e_a_suspensao_tem_587684,
                state.suspensionEndsAt
            ),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = { onAction(ResponsibleGamingAction.Complete) }
        )
    }
    CaixaDSAttentionIcon(
        modifier = (Modifier.padding(top = 10.dp).width(72.dp).height(89.dp)).uiId("rg.0066")
    )
    Text(
        uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
        modifier = (Modifier.offset(y = (-35).dp)).uiId("rg.0067"),
        color = CaixaDS.colors.primaryButton,
        fontSize = 18.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )
    if (state.suspensionConfirmation) {
        Text(
            buildAnnotatedString {
                append(uiStrings.getString(LocalizedR.string.ui_a_suspensao_temporaria_de_compras_e_db0df5))
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("${state.selectedSuspensionPeriod} ?")
                }
            },
            modifier = (Modifier.fillMaxWidth().padding(32.dp)).uiId("rg.0068"),
            color = CaixaDS.colors.primaryButton,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
            LegacyTealButton("Desistir", true, { onAction(ResponsibleGamingAction.Back) }, (Modifier.width(140.dp)).uiId("rg.0069"))
            Spacer(Modifier.width(8.dp))
            LegacyTealButton(
                uiStrings.getString(LocalizedR.string.ui_confirmar_suspensao_105a17),
                false,
                onSubmitSuspension,
                (Modifier.weight(1f)).uiId("rg.0070")
            )
        }
        return
    }
    SuspensionNotices()
    SuspensionPeriods(state, onAction)
    SuspensionAcknowledgement(state, onAction)
    // No XML legado, o checkbox fica entre os RadioGroups e o
    // botão ancorado ao rodapé do cartão (margens 16dp/24dp/5dp).
    // Reproduzimos esse intervalo sem empurrar o botão para fora da tela.
    Spacer(Modifier.height(69.dp))
    LegacyTealButton(
        stringResource(LocalizedR.string.format_confirmar),
        false,
        { onAction(ResponsibleGamingAction.ConfirmSuspension) },
        (Modifier.width(130.dp).padding(bottom = 5.dp).offset(y = (-35).dp)).uiId("rg.0071"),
        enabled = canSubmitSuspension(state.selectedSuspensionPeriod, state.suspensionAcknowledged)
    )
}

@Composable
private fun SuspensionNotices() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val notices = listOf(
        uiStrings.getString(LocalizedR.string.ui_1_essa_acao_acarretara_na_impossibil_6c0755),
        uiStrings.getString(LocalizedR.string.ui_1_1_o_periodo_de_suspensao_pode_ser_aba15e),
        uiStrings.getString(LocalizedR.string.ui_1_2_a_suspensao_passa_a_valer_a_part_fa291f),
        uiStrings.getString(LocalizedR.string.ui_1_3_a_suspensao_temporaria_de_compra_2f8348)
    )
    notices.forEachIndexed { index, notice ->
        Text(
            notice,
            modifier = (
                Modifier.fillMaxWidth().padding(top = if (index == 0) 4.dp else 12.dp)
                    .offset(y = (-35).dp)
                ).uiId("rg.0072"),
            color = CaixaDS.colors.primaryButton,
            fontSize = 16.sp,
            lineHeight = 20.sp
        )
    }
    Text(
        uiStrings.getString(LocalizedR.string.ui_selecione_abaixo_o_periodo_de_suspen_bc745b),
        modifier = (Modifier.fillMaxWidth().padding(top = 12.dp).offset(y = (-35).dp)).uiId("rg.0073"),
        color = CaixaDS.colors.primaryButton,
        fontSize = 14.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )
}

@Composable
private fun SuspensionPeriods(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit) {
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
                        CaixaDSRadioButton(
                            selected = state.selectedSuspensionPeriod == option,
                            onClick = null,
                            modifier = Modifier.uiId("rg.0075")
                        )
                        Text(
                            option,
                            color = CaixaDS.colors.primaryButton,
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
private fun SuspensionAcknowledgement(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit) {
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
        CaixaDSCheckbox(
            checked = state.suspensionAcknowledged,
            onClick = null,
            uncheckedColor = CaixaDSColorTokens.SuspensionCheckboxUnchecked,
            modifier = Modifier.uiId("rg.0078")
        )
        Spacer(Modifier.width(4.dp))
        Text(
            uiStrings.getString(LocalizedR.string.ui_estou_ciente_e_concordo_com_as_infor_97e8b0),
            color = CaixaDS.colors.primaryButton,
            fontSize = 14.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.uiId("rg.0079")
        )
    }
}
