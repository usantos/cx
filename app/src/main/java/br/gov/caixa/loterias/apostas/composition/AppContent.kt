package br.gov.caixa.loterias.apostas.composition

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.apostas.composition.navigation.AppFeatureGraphDependencies
import br.gov.caixa.loterias.apostas.initialization.AppInitializationDependencies
import br.gov.caixa.loterias.apostas.initialization.startup.FilaBraAppGate
import br.gov.caixa.loterias.apostas.navigation.AppNavHost
import br.gov.caixa.loterias.apostas.navigation.session.AppSessionDependencies
import br.gov.caixa.loterias.apostas.theme.campaign.AppCampaignDependencies
import br.gov.caixa.loterias.apostas.theme.campaign.ApplicationCampaignTheme
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBar
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.navigation.AccountEnvironmentDependencies

@Composable
internal fun AppContent(
    initialization: AppInitializationDependencies,
    featureDependencies: AppFeatureGraphDependencies,
    session: AppSessionDependencies,
    campaign: AppCampaignDependencies,
    environment: AccountEnvironmentDependencies,
    activity: Activity,
    visualTestRoute: String?,
    restartForegroundStartup: () -> Unit,
) {
    // O aplicativo legado possui identidade exclusivamente clara. Manter a paleta
    // estável também evita que cards brancos recebam textos do tema escuro do sistema.
    CaixaDSTheme(darkTheme = false) {
        ApplicationCampaignTheme(campaign) {
            CaixaDSStatusBar {
                val recoveryViewModel = viewModel(key = "session-cleanup-recovery") {
                    br.gov.caixa.loterias.feature.account.presentation.viewmodel.SessionRecoveryViewModel(
                        session.account.resumeLocalSessionCleanup,
                        session.account.authentication,
                        session.account.dispatchers
                    )
                }
                br.gov.caixa.loterias.apostas.composition.startup.ForceUpdateGate(activity) {
                    br.gov.caixa.loterias.feature.account.presentation.components.SessionRecoveryGate(
                        recoveryViewModel,
                        loading = {
                            br.gov.caixa.loterias.feature.onboarding.presentation.screen.SplashScreen(
                                modifier = Modifier
                                    .uiId("app.0001")
                            )
                        }
                    ) {
                        if (visualTestRoute != null) {
                            AppNavHost(
                                featureDependencies,
                                session,
                                visualTestRoute,
                                onForegroundSessionRestart = restartForegroundStartup
                            )
                        } else {
                            FilaBraAppGate(
                                activity,
                                initialization,
                                featureDependencies,
                                session,
                                environment,
                                onForegroundSessionRestart = restartForegroundStartup
                            )
                        }
                    }
                }
            }
        }
    }
}
