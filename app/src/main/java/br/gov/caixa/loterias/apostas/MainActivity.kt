package br.gov.caixa.loterias.apostas

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import br.gov.caixa.loterias.apostas.composition.navigation.AppFeatureGraphDependencies
import br.gov.caixa.loterias.apostas.initialization.AppInitializationDependencies
import br.gov.caixa.loterias.apostas.navigation.session.AppSessionDependencies
import br.gov.caixa.loterias.apostas.navigation.shortcuts.LocalResultShortcutRequests
import br.gov.caixa.loterias.apostas.runtime.fixture.LoggedVisualSessionFixture
import br.gov.caixa.loterias.apostas.runtime.host.AppHostRuntime
import br.gov.caixa.loterias.apostas.runtime.session.restartForegroundSession
import br.gov.caixa.loterias.apostas.runtime.shortcuts.ResultShortcutRequests
import br.gov.caixa.loterias.apostas.runtime.shortcuts.reportResultLauncherShortcutUsage
import br.gov.caixa.loterias.apostas.theme.campaign.AppCampaignDependencies
import br.gov.caixa.loterias.core.queue.android.registerPrivateQueueReceiver
import br.gov.caixa.loterias.core.security.lifecycle.NetworkSecurityLifecycleObserver
import br.gov.caixa.loterias.feature.account.navigation.AccountEnvironmentDependencies
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : androidx.activity.ComponentActivity() {
    private val resultShortcuts = ResultShortcutRequests {
        reportResultLauncherShortcutUsage(this, it)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        resultShortcuts.receive(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        resultShortcuts.save(outState)
        super.onSaveInstanceState(outState)
    }

    private fun isQueueReceiver(filter: IntentFilter): Boolean = filter.hasAction("$packageName.com.filabra.adapter.FILABRA_BROADCAST")

    // O fornecedor solicita RECEIVER_EXPORTED. Eventos de admissão devem ter o mesmo UID.
    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter): Intent? = if (isQueueReceiver(filter)) {
        registerPrivateQueueReceiver(baseContext, receiver, filter)
    } else {
        super.registerReceiver(receiver, filter)
    }

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter, flags: Int): Intent? = if (isQueueReceiver(filter)) {
        registerPrivateQueueReceiver(baseContext, receiver, filter)
    } else {
        super.registerReceiver(receiver, filter, flags)
    }

    @Inject lateinit var initialization: AppInitializationDependencies

    @Inject lateinit var featureDependencies: AppFeatureGraphDependencies

    @Inject lateinit var session: AppSessionDependencies

    @Inject lateinit var campaign: AppCampaignDependencies

    @Inject lateinit var environment: AccountEnvironmentDependencies

    @Inject internal lateinit var runtime: AppHostRuntime

    @Inject internal lateinit var visualSessionFixture: LoggedVisualSessionFixture

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            WindowCompat.getInsetsController(window, window.decorView).apply {
                // Cada destino controla a aparência da barra de status (a Home clássica é branca).
                isAppearanceLightNavigationBars = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setOnExitAnimationListener { splash -> splash.remove() }
        super.onCreate(savedInstanceState)
        resultShortcuts.restore(intent, savedInstanceState)
        // Mantém a janela de ponta a ponta estável durante a inicialização. Cada tela
        // desenha o fundo das barras do sistema por meio de CaixaDSStatusBar.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        // O Android 10+ adiciona contraste à barra de navegação clara por padrão.
        // Em produção, mantém esta área branca com ícones escuros, preservando
        // a cor da barra de navegação configurada pelo tema.
        window.isNavigationBarContrastEnforced = false
        lifecycle.addObserver(
            NetworkSecurityLifecycleObserver(initialization.networkSecurity, lifecycleScope, initialization.dispatchers.io)
        )
        val visualTestRoute = intent.getStringExtra("visual_test_route").takeIf { BuildConfig.DEBUG }
        if (visualTestRoute != null) visualSessionFixture.installIfRequested(intent.getBooleanExtra("visual_test_authenticated", false))
        val restartForegroundStartup = { restartForegroundSession(this) }
        setContent {
            CompositionLocalProvider(LocalResultShortcutRequests provides resultShortcuts) {
                br.gov.caixa.loterias.apostas.composition.AppContent(
                    initialization,
                    featureDependencies,
                    session,
                    campaign,
                    environment,
                    this,
                    visualTestRoute,
                    restartForegroundStartup
                )
            }
        }
    }
}
