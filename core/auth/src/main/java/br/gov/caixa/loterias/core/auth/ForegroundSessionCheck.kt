package br.gov.caixa.loterias.core.auth

enum class ForegroundSessionStatus { ACTIVE, MISSING, EXPIRED, AUTHORIZATION_PENDING }

interface ForegroundSessionAccess {
    fun foregroundCheck(nowSeconds: Long): ForegroundSessionCheck
    fun clearForegroundSession(check: ForegroundSessionCheck): Boolean
    fun rememberForegroundNotice(check: ForegroundSessionCheck): String?
    fun restoreForegroundNotice(marker: String, nowSeconds: Long): ForegroundSessionCheck?
    fun dismissForegroundNotice(marker: String)
}

/** Local session snapshot only; never carries a credential into navigation or saved state. */
class ForegroundSessionCheck internal constructor(
    val status: ForegroundSessionStatus,
    internal val reference: Any,
) {
    override fun toString(): String = "ForegroundSessionCheck($status)"
}
