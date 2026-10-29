package br.gov.caixa.loterias.apostas.navigation.session

import br.gov.caixa.loterias.core.auth.foreground.ForegroundSessionBackgroundState
import br.gov.caixa.loterias.feature.account.navigation.AccountSessionDependencies
import java.time.Clock
import javax.inject.Inject

class AppSessionDependencies @Inject constructor(
    val account: AccountSessionDependencies,
    val foregroundBackground: ForegroundSessionBackgroundState,
    val localClock: Clock,
)
