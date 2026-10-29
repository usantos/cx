package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSSelfAssessmentIcon
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSRadioButton
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.responsiblegaming.R as LocalizedR
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.ErrorState
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.LegacyTealButton
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.LoadingState
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState

@Composable
internal fun AssessmentFormScreen(
    state: ResponsibleGamingUiState,
    onAction: (ResponsibleGamingAction) -> Unit,
    privateDataEnabled: Boolean = true,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_autoavaliacao_para_o_apostador_89e9ed),
        onBack = null,
        modifier = Modifier.uiId("rg.0019")
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.BolaoGrayStroke)) {
            when {
                state.loading -> LoadingState()

                state.errorMessage != null -> ErrorState(state.errorMessage) { onAction(ResponsibleGamingAction.Retry) }

                privateDataEnabled && state.currentQuestion == null -> ErrorState(
                    uiStrings.getString(LocalizedR.string.ui_autoavaliacao_sem_perguntas_3bbd1f)
                ) { onAction(ResponsibleGamingAction.Retry) }

                else -> AssessmentQuestionState(state, onAction)
            }
        }
    }
}

@Composable
private fun AssessmentQuestionState(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var showHelp by remember { mutableStateOf(false) }
    if (showHelp) AssessmentHelpSheet(onDismiss = { showHelp = false })
    Column(
        // O CardView original herda o fundo Material #ECECEC; usar
        // branco aqui clareava o formulário inteiro e reduzia a paridade visual.
        Modifier.fillMaxSize().padding(12.dp).background(CaixaDSColorTokens.MaterialCard),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.fillMaxWidth().weight(1f).uiId("rg.0020").verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            AssessmentIntroduction(state, onAction)
            Text(
                if (!state.assessmentSubmitted) {
                    state.currentQuestion?.description.orEmpty()
                } else if (state.hasAffirmativeAnswer) {
                    uiStrings.getString(LocalizedR.string.ui_muita_atencao_se_voce_respondeu_sim_c02efa)
                } else {
                    uiStrings.getString(LocalizedR.string.ui_parabens_o_seu_habito_de_apostar_nao_d2eebd)
                },
                modifier = (Modifier.fillMaxWidth().padding(top = 26.dp)).uiId("rg.0021"),
                fontWeight = if (state.assessmentSubmitted) FontWeight.Bold else FontWeight.Normal,
                textAlign = if (state.assessmentSubmitted) TextAlign.Center else TextAlign.Start,
                color = CaixaDS.colors.primaryButton,
                fontSize = 16.sp
            )
            AssessmentAnswers(state, onAction)
        }
        AssessmentNavigation(state, onAction, onHelp = { showHelp = true })
    }
}

@Composable
private fun ColumnScope.AssessmentIntroduction(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (!state.assessmentRequired && !state.assessmentSubmitted) {
        val largeFont = androidx.compose.ui.platform.LocalDensity.current.fontScale > 1f
        Row(
            Modifier.align(
                Alignment.End
            ).then(if (largeFont) Modifier.heightIn(min = 48.dp) else Modifier.height(20.dp)).padding(end = 4.dp)
                .uiId("rg.0022").clickable(role = Role.Button) { onAction(ResponsibleGamingAction.Back) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_fechar_0f2bd8),
                modifier = (if (largeFont) Modifier.width(IntrinsicSize.Min) else Modifier).uiId("rg.0023"),
                color = CaixaDS.colors.teal,
                fontSize = 17.sp
            )
            androidx.compose.foundation.Image(
                androidx.compose.ui.res.painterResource(br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_assessment_close),
                contentDescription = null,
                modifier = Modifier.padding(start = 6.dp).size(30.dp)
            )
        }
    }
    CaixaDSSelfAssessmentIcon(
        (
            Modifier.align(Alignment.CenterHorizontally)
                .padding(top = if (state.assessmentRequired) 35.dp else 15.dp)
                .width(66.dp).height(69.dp)
            ).uiId("rg.0024")
    )
    Text(
        uiStrings.getString(LocalizedR.string.ui_este_app_e_valido_para_qualquer_tipo_2fe20c) +
            uiStrings.getString(LocalizedR.string.ui_os_criterios_de_avaliacao_breve_da_p_024a1f),
        modifier = (Modifier.padding(top = 35.dp)).uiId("rg.0025"),
        color = CaixaDS.colors.primaryButton,
        fontSize = 16.sp
    )
    Text(
        uiStrings.getString(LocalizedR.string.ui_nods_clip_problem_gambling_screen_11c093),
        color = CaixaDS.colors.primaryButton,
        fontSize = 16.sp,
        modifier = Modifier.uiId("rg.0026")
    )
    Text(
        uiStrings.getString(LocalizedR.string.ui_volberg_r_a_munck_i_m_petry_n_m_2011_5961ba) +
            uiStrings.getString(LocalizedR.string.ui_screening_method_for_pathological_an_2545d7) +
            uiStrings.getString(LocalizedR.string.ui_the_american_journal_on_addictions_2_96e793),
        color = CaixaDS.colors.primaryButton,
        fontSize = 12.sp,
        modifier = Modifier.uiId("rg.0027")
    )
}

