package br.gov.caixa.loterias.core.auth.session

import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionContent
import br.gov.caixa.loterias.feature.account.domain.repository.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.feature.account.domain.model.AccountNotification
import br.gov.caixa.loterias.feature.account.domain.model.LatestNotificationSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import br.gov.caixa.loterias.core.auth.token.accessTokenExpiration
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshContext
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshRequest
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshResponse
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshTicket
import br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshResult

class SessionTokenStore : AuthTokenProvider, AuthenticatedSessionIdentityProvider, AuthenticatedSessionContentRepository, LatestNotificationSession, ForegroundSessionAccess {
    private class State(val token: String?, val attempt: AuthorizationAttempt? = null, val idToken: String? = null,
        val identity: AuthenticatedSessionIdentity? = null, val content: AuthenticatedSessionContent? = null,
        val refreshToken: String? = null, val refreshContext: RegularSessionRefreshContext? = null,
        val generation: Any = Any(),
    )
    private val state = MutableStateFlow(State(null))
    private class ForegroundNotice(val marker: String, val check: ForegroundSessionCheck)
    private var foregroundNotice: ForegroundNotice? = null

    override fun token(): String? = state.value.token

    /** Uma geração apenas em memória. A concessão de visitante não pode voltar a valer após login seguido de logout. */
    fun captureLocalPersistenceLease(): () -> Boolean {
        val captured = state.value
        return {
            val current = state.value
            captured.attempt == null && current.attempt == null &&
                current.generation === captured.generation && current.identity === captured.identity
        }
    }

    /** Classifica localmente o token de acesso já disponível; nunca autentica um JWT. */
    override fun foregroundCheck(nowSeconds: Long): ForegroundSessionCheck {
        val captured = state.value
        val status = when {
            captured.attempt != null -> ForegroundSessionStatus.AUTHORIZATION_PENDING
            captured.token == null -> ForegroundSessionStatus.MISSING
            (accessTokenExpiration(captured.token) ?: Long.MIN_VALUE) <= nowSeconds -> ForegroundSessionStatus.EXPIRED
            else -> ForegroundSessionStatus.ACTIVE
        }
        return ForegroundSessionCheck(status, captured)
    }

    /** A confirmação de expiração não pode encerrar uma sessão substituta nem um login pendente por navegador ou biometria. */
    @Synchronized override fun clearForegroundSession(check: ForegroundSessionCheck): Boolean {
        if (check.status != ForegroundSessionStatus.EXPIRED && check.status != ForegroundSessionStatus.MISSING) return false
        val captured = check.reference as? State ?: return false
        while (true) {
            val current = state.value
            if (current.attempt != null || captured.attempt != null || current.identity !== captured.identity ||
                current.token !== captured.token || (captured.identity == null && current !== captured)
            ) {
                return false
            }
            if (state.compareAndSet(current, State(null))) {
                forgetInvalidForegroundNotice()
                return true
            }
        }
    }

    /** Somente o marcador aleatório da interface pode ser salvo; este registro de retratos termina com o processo. */
    @Synchronized override fun rememberForegroundNotice(check: ForegroundSessionCheck): String? {
        if (check.status != ForegroundSessionStatus.EXPIRED || !isForegroundSnapshotCurrent(check)) return null
        foregroundNotice?.takeIf { isForegroundSnapshotCurrent(it.check) }?.let { return it.marker }
        return java.util.UUID.randomUUID().toString().also { foregroundNotice = ForegroundNotice(it, check) }
    }

    @Synchronized override fun restoreForegroundNotice(marker: String, nowSeconds: Long): ForegroundSessionCheck? {
        val notice = foregroundNotice?.takeIf { it.marker == marker } ?: return null
        if (!isForegroundSnapshotCurrent(notice.check) || foregroundCheck(nowSeconds).status != ForegroundSessionStatus.EXPIRED) {
            foregroundNotice = null
            return null
        }
        return notice.check
    }

    @Synchronized override fun dismissForegroundNotice(marker: String) {
        if (foregroundNotice?.marker == marker) foregroundNotice = null
    }

    @Synchronized private fun forgetInvalidForegroundNotice() {
        if (foregroundNotice?.let { !isForegroundSnapshotCurrent(it.check) } == true) foregroundNotice = null
    }

    private fun isForegroundSnapshotCurrent(check: ForegroundSessionCheck): Boolean {
        val captured = check.reference as? State ?: return false
        val current = state.value
        return current.attempt == null && captured.attempt == null && current.identity === captured.identity &&
            current.token === captured.token && (captured.identity != null || current === captured)
    }

