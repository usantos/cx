package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.responsiblegaming.R as LocalizedR
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.ErrorState
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.LoadingState
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState
import br.gov.caixa.loterias.feature.responsiblegaming.ui.R as DeliveryR

@Composable
internal fun DeliveredSuspensionScreen(
    state: ResponsibleGamingUiState,
    onAction: (ResponsibleGamingAction) -> Unit,
    onSubmitSuspension: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val errorMessage = state.errorMessage
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(DeliveryR.string.ui_suspensao_temporaria_3a6e3c),
        onBack = { onAction(ResponsibleGamingAction.Back) },
        modifier = Modifier.uiId("rg.0064")
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding)
                .background(LoteriasCaixaDSColorTokens.BolaoGrayStroke).padding(12.dp)
        ) {
            Column(
                Modifier.fillMaxSize().background(LoteriasCaixaDS.colors.surface).uiId("rg.0065").verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.loading) {
                    LoadingState()
                    return@Column
                }
                if (errorMessage != null) {
                    ErrorState(errorMessage) { onAction(ResponsibleGamingAction.Retry) }
                    return@Column
                }
                SuspensionDetails(state, onAction, onSubmitSuspension)
            }
        }
    }
}
