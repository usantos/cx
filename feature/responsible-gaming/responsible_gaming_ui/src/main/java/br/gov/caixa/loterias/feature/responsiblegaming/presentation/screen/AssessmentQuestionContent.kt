package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSSelfAssessmentIcon
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSRadioButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.responsiblegaming.R as LocalizedR
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.LegacyTealButton
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState
import br.gov.caixa.loterias.feature.responsiblegaming.ui.R as DeliveryR

@Composable
internal fun ColumnScope.AssessmentIntroduction(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit) {
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
                uiStrings.getString(DeliveryR.string.ui_fechar_0f2bd8),
                modifier = (if (largeFont) Modifier.width(IntrinsicSize.Min) else Modifier).uiId("rg.0023"),
                color = LoteriasCaixaDS.colors.teal,
                fontSize = 17.sp
            )
            androidx.compose.foundation.Image(
                androidx.compose.ui.res.painterResource(br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_assessment_close),
                contentDescription = null,
                modifier = Modifier.padding(start = 6.dp).size(30.dp)
            )
        }
    }
    LoteriasCaixaDSSelfAssessmentIcon(
        (
            Modifier.align(Alignment.CenterHorizontally)
                .padding(top = if (state.assessmentRequired) 35.dp else 15.dp)
                .width(66.dp).height(69.dp)
            ).uiId("rg.0024")
    )
    Text(
        uiStrings.getString(DeliveryR.string.ui_este_app_e_valido_para_qualquer_tipo_2fe20c) +
            uiStrings.getString(DeliveryR.string.ui_os_criterios_de_avaliacao_breve_da_p_024a1f),
        modifier = (Modifier.padding(top = 35.dp)).uiId("rg.0025"),
        color = LoteriasCaixaDS.colors.primaryButton,
        fontSize = 16.sp
    )
    Text(
        uiStrings.getString(DeliveryR.string.ui_nods_clip_problem_gambling_screen_11c093),
        color = LoteriasCaixaDS.colors.primaryButton,
        fontSize = 16.sp,
        modifier = Modifier.uiId("rg.0026")
    )
    Text(
        uiStrings.getString(DeliveryR.string.ui_volberg_r_a_munck_i_m_petry_n_m_2011_5961ba) +
            uiStrings.getString(DeliveryR.string.ui_screening_method_for_pathological_an_2545d7) +
            uiStrings.getString(DeliveryR.string.ui_the_american_journal_on_addictions_2_96e793),
        color = LoteriasCaixaDS.colors.primaryButton,
        fontSize = 12.sp,
        modifier = Modifier.uiId("rg.0027")
    )
}

@Composable
internal fun ColumnScope.AssessmentAnswers(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (!state.assessmentSubmitted && state.currentQuestion != null) {
        Row(
            Modifier.align(Alignment.CenterHorizontally).padding(top = 40.dp).selectableGroup(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                true to stringResource(DeliveryR.string.format_sim),
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
                    LoteriasCaixaDSRadioButton(
                        selected = state.currentAnswer == affirmative,
                        onClick = null,
                        modifier = Modifier.uiId("rg.0029")
                    )
                    Text(
                        option,
                        (Modifier.padding(start = 4.dp)).uiId("rg.0030"),
                        color = LoteriasCaixaDS.colors.primaryButton,
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
internal fun AssessmentNavigation(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit, onHelp: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().padding(12.dp)) {
        if (state.assessmentSubmitted) {
            LegacyTealButton(
                stringResource(DeliveryR.string.ui_fechar_0f2bd8),
                true,
                { onAction(ResponsibleGamingAction.Complete) },
                (Modifier.width(150.dp)).uiId("rg.0031")
            )
            Spacer(Modifier.weight(1f))
            if (state.hasAffirmativeAnswer) {
                LegacyTealButton(
                    stringResource(DeliveryR.string.format_ajuda),
                    false,
                    onHelp,
                    (Modifier.width(150.dp)).uiId("rg.0032")
                )
            }
        } else {
            LegacyTealButton(
                stringResource(DeliveryR.string.format_anterior),
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
                    stringResource(DeliveryR.string.format_concluido)
                } else {
                    uiStrings.getString(DeliveryR.string.ui_proximo_4a1772)
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
