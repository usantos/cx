package br.gov.caixa.loterias.feature.account.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.contract.account.port.SessionAuthentication
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.account.domain.model.AccountStatus
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.account.presentation.mapper.loginRoute
import br.gov.caixa.loterias.feature.account.presentation.mapper.loginUserMessage
import br.gov.caixa.loterias.feature.account.presentation.state.LoginAction
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class LoginCompletion(
    private val mutableState: ReduxStateStore<LoginUiState, LoginAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val authentication: SessionAuthentication,
    private val verifyAccess: VerifyPostLoginAccessUseCase,
    private val biometricAuthorizationFailed: () -> Unit,
) {
    @Volatile private var failedIdentity: AuthenticatedSessionIdentity? = null

    fun clearUnverifiedSession() {
        val identity = failedIdentity
        if (identity == null) authentication.logout() else authentication.logout(identity)
    }

    fun completeLogin(biometricExchange: Boolean = false, silentExchange: Boolean = false, exchange: suspend () -> Unit) {
        if (mutableState.value.isLoading || mutableState.value.authenticated || mutableState.value.blockedAccountCheckFailed ||
            mutableState.value.consultationOnlyNoticePending
        ) {
            return
        }
        mutableState.dispatch(LoginAction.LoginStarted)
        scope.launch(dispatchers.io) {
            try {
                exchange()
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                mutableState.dispatch(LoginAction.LoginCancelled)
                throw cancelled
            } catch (_: Exception) {
                if (biometricExchange) {
                    biometricAuthorizationFailed()
                    return@launch
                }
                if (silentExchange) authentication.cancelAuthorization()
                mutableState.dispatch(
                    LoginAction.LoginFailed(if (silentExchange) null else messageText("nao_foi_possivel_entrar_tente_no_e61749"))
                )
                return@launch
            }
            val access = try {
                verifyAccess()
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                mutableState.dispatch(LoginAction.LoginCancelled)
                throw cancelled
            }
            applyAccessResult(access)
        }
    }

    private fun applyAccessResult(access: PostLoginAccessResult) {
        when (access) {
            is PostLoginAccessResult.Verified -> {
                val identity = access.identity
                if (identity != null && !authentication.isCurrent(identity)) {
                    mutableState.dispatch(LoginAction.LoginCancelled)
                    return
                }
                mutableState.dispatch(
                    LoginAction.AccessVerified(
                        access.access.status != AccountStatus.PARTIALLY_BLOCKED,
                        access.access.status == AccountStatus.PARTIALLY_BLOCKED,
                        access.access.destination.loginRoute
                    )
                )
            }

            is PostLoginAccessResult.AccountCheckFailed -> {
                // Revoga o titular adquirido antes de exibir um erro ou permitir retorno ao fluxo anterior.
                try {
                    val identity = access.identity
                    failedIdentity = identity
                    if (identity != null && !authentication.logout(identity)) {
                        mutableState.dispatch(LoginAction.LoginCancelled)
                        return
                    }
                    mutableState.dispatch(LoginAction.LoginFailed(access.error.loginUserMessage()))
                } catch (_: Exception) {
                    // Mantém o aviso de bloqueio até ser possível repetir a limpeza local com segurança.
                    mutableState.dispatch(LoginAction.AccountCheckFailed)
                }
            }

            PostLoginAccessResult.SessionSuperseded -> mutableState.dispatch(LoginAction.LoginCancelled)

            is PostLoginAccessResult.BlockedAccountCheckFailedForSession -> {
                failedIdentity = access.identity
                mutableState.dispatch(LoginAction.AccountCheckFailed)
            }

            PostLoginAccessResult.BlockedAccountCheckFailed -> {
                failedIdentity = null
                mutableState.dispatch(LoginAction.AccountCheckFailed)
            }
        }
    }
}
