package br.gov.caixa.loterias.apostas.initialization.startup

import android.app.Activity
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.apostas.initialization.AppInitializationDependencies
import br.gov.caixa.loterias.apostas.composition.navigation.AppFeatureGraphDependencies
import br.gov.caixa.loterias.apostas.navigation.session.AppSessionDependencies
import br.gov.caixa.loterias.feature.account.navigation.AccountEnvironmentDependencies
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.apostas.runtime.session.restartForegroundSession

@Composable
fun FilaBraAppGate(activity: Activity, initialization: AppInitializationDependencies,
    featureDependencies: AppFeatureGraphDependencies,
    session: AppSessionDependencies,
    environment: AccountEnvironmentDependencies,
    onForegroundSessionRestart: () -> Unit = { restartForegroundSession(activity) },
) {
    FilaBraInitialization(initialization) { minimumSplashElapsed ->
        if (BuildConfig.ENABLE_MOCK_BACKEND) {
            MockBackendAppGate(
                initialization,
                featureDependencies,
                session,
                minimumSplashElapsed,
                onForegroundSessionRestart
            )
        } else {
            FilaBraQueueGate(
                activity,
                initialization,
                featureDependencies,
                session,
                environment,
                minimumSplashElapsed,
                onForegroundSessionRestart
            )
        }
    }
}
