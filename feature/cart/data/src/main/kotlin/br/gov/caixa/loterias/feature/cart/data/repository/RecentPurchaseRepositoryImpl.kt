package br.gov.caixa.loterias.feature.cart.data.repository

import android.content.Context
import br.gov.caixa.loterias.feature.cart.domain.repository.RecentPurchaseRepository
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import br.gov.caixa.loterias.feature.cart.data.datasource.local.RecentPurchaseLocalDataSource
import br.gov.caixa.loterias.feature.cart.data.datasource.local.RecentPurchaseLocalDataSourceImpl

/** Adaptador de compatibilidade: preserva o banco legado, a tabela e as datas com resolução de minutos. */
class RecentPurchaseRepositoryImpl internal constructor(
    private val store: RecentPurchaseLocalDataSource,
    private val zone: () -> ZoneId,
) : RecentPurchaseRepository {
    override suspend fun latestPurchaseAt(amount: BigDecimal): Instant? =
        store.datesForAmount(amount.stripTrailingZeros().toPlainString()).mapNotNull { date ->
            try {
                LocalDateTime.parse(date, FORMAT).atZone(zone()).toInstant()
            } catch (_: DateTimeParseException) {
                null
            }
        }.maxOrNull()

    override suspend fun record(amount: BigDecimal, purchasedAt: Instant) {
        store.replaceForAmount(
            amount.stripTrailingZeros().toPlainString(),
            FORMAT.format(purchasedAt.atZone(zone())),
        )
    }

    companion object {
        private val FORMAT = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT)
        fun fromContext(
            context: Context,
            captureLease: () -> (() -> Boolean) = { { true } },
            scope: () -> br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope,
        ): RecentPurchaseRepositoryImpl {
            val legacy = br.gov.caixa.loterias.feature.cart.data.datasource.local.AndroidCartPersistence(
                context,
                br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope("installation", "installation", "cart-legacy-v1")
            )
            return RecentPurchaseRepositoryImpl(
                RecentPurchaseLocalDataSourceImpl(
                    br.gov.caixa.loterias.feature.cart.data.datasource.local.SecureCartAccess(
                        br.gov.caixa.loterias.core.persistence.AndroidSecureStores(context.applicationContext),
                        scope,
                        legacy::migrateLegacy,
                        captureLease
                    )
                ),
                ZoneId::systemDefault
            )
        }
    }
}