    /** Concessões reativas de titularidade para navegação; suspensas enquanto outro login estiver pendente. */
    fun observeIdentities(): kotlinx.coroutines.flow.Flow<AuthenticatedSessionIdentity?> =
        state.map { if (it.attempt == null) it.identity else null }

    override fun captureIdentity(): AuthenticatedSessionIdentity? = state.value.let {
        if (it.attempt == null) it.identity else null
    }

    override fun isCurrent(identity: AuthenticatedSessionIdentity): Boolean = state.value.let {
        it.attempt == null && it.identity === identity
    }

    /** Vincula atomicamente as credenciais à identidade capturada; nunca substitui por outro login. */
    fun tokenFor(identity: AuthenticatedSessionIdentity): String? = state.value.let {
        if (it.attempt == null && it.identity === identity) it.token else null
    }

    /** Captura token e identidade atomicamente; cabeçalhos explícitos não podem vincular outro login. */
    fun captureRefreshTicketForToken(token: String): RegularSessionRefreshTicket? {
        val captured = state.value
        if (captured.token != token) return null
        return refreshTicket(captured)
    }

    fun captureRefreshTicket(): RegularSessionRefreshTicket? = refreshTicket(state.value)

    private fun refreshTicket(captured: State): RegularSessionRefreshTicket? {
        if (captured.attempt != null || captured.refreshToken == null || captured.refreshContext == null) return null
        return captured.identity?.let { RegularSessionRefreshTicket(it, captured.generation) }
    }

    internal fun refreshTicketStatus(ticket: RegularSessionRefreshTicket): RegularSessionRefreshResult? {
        val current = state.value
        if (current.attempt != null || current.identity !== ticket.identity) return RegularSessionRefreshResult.STALE
        if (current.generation !== ticket.generation) return RegularSessionRefreshResult.ALREADY_UPDATED
        return null
    }

    internal fun refreshRequest(ticket: RegularSessionRefreshTicket): RegularSessionRefreshRequest? {
        val current = state.value
        if (current.attempt != null || current.identity !== ticket.identity || current.generation !== ticket.generation) return null
        return RegularSessionRefreshRequest(
            current.refreshToken ?: return null,
            ticket.identity.ownerKey,
            current.refreshContext ?: return null,
            current.idToken
        )
    }

    @Synchronized internal fun commitRefresh(
        ticket: RegularSessionRefreshTicket,
        response: RegularSessionRefreshResponse,
        afterCommit: () -> Unit = {},
    ): RegularSessionRefreshResult {
        val access = response.accessToken.trim().takeIf(String::isNotEmpty)
        val refresh = response.refreshToken.trim().takeIf(String::isNotEmpty)
        while (true) {
            val previous = state.value
            if (previous.attempt != null || previous.identity !== ticket.identity) return RegularSessionRefreshResult.STALE
            if (previous.generation !== ticket.generation) return RegularSessionRefreshResult.ALREADY_UPDATED
            if (access == null || refresh == null || response.ownerKey != ticket.identity.ownerKey ||
                (response.idToken != null && response.idToken.isBlank())
            ) {
                return RegularSessionRefreshResult.FAILED
            }
            if (state.compareAndSet(
                    previous,
                    State(
                        access,
                        idToken = response.idToken ?: previous.idToken,
                        identity = previous.identity,
                        content = previous.content,
                        refreshToken = refresh,
                        refreshContext = previous.refreshContext
                    )
                )
            ) {
                forgetInvalidForegroundNotice()
                // Esta concessão permanece atual durante a persistência; logout e novo login usam o mesmo monitor.
                // A persistência opcional de credenciais não pode transformar uma troca de tokens aceita em falha de autenticação.
                try {
                    afterCommit()
                } catch (_: Exception) { }
                return RegularSessionRefreshResult.UPDATED
            }
        }
    }

    @Synchronized fun replace(token: String?) {
        state.value = State(token?.trim()?.takeIf(String::isNotEmpty))
        forgetInvalidForegroundNotice()
    }

    @Synchronized fun clear() {
        state.value = State(null)
        forgetInvalidForegroundNotice()
    }

    /** Chamado somente pelos dados de teste da variante de depuração; nunca substitui login nem tentativa pendente. */
    @Synchronized internal fun installVisualFixtureIfEmpty(token: String): Boolean {
        while (true) {
            val previous = state.value
            if (previous.token != null || previous.attempt != null || previous.identity != null) return false
            if (state.compareAndSet(previous, State(token, identity = AuthenticatedSessionIdentity("a".repeat(64))))) return true
        }
    }

