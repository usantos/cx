package br.gov.caixa.loterias.apostas

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.webkit.ConsoleMessage
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.core.queue.FilaBraQueueGateway
import br.gov.caixa.loterias.core.queue.QueueConsoleEvent
import br.gov.caixa.loterias.core.queue.QueueConsoleEventParser
import br.gov.caixa.loterias.core.queue.QueueListener
import br.gov.caixa.loterias.core.network.MockBackendScenario
import br.gov.caixa.loterias.feature.onboarding.StartupStatusKind
import br.gov.caixa.loterias.feature.onboarding.StartupViewModel
import br.gov.caixa.loterias.feature.onboarding.LegacySplashScreen
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPrimaryButton
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Box
import com.filabra.adapter.android.FilaBRAAndroidService
import com.kevinnzou.web.AccompanistWebChromeClient
import com.kevinnzou.web.WebView as ComposeWebView
import com.kevinnzou.web.rememberWebViewState
import kotlinx.coroutines.delay

private enum class QueueGateState { WAITING, NO_INTERNET, PASSED }

@Composable
fun FilaBraAppGate(activity: Activity, container: AppContainer) {
    var minimumSplashElapsed by remember { mutableStateOf(false) }
    var networkSecurityReady by remember { mutableStateOf(false) }
    var securityFailed by remember { mutableStateOf(false) }
    var securityAttempt by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        delay(2_000)
        minimumSplashElapsed = true
    }
    LaunchedEffect(container, securityAttempt) {
        securityFailed = false
        val result = container.networkSecurity()
        networkSecurityReady = result is br.gov.caixa.loterias.core.common.AppResult.Success
        securityFailed = !networkSecurityReady
    }
    if (securityFailed || (!networkSecurityReady && securityAttempt > 0)) {
        StartupStatusPanel(
            networkAvailability = container.networkAvailability,
            kind = StartupStatusKind.UNAVAILABLE,
            loading = !securityFailed,
            onRetry = { securityFailed = false; securityAttempt++ },
        )
        return
    }
    if (!networkSecurityReady) {
        LegacySplashScreen()
        return
    }
    if (BuildConfig.ENABLE_MOCK_BACKEND) {
        MockBackendAppGate(container, minimumSplashElapsed)
        return
    }

    var state by remember { mutableStateOf(QueueGateState.WAITING) }
    var attempt by remember { mutableIntStateOf(0) }
    val gateway = remember {
        FilaBraQueueGateway(BuildConfig.FILABRA_ACCOUNT_SYSTEM_NAME, BuildConfig.FILABRA_QUEUE_SYSTEM_NAME)
    }
    val startupViewModel = viewModel {
        StartupViewModel(
            isVersionSupported = container.isVersionSupported,
            getFilterParameters = container.marketplaceFilterParameters,
            versionName = BuildConfig.VERSION_NAME,
            prepareNetworkSecurity = container.networkSecurity,
            dispatchers = container.dispatchers,
        )
    }
    val startupState by startupViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(attempt) {
        state = QueueGateState.WAITING
        gateway.start(activity, activity.javaClass, object : QueueListener {
            override fun onPass(passType: String) {
                state = QueueGateState.PASSED
                startupViewModel.queuePassed()
            }
            override fun onNoInternet() { state = QueueGateState.NO_INTERNET }
            override fun onError(message: String) {
                state = QueueGateState.PASSED
                startupViewModel.queuePassed()
            }
            override fun onShow() = Unit
            override fun onAbandon(cause: String) { if (cause == "Back") activity.finish() }
            override fun onJoin(request: Int) = Unit
        })
    }
    when (state) {
        QueueGateState.PASSED -> StartupGateContent(startupState, startupViewModel::retry, container.networkAvailability) {
            if (minimumSplashElapsed) {
                LoteriasApp(container, startDestination = startupDestination(container.shouldShowIntroduction()))
            } else {
                LegacySplashScreen()
            }
        }
        QueueGateState.WAITING -> if (attempt > 0) {
            StartupStatusPanel(StartupStatusKind.OFFLINE, onRetry = {}, networkAvailability = container.networkAvailability, loading = true)
        } else LegacySplashScreen()
        QueueGateState.NO_INTERNET -> StartupStatusPanel(StartupStatusKind.OFFLINE, onRetry = { attempt++ }, networkAvailability = container.networkAvailability)
    }
}

