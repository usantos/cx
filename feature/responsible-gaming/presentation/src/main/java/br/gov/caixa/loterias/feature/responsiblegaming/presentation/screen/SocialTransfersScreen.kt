package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.responsiblegaming.R as LocalizedR
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.ErrorState
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.LoadingState
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState

@Composable
internal fun SocialTransfersScreen(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit, onOpenTerms: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_repasses_sociais_f44285),
        onBack = { onAction(ResponsibleGamingAction.Back) },
        gradientTopBar = false,
        topBarEndColor = LoteriasCaixaDSColorTokens.Blue700,

        topBarActionWidth = 65.dp,
        topBarAction = {
            Image(
                painter = painterResource(R.drawable.icon_duvida2),
                contentDescription = uiStrings.getString(LocalizedR.string.ui_duvidas_10e026),
                modifier = Modifier.size(25.dp).uiId("rg.0048").clickable(onClick = onOpenTerms),
                contentScale = ContentScale.Fit
            )
        },
        modifier = Modifier.uiId("rg.0049")
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding)) { LoadingState() }

            state.errorMessage != null -> Box(Modifier.fillMaxSize().padding(padding)) {
                ErrorState(state.errorMessage) { onAction(ResponsibleGamingAction.Retry) }
            }

            else -> SocialTransfersContent(state, padding) {
                onAction(ResponsibleGamingAction.Back)
            }
        }
    }
}
