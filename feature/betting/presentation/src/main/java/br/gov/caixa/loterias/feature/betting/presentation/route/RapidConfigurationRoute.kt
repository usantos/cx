package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.ui.res.stringResource

import br.gov.caixa.loterias.feature.betting.R as LocalizedR

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.feature.betting.presentation.screen.RapidConfigurationActions
import br.gov.caixa.loterias.feature.betting.presentation.screen.RapidConfigurationContent
import br.gov.caixa.loterias.feature.betting.presentation.components.RAPID_PAGE_COUNT
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidPagerSynchronization
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.RapidConfigurationViewModel

@Composable
fun RapidConfigurationRoute(
    viewModel: RapidConfigurationViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onServiceRedirect: (br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) -> Unit = {},
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    br.gov.caixa.loterias.core.presentation.components.ServiceFailureOverlay(
        state.serviceFailure,
        viewModel::acceptServiceNotice,
        viewModel::dismissServiceNotice,
        viewModel::consumeServiceRedirect,
        onServiceRedirect
    )
    val pager = rememberPagerState(initialPage = state.page, pageCount = { RAPID_PAGE_COUNT })
    var summaryVisited by remember { mutableStateOf(false) }
    LaunchedEffect(state.page) { if (state.page == RAPID_PAGE_COUNT - 1) summaryVisited = true }
    RapidPagerSynchronization(pager, state.page, viewModel::setPage)
    LaunchedEffect(state.saved) { if (state.saved) onSaved() }

    RapidConfigurationContent(
        state = state,
        pager = pager,
        summaryVisited = summaryVisited,
        onBack = onBack,
        actions = RapidConfigurationActions(
            setPage = viewModel::setPage,
            setMinimumPrize = viewModel::setMinimumPrize,
            setMinimumAmount = viewModel::setMinimumAmount,
            setMaximumAmount = viewModel::setMaximumAmount,
            setCustomAmount = viewModel::setCustomAmount,
            loadAmountLimits = { viewModel.loadAmountLimits() },
            toggleModality = viewModel::toggleModality,
            toggleRequiredNumber = viewModel::toggleRequiredNumber,
            clearRequiredNumbers = viewModel::clearRequiredNumbers,
            toggleExcludedNumber = viewModel::toggleExcludedNumber,
            clearExcludedNumbers = viewModel::clearExcludedNumbers,
            save = viewModel::save
        )
    )
    state.warningMessage?.let {
        CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissMessage
        )
    }
    state.errorMessage?.let {
        CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissMessage
        )
    }
}