@Composable
private fun MockBackendAppGate(container: AppContainer, minimumSplashElapsed: Boolean) {
    val scenario = remember { MockBackendScenario.from(BuildConfig.MOCK_BACKEND_SCENARIO) }
    var suspensionAcknowledged by remember { mutableStateOf(false) }
    val startupViewModel = viewModel(key = "mock-startup-${scenario.name}") {
        StartupViewModel(
            isVersionSupported = container.isVersionSupported,
            getFilterParameters = container.marketplaceFilterParameters,
            versionName = BuildConfig.VERSION_NAME,
            prepareNetworkSecurity = container.networkSecurity,
            dispatchers = container.dispatchers,
        )
    }
    val startupState by startupViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(startupViewModel) { startupViewModel.queuePassed() }
    StartupGateContent(startupState, startupViewModel::retry, container.networkAvailability) {
        if (minimumSplashElapsed) {
            if (scenario == MockBackendScenario.ACCOUNT_SUSPENDED && !suspensionAcknowledged) {
                StatusAction(
                    title = "Conta temporariamente suspensa",
                    message = "Sua conta está temporariamente suspensa para compras de apostas digitais.",
                    action = "Entendi",
                    onAction = { suspensionAcknowledged = true },
                )
            } else {
                LoteriasApp(container, startDestination = mockStartDestination(scenario))
            }
        } else {
            LegacySplashScreen()
        }
    }
}

internal fun mockStartDestination(scenario: MockBackendScenario): String = when (scenario) {
    MockBackendScenario.REGISTRATION_REQUIRED -> "account/register"
    MockBackendScenario.TERMS_REQUIRED -> "support/terms-acceptance"
    MockBackendScenario.SELF_ASSESSMENT_REQUIRED -> "responsible/start/required"
    MockBackendScenario.CART_EXPIRED_POOL -> "cart/current"
    MockBackendScenario.PURCHASE_PROCESSING -> "payment/confirmation"
    MockBackendScenario.PURCHASE_REJECTED -> "payment/failure"
    MockBackendScenario.PURCHASE_RESPONSE_LOST,
    MockBackendScenario.PURCHASE_LOGIN_REQUIRED,
    MockBackendScenario.PURCHASE_TERMS_REQUIRED,
    MockBackendScenario.PURCHASE_REGISTRATION_REQUIRED,
    MockBackendScenario.PURCHASE_UNAVAILABLE,
    MockBackendScenario.PURCHASE_HTTP_UNAUTHORIZED,
    MockBackendScenario.PURCHASE_CART_REDIRECT,
    MockBackendScenario.PURCHASE_FAVORITE_CARTS_REDIRECT,
    MockBackendScenario.PURCHASE_MORE_BETS_REQUIRED,
    MockBackendScenario.PURCHASE_BUSINESS_RULE,
    MockBackendScenario.PURCHASE_SERVER_MESSAGE,
    MockBackendScenario.PURCHASE_SERVER_FAILURE,
    MockBackendScenario.PURCHASE_INVALID_IDENTIFICATION,
    MockBackendScenario.PURCHASE_MP_REGISTRATION_REQUIRED,
    MockBackendScenario.PURCHASE_MP_REGISTRATION_TOKEN_FAILURE -> "payment/confirm-card"
    MockBackendScenario.PURCHASE_APPROVED -> "payment/success"
    MockBackendScenario.PAYMENT_NO_SAVED_CARDS -> "payment/method"
    MockBackendScenario.PIX_PENDING -> "payment/pix-copy"
    MockBackendScenario.PIX_EXPIRED -> "payment/pix-expired"
    MockBackendScenario.DAM_BLOCKED -> "payment/method"
    MockBackendScenario.TEIMOSINHA_RESCUED -> "tickets/bet-detail?original=true&resgate=true"
    MockBackendScenario.PRIZE_AVAILABLE -> "results/confirmed-bet"
    MockBackendScenario.PRIZE_MP_REGISTRATION_REQUIRED,
    MockBackendScenario.PRIZE_LOGIN_REQUIRED,
    MockBackendScenario.MP_TOKEN_LOGIN_REQUIRED,
    MockBackendScenario.MP_TOKEN_LOGIN_REDIRECT,
    MockBackendScenario.MP_TOKEN_TERMS_REQUIRED,
    MockBackendScenario.MP_TOKEN_REPEAT_REGISTRATION,
    MockBackendScenario.MP_TOKEN_BUSINESS_RULE,
    MockBackendScenario.MP_TOKEN_UNCHANGED,
    MockBackendScenario.MP_TOKEN_RESPONSE_TIMEOUT,
    MockBackendScenario.MP_TOKEN_CONNECTION_FAILURE,
    MockBackendScenario.PRIZE_RESPONSE_TIMEOUT,
    MockBackendScenario.PRIZE_CONNECTION_FAILURE,
    MockBackendScenario.PRIZE_PROCESSING_REDIRECT,
    MockBackendScenario.PRIZE_LOGIN_REDIRECT,
    MockBackendScenario.PRIZE_TERMS_REQUIRED,
    MockBackendScenario.PRIZE_ACCOUNT_REGISTRATION_REQUIRED,
    MockBackendScenario.PRIZE_UNAVAILABLE,
    MockBackendScenario.PRIZE_CART_REDIRECT,
    MockBackendScenario.PRIZE_FAVORITE_CARTS_REDIRECT,
    MockBackendScenario.PRIZE_MORE_BETS_REQUIRED,
    MockBackendScenario.PRIZE_BUSINESS_RULE,
    MockBackendScenario.PRIZE_SERVER_MESSAGE,
    MockBackendScenario.PRIZE_SERVER_FAILURE,
    MockBackendScenario.PRIZE_INVALID_IDENTIFICATION,
    MockBackendScenario.PRIZE_MP_REGISTRATION_TOKEN_FAILURE -> "retail/prize-mercado-pago"
    else -> "home"
}

