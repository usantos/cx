package br.gov.caixa.loterias.core.navigation.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters

/** Dados entre destinos; o estado das telas das features permanece nos ViewModels pertencentes aos destinos. */
class AppNavigationState(
    val details: LoggedDetailContext,
    preparedBettingParametersState: MutableState<BettingParameters?>,
    marketplaceRevisionState: MutableState<Int>,
    requestedHomeModalityIdState: MutableState<Int?>,
) {
    var preparedBettingParameters by preparedBettingParametersState
    var marketplaceRevision by marketplaceRevisionState
    var requestedHomeModalityId by requestedHomeModalityIdState
}

@Composable
fun rememberAppNavigationState(ownerKey: String?): AppNavigationState = AppNavigationState(
    details = rememberLoggedDetailContext(ownerKey),
    preparedBettingParametersState = remember(ownerKey) { mutableStateOf(null) },
    marketplaceRevisionState = rememberSaveable(ownerKey) { mutableStateOf(0) },
    requestedHomeModalityIdState = remember { mutableStateOf(null) },
)
