package br.gov.caixa.loterias.apostas.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.apostas.R as LocalizedR
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.betting.presentation.route.ShakeTutorialRoute
import br.gov.caixa.loterias.feature.home.domain.usecase.HomePreferencesUseCases
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomePreferencesViewModel

/** Criado somente após validar a conta depois do login, preservando a continuação original. */
internal data class PostLoginTutorialRequest(val destination: String, val identity: AuthenticatedSessionIdentity)

@Composable
internal fun PostLoginShakeTutorial(
    request: PostLoginTutorialRequest,
    currentIdentity: AuthenticatedSessionIdentity?,
    identityProvider: AuthenticatedSessionIdentityProvider,
    preferences: HomePreferencesUseCases,
    dispatchers: CoroutineDispatchers,
    onReady: (String) -> Unit,
    onCancelled: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val current = identityProvider.isCurrent(request.identity)
    if (!current) {
        LaunchedEffect(request, currentIdentity) { onCancelled() }
        return
    }
    key(request) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            val owner = remember {
                object : ViewModelStoreOwner {
                    override val viewModelStore = ViewModelStore()
                }
            }
            DisposableEffect(owner) { onDispose { owner.viewModelStore.clear() } }
            val model = viewModel<HomePreferencesViewModel>(viewModelStoreOwner = owner) {
                HomePreferencesViewModel(preferences, dispatchers, request.identity.ownerKey)
            }
            val state by model.state.collectAsStateWithLifecycle()
            LaunchedEffect(state.loaded, state.shakeTutorialCompleted) {
                if (state.loaded && state.shakeTutorialCompleted && identityProvider.isCurrent(request.identity)) {
                    onReady(request.destination)
                }
            }
            Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.White)) {
                if (state.loaded && !state.shakeTutorialCompleted) {
                    ShakeTutorialRoute { model.completeShakeTutorial() }
                }
                state.errorMessage?.let { message ->
                    CaixaDSDialog(
                        title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
                        message = message,
                        positiveLabel = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                        onPositive = { if (state.loaded) model.completeShakeTutorial() else model.load() }
                    )
                }
            }
        }
    }
}
