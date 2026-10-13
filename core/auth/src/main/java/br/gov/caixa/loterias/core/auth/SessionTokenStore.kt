package br.gov.caixa.loterias.core.auth

import br.gov.caixa.loterias.core.network.AuthTokenProvider
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionContent
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.core.domain.account.AccountNotification
import br.gov.caixa.loterias.core.domain.account.LatestNotificationSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class SessionTokenStore : AuthTokenProvider, AuthenticatedSessionIdentityProvider, AuthenticatedSessionContentRepository, LatestNotificationSession, ForegroundSessionAccess {
    private class State(val token: String?, val attempt: AuthorizationAttempt? = null, val idToken: String? = null,
        val identity: AuthenticatedSessionIdentity? = null, val content: AuthenticatedSessionContent? = null)
    private val state = MutableStateFlow(State(null))
    private class ForegroundNotice(val marker: String, val check: ForegroundSessionCheck)
    private var foregroundNotice: ForegroundNotice? = null

    override fun token(): String? = state.value.token

    /** Classifies the already-held access token locally; it never authenticates a JWT. */
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

    /** Expiry acknowledgment cannot end a replacement session or a pending browser/biometric login. */
    override fun clearForegroundSession(check: ForegroundSessionCheck): Boolean {
        if (check.status != ForegroundSessionStatus.EXPIRED && check.status != ForegroundSessionStatus.MISSING) return false
        val captured = check.reference as? State ?: return false
        while (true) {
            val current = state.value
            if (current.attempt != null || captured.attempt != null || current.identity !== captured.identity ||
                current.token !== captured.token || (captured.identity == null && current !== captured)) return false
            if (state.compareAndSet(current, State(null))) {
                forgetInvalidForegroundNotice()
                return true
            }
        }
    }

    /** Only the random UI marker is saveable; this snapshot registry dies with the process. */
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

    /** Reactive owner leases for navigation; suspended while a different login is pending. */
    fun observeIdentities(): kotlinx.coroutines.flow.Flow<AuthenticatedSessionIdentity?> =
        state.map { if (it.attempt == null) it.identity else null }

    override fun captureIdentity(): AuthenticatedSessionIdentity? = state.value.let {
        if (it.attempt == null) it.identity else null
    }

    override fun isCurrent(identity: AuthenticatedSessionIdentity): Boolean = state.value.let {
        it.attempt == null && it.identity === identity
    }

    /** Atomically binds credentials to the captured identity; never substitutes another login. */
    fun tokenFor(identity: AuthenticatedSessionIdentity): String? = state.value.let {
        if (it.attempt == null && it.identity === identity) it.token else null
    }

    fun replace(token: String?) {
        state.value = State(token?.trim()?.takeIf(String::isNotEmpty))
        forgetInvalidForegroundNotice()
    }

    fun clear() { state.value = State(null); forgetInvalidForegroundNotice() }

    /** Called only by the debug source-set fixture, never overrides login or a pending attempt. */
    internal fun installVisualFixtureIfEmpty(token: String): Boolean {
        while (true) {
            val previous = state.value
            if (previous.token != null || previous.attempt != null || previous.identity != null) return false
            if (state.compareAndSet(previous, State(token, identity = AuthenticatedSessionIdentity("a".repeat(64))))) return true
        }
    }

    override fun publish(identity: AuthenticatedSessionIdentity, content: AuthenticatedSessionContent?): Boolean {
        while (true) {
            val previous = state.value
            if (previous.attempt != null || previous.identity !== identity) return false
            if (state.compareAndSet(previous, State(previous.token, idToken = previous.idToken,
                    identity = previous.identity, content = content))) return true
        }
    }

    override fun observe() = state.map { if (it.attempt == null && it.identity != null) it.content else null }

    override fun capture(notification: AccountNotification): AuthenticatedSessionIdentity? = state.value.let {
        if (it.attempt == null && it.content?.latestNotification === notification) it.identity else null
    }

    override fun clear(identity: AuthenticatedSessionIdentity, notification: AccountNotification): Boolean {
        while (true) {
            val previous = state.value
            val content = previous.content ?: return false
            if (previous.attempt != null || previous.identity !== identity || content.latestNotification !== notification) return false
            if (state.compareAndSet(previous, State(previous.token, idToken = previous.idToken, identity = previous.identity,
                    content = content.copy(latestNotification = null)))) return true
        }
    }

    /** Atomically ends local authorization and extracts only the OIDC ID token for logout. */
    internal fun clearForEndSession(): String? {
        while (true) {
            val previous = state.value
            if (state.compareAndSet(previous, State(null))) {
                forgetInvalidForegroundNotice()
                return previous.idToken
            }
        }
    }

    internal fun isAuthorizationCurrent(attempt: AuthorizationAttempt): Boolean = state.value.attempt === attempt

    internal fun beginAuthorization(): AuthorizationAttempt {
        val attempt = AuthorizationAttempt()
        while (true) {
            val previous = state.value
            // Even cancellation must not resurrect a lease captured before the new login began.
            val renewedIdentity = previous.identity?.let { AuthenticatedSessionIdentity(it.ownerKey) }
            if (state.compareAndSet(previous, State(previous.token, attempt, previous.idToken, renewedIdentity, previous.content))) return attempt
        }
    }

    internal fun completeAuthorization(attempt: AuthorizationAttempt, token: String, idToken: String? = null,
        ownerKey: String? = null): Boolean {
        val normalized = token.trim().takeIf(String::isNotEmpty) ?: return false
        val identity = ownerKey?.let(::AuthenticatedSessionIdentity)
        while (true) {
            val previous = state.value
            if (previous.attempt !== attempt) return false
            if (state.compareAndSet(previous, State(normalized, idToken = idToken?.trim()?.takeIf(String::isNotEmpty), identity = identity))) return true
        }
    }

    internal fun cancelAuthorization(attempt: AuthorizationAttempt? = null) {
        while (true) {
            val previous = state.value
            if (attempt != null && previous.attempt !== attempt) return
            if (state.compareAndSet(previous, State(previous.token, idToken = previous.idToken, identity = previous.identity,
                    content = previous.content))) return
        }
    }
}

/** Identity-only ticket: carries no credential and is invalidated by logout/replacement/new login. */
internal class AuthorizationAttempt