internal fun startupDestination(shouldShowIntroduction: Boolean): String =
    if (shouldShowIntroduction) "onboarding/introduction" else "account/login"

@Composable
private fun StatusAction(title: String, message: String, action: String, onAction: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(CaixaDSColorTokens.White).padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(130.dp).border(4.dp, CaixaDS.colors.brand, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = CaixaDSIcons.Forward,
                contentDescription = null,
                modifier = Modifier.size(52.dp).rotate(-90f),
                tint = CaixaDS.colors.brand,
            )
        }
        Text(title, modifier = Modifier.padding(top = 28.dp), color = CaixaDS.colors.brand, style = CaixaDS.typography.headlineSmall, textAlign = TextAlign.Center)
        Text(message, modifier = Modifier.padding(vertical = 16.dp), color = CaixaDS.colors.legacyMutedText, textAlign = TextAlign.Center)
        CaixaDSLegacyPrimaryButton(action, onAction, Modifier.fillMaxWidth())
    }
}

@Composable
private fun GateMessage(message: String) {
    Column(
        Modifier.fillMaxSize().background(CaixaDSColorTokens.White).padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = CaixaDS.colors.brand)
        Text(
            message,
            modifier = Modifier.padding(top = 20.dp),
            color = CaixaDS.colors.legacyMutedText,
            textAlign = TextAlign.Center,
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FilaBraQueueScreen(location: String, activity: Activity) {
    val secureLocation = remember(location) { location.takeIf { it.startsWith("https://") } }
    var pendingPass by remember { mutableStateOf<QueueConsoleEvent.Pass?>(null) }

    fun broadcast(event: QueueConsoleEvent) {
        val intent = Intent().setAction("${activity.packageName}.com.filabra.adapter.FILABRA_BROADCAST")
            .setPackage(activity.packageName)
        when (event) {
            is QueueConsoleEvent.Join -> {
                FilaBRAAndroidService.setPreference(activity, "mostRecentRequestNumber", event.request.toString())
                intent.putExtra("result", "JOIN").putExtra("request", event.request)
            }
            is QueueConsoleEvent.Pass -> intent.putExtra("result", "SUCCESS")
                .putExtra("target", event.target)
                .putExtra("passType", event.passType)
                .putExtra("when", event.delayMillis)
                .putExtra("pl", event.passedLifetime)
            is QueueConsoleEvent.Error -> intent.putExtra("result", "ERROR").putExtra("message", event.message)
            QueueConsoleEvent.Ignore -> return
        }
        activity.sendBroadcast(intent)
    }

    val currentBroadcast = rememberUpdatedState<(QueueConsoleEvent) -> Unit>(::broadcast)
    val currentPendingPassHandler = rememberUpdatedState<(QueueConsoleEvent.Pass) -> Unit> {
        pendingPass = it
    }

    BackHandler {
        activity.sendBroadcast(
            Intent("${activity.packageName}.com.filabra.adapter.FILABRA_BROADCAST")
                .setPackage(activity.packageName)
                .putExtra("result", "ABANDON")
                .putExtra("cause", "Back"),
        )
        activity.finish()
    }

    if (secureLocation == null) {
        LaunchedEffect(Unit) {
            broadcast(QueueConsoleEvent.Error("Endereço inválido para a fila."))
            activity.finish()
        }
        GateMessage("Não foi possível abrir a fila.")
        return
    }

    pendingPass?.let { pass ->
        LaunchedEffect(pass) {
            broadcast(pass)
            delay(pass.delayMillis.coerceAtMost(60_000))
            activity.finish()
        }
    }

    val queueUrl = remember(secureLocation) {
        secureLocation + if ('?' in secureLocation) "&qfnoredirect=true" else "?qfnoredirect=true"
    }
    val webViewState = rememberWebViewState(queueUrl)
    val queueChromeClient = remember {
        object : AccompanistWebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                when (val event = QueueConsoleEventParser.parse(consoleMessage.message())) {
                    is QueueConsoleEvent.Pass -> currentPendingPassHandler.value(event)
                    else -> currentBroadcast.value(event)
                }
                return true
            }
        }
    }
    ComposeWebView(
        state = webViewState,
        modifier = Modifier.fillMaxSize(),
        captureBackPresses = false,
        onCreated = { it.settings.javaScriptEnabled = true },
        onDispose = {
            it.stopLoading()
            it.loadUrl("about:blank")
            it.destroy()
        },
        chromeClient = queueChromeClient,
    )
}