    @Synchronized override fun publish(identity: AuthenticatedSessionIdentity, content: AuthenticatedSessionContent?): Boolean {
        while (true) {
            val previous = state.value
            if (previous.attempt != null || previous.identity !== identity) return false
            if (state.compareAndSet(
                    previous,
                    State(
                        previous.token,
                        idToken = previous.idToken,
                        identity = previous.identity,
                        content = content,
                        refreshToken = previous.refreshToken,
                        refreshContext = previous.refreshContext,
                        generation = previous.generation
                    )
                )
            ) {
                return true
            }
        }
    }

    override fun observe() = state.map { if (it.attempt == null && it.identity != null) it.content else null }

    override fun capture(notification: AccountNotification): AuthenticatedSessionIdentity? = state.value.let {
        if (it.attempt == null && it.content?.latestNotification === notification) it.identity else null
    }

    @Synchronized override fun clear(identity: AuthenticatedSessionIdentity, notification: AccountNotification): Boolean {
        while (true) {
            val previous = state.value
            val content = previous.content ?: return false
            if (previous.attempt != null || previous.identity !== identity || content.latestNotification !== notification) return false
            if (state.compareAndSet(
                    previous,
                    State(
                        previous.token,
                        idToken = previous.idToken,
                        identity = previous.identity,
                        content = content.copy(latestNotification = null),
                        refreshToken = previous.refreshToken,
                        refreshContext = previous.refreshContext,
                        generation = previous.generation
                    )
                )
            ) {
                return true
            }
        }
    }

    /** Encerra atomicamente a autorização local e extrai apenas o token de ID OIDC para logout. */
    @Synchronized internal fun clearForEndSession(): String? {
        while (true) {
            val previous = state.value
            if (state.compareAndSet(previous, State(null))) {
                forgetInvalidForegroundNotice()
                return previous.idToken
            }
        }
    }

    internal fun isAuthorizationCurrent(attempt: AuthorizationAttempt): Boolean = state.value.attempt === attempt

    @Synchronized internal fun beginAuthorization(): AuthorizationAttempt {
        val attempt = AuthorizationAttempt()
        while (true) {
            val previous = state.value
            // Mesmo um cancelamento não deve reativar uma concessão capturada antes do início do novo login.
            val renewedIdentity = previous.identity?.let { AuthenticatedSessionIdentity(it.ownerKey) }
            if (state.compareAndSet(
                    previous,
                    State(
                        previous.token,
                        attempt,
                        previous.idToken,
                        renewedIdentity,
                        previous.content,
                        previous.refreshToken,
                        previous.refreshContext
                    )
                )
            ) {
                return attempt
            }
        }
    }

    @Synchronized internal fun completeAuthorization(
        attempt: AuthorizationAttempt,
        token: String,
        idToken: String? = null,
        ownerKey: String? = null,
        refreshToken: String? = null,
        refreshContext: RegularSessionRefreshContext? = null,
    ): Boolean {
        val normalized = token.trim().takeIf(String::isNotEmpty) ?: return false
        val identity = ownerKey?.let(::AuthenticatedSessionIdentity)
        while (true) {
            val previous = state.value
            if (previous.attempt !== attempt) return false
            if (state.compareAndSet(
                    previous,
                    State(
                        normalized,
                        idToken = idToken?.trim()?.takeIf(String::isNotEmpty),
                        identity = identity,
                        refreshToken = refreshToken?.trim()?.takeIf(String::isNotEmpty),
                        refreshContext = refreshContext
                    )
                )
            ) {
                return true
            }
        }
    }

    @Synchronized internal fun cancelAuthorization(attempt: AuthorizationAttempt? = null) {
        while (true) {
            val previous = state.value
            if (attempt != null && previous.attempt !== attempt) return
            if (state.compareAndSet(
                    previous,
                    State(
                        previous.token,
                        idToken = previous.idToken,
                        identity = previous.identity,
                        content = previous.content,
                        refreshToken = previous.refreshToken,
                        refreshContext = previous.refreshContext,
                        generation = previous.generation
                    )
                )
            ) {
                return
            }
        }
    }
}

/** Comprovante apenas de identidade: não contém credencial e é invalidado por logout, substituição ou novo login. */
internal class AuthorizationAttempt
