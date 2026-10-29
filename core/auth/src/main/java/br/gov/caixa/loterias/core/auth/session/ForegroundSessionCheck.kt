package br.gov.caixa.loterias.core.auth.session

enum class ForegroundSessionStatus { ACTIVE, MISSING, EXPIRED, AUTHORIZATION_PENDING }

interface ForegroundSessionAccess {
    fun foregroundCheck(nowSeconds: Long): ForegroundSessionCheck
    fun clearForegroundSession(check: ForegroundSessionCheck): Boolean
    fun rememberForegroundNotice(check: ForegroundSessionCheck): String?
    fun restoreForegroundNotice(marker: String, nowSeconds: Long): ForegroundSessionCheck?
    fun dismissForegroundNotice(marker: String)
}

/** Retrato local da sessão; nunca leva uma credencial à navegação nem ao estado salvo. */
class ForegroundSessionCheck internal constructor(
    val status: ForegroundSessionStatus,
    internal val reference: Any,
) {
    override fun toString(): String = "ForegroundSessionCheck($status)"
}
