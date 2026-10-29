package br.gov.caixa.loterias.feature.responsiblegaming.presentation.route

import br.gov.caixa.loterias.feature.responsiblegaming.R as LocalizedR

import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSProvideTextStyle as ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.AssessmentFormScreen
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.AssessmentStartScreen
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingEffect
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.viewmodel.ResponsibleGamingViewModel
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.SocialTransfersScreen
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.SuspensionScreen

@Composable
fun ResponsibleGamingRoute(
    route: String,
    viewModel: ResponsibleGamingViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onOpenTerms: () -> Unit,
    onSubmitSuspension: (() -> Unit)? = null,
    privateDataEnabled: Boolean = true,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ResponsibleGamingEffect.NavigateBack -> onBack()
                ResponsibleGamingEffect.Completed -> onComplete()
            }
        }
    }
    ProvideTextStyle(
        TextStyle(
            fontFamily = CaixaDSFontFamily,
            fontSize = 16.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    ) {
        when (route) {
            "responsible/start" -> AssessmentStartScreen(state, viewModel::onAction)

            "responsible/form" -> AssessmentFormScreen(state, viewModel::onAction, privateDataEnabled)

            "responsible/suspension" -> SuspensionScreen(
                state,
                viewModel::onAction,
                onSubmitSuspension ?: { viewModel.onAction(ResponsibleGamingAction.SubmitSuspension) }
            )

            "responsible/social" -> SocialTransfersScreen(state, viewModel::onAction, onOpenTerms)

            else -> error(uiStrings.getString(LocalizedR.string.ui_rota_de_jogo_responsavel_sem_layout_a0684c, route))
        }
    }
}
