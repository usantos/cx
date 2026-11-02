package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.responsiblegaming.R as LocalizedR
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.ErrorState
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
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_autoavaliacao_para_o_apostador_89e9ed),
        onBack = null,
        modifier = Modifier.uiId("rg.0019")
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).background(LoteriasCaixaDSColorTokens.BolaoGrayStroke)) {
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
        Modifier.fillMaxSize().padding(12.dp).background(LoteriasCaixaDSColorTokens.MaterialCard),
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
                color = LoteriasCaixaDS.colors.primaryButton,
                fontSize = 16.sp
            )
            AssessmentAnswers(state, onAction)
        }
        AssessmentNavigation(state, onAction, onHelp = { showHelp = true })
    }
}
