package br.gov.caixa.loterias.apostas

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : androidx.activity.ComponentActivity() {
    @Inject lateinit var container: AppContainer

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // Android 10+ adiciona contraste à barra clara de navegação por padrão.
        // A produção mantém esta área branca com ícones escuros; preserve a cor
        // navigationBarColor configurada pelo tema.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        lifecycle.addObserver(NetworkSecurityLifecycleObserver(container.networkSecurity, lifecycleScope, container.dispatchers.io))
        val queueLocation = intent.getStringExtra("location")
        val visualTestRoute = intent.getStringExtra("visual_test_route").takeIf { BuildConfig.DEBUG }
        if (visualTestRoute != null) container.prepareLoggedVisualFixture(intent.getBooleanExtra("visual_test_authenticated", false))
        val restartForegroundStartup = { restartForegroundSession(this) }
        setContent {
            // O aplicativo legado possui identidade exclusivamente clara. Manter a paleta
            // estável também evita que cards brancos recebam textos do tema escuro do sistema.
            CaixaDSTheme(darkTheme = false) {
                val recoveryViewModel = viewModel(key = "session-cleanup-recovery") {
                    br.gov.caixa.loterias.feature.account.SessionRecoveryViewModel(
                        container.resumeLocalSessionCleanup, container.authentication, container.dispatchers,
                    )
                }
                br.gov.caixa.loterias.feature.account.SessionRecoveryGate(
                    recoveryViewModel,
                    loading = { br.gov.caixa.loterias.feature.onboarding.LegacySplashScreen() },
                ) {
                    if (visualTestRoute != null) {
                        LoteriasApp(container, visualTestRoute, onForegroundSessionRestart = restartForegroundStartup)
                    } else if (queueLocation == null) {
                        FilaBraAppGate(this, container, onForegroundSessionRestart = restartForegroundStartup)
                    } else {
                        FilaBraQueueScreen(queueLocation, this)
                    }
                }
            }
        }
    }
}