@Composable
private fun ColumnScope.AssessmentAnswers(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (!state.assessmentSubmitted && state.currentQuestion != null) {
        Row(
            Modifier.align(Alignment.CenterHorizontally).padding(top = 40.dp).selectableGroup(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                true to stringResource(LocalizedR.string.format_sim),
                false to uiStrings.getString(LocalizedR.string.ui_nao_9bc947)
            ).forEach { (
                affirmative,
                option,
            ),
                ->
                Row(
                    Modifier.heightIn(min = 48.dp).uiId("rg.0028").selectable(
                        selected = state.currentAnswer == affirmative,
                        role = Role.RadioButton,
                        onClick = { onAction(ResponsibleGamingAction.Answer(affirmative)) }
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CaixaDSRadioButton(
                        selected = state.currentAnswer == affirmative,
                        onClick = null,
                        modifier = Modifier.uiId("rg.0029")
                    )
                    Text(
                        option,
                        (Modifier.padding(start = 4.dp)).uiId("rg.0030"),
                        color = CaixaDS.colors.primaryButton,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(10.dp))
            }
        }
    }
}

@Composable
private fun AssessmentNavigation(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit, onHelp: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().padding(12.dp)) {
        if (state.assessmentSubmitted) {
            LegacyTealButton(
                stringResource(LocalizedR.string.ui_fechar_0f2bd8),
                true,
                { onAction(ResponsibleGamingAction.Complete) },
                (Modifier.width(150.dp)).uiId("rg.0031")
            )
            Spacer(Modifier.weight(1f))
            if (state.hasAffirmativeAnswer) {
                LegacyTealButton(
                    stringResource(LocalizedR.string.format_ajuda),
                    false,
                    onHelp,
                    (Modifier.width(150.dp)).uiId("rg.0032")
                )
            }
        } else {
            LegacyTealButton(
                stringResource(LocalizedR.string.format_anterior),
                true,
                { onAction(ResponsibleGamingAction.PreviousQuestion) },
                (Modifier.width(150.dp)).uiId("rg.0033"),
                enabled = state.currentQuestionIndex > 0,
                direction = -1
            )
            Spacer(Modifier.weight(1f))
            LegacyTealButton(
                if (
                    state.currentQuestionIndex == state.questions.lastIndex
                ) {
                    stringResource(LocalizedR.string.format_concluido)
                } else {
                    uiStrings.getString(LocalizedR.string.ui_proximo_4a1772)
                },
                state.currentAnswer == null,
                { onAction(ResponsibleGamingAction.NextQuestion) },
                (Modifier.width(150.dp)).uiId("rg.0034"),
                enabled = state.currentAnswer != null,
                direction = 1
            )
        }
    }
}
