package br.gov.caixa.loterias.core.domain.tickets

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf
import br.gov.caixa.loterias.core.domain.betting.LotecaMatchSelection
import br.gov.caixa.loterias.core.domain.betting.LotogolMatchSelection
import br.gov.caixa.loterias.core.domain.betting.LuckyMonth
import br.gov.caixa.loterias.core.domain.betting.SportsTeam
import java.math.BigDecimal

data class ConfirmedBet(
    val id: Long,
    val modality: String,
    val contest: Int,
    val date: String,
    val status: String,
    val numbers: List<Int>,
    val prize: BigDecimal = BigDecimal.ZERO,
    val originalRecurringBet: Boolean = false,
    val redeemedPrize: Boolean = false,
    val exchangeBetId: Long? = null,
    val modalityId: Int = 0,
    val statusId: Long = 0,
    val contestTypeId: Long = 0,
    val poolShare: Boolean = false,
    val surprise: Boolean = false,
    val time: String = "",
    val numberColumns: List<List<Int>> = if (numbers.isEmpty()) emptyList() else listOf(numbers),
    val clovers: List<Int> = emptyList(),
    val favoriteTeam: SportsTeam? = null,
    val luckyMonth: LuckyMonth? = null,
    val lotecaSelections: List<LotecaMatchSelection> = emptyList(),
    val lotogolSelections: List<LotogolMatchSelection> = emptyList(),
    val recurringDraws: Int = 0,
    val quantity: Int = 1,
    val mirror: Boolean = false,
    val exchange: Boolean = false,
    val combo: Boolean = false,
    val receipt: ConfirmedBetReceipt? = null,
    val backend: ConfirmedBetsBackend = ConfirmedBetsBackend.SILCE,
)

data class ConfirmedBetReceipt(
    val nsbi: String = "",
    val paymentDate: String = "",
    val paymentTime: String = "",
    val paymentLocation: String = "",
    val paymentNsu: String = "",
    val netPrize: BigDecimal? = null,
    val awards: List<CheckedTicketAward> = emptyList(),
)

data class Purchase(
    val id: Long,
    val date: String,
    val status: String,
    val paymentMethod: String,
    val total: BigDecimal,
    val pixCopyPaste: String = "",
    val paymentExpiresAt: String = "",
    val statusId: Long = 0,
    val paymentMethodId: Long = 0,
    val finalizedTime: String = "",
    val endToEndId: String = "",
)

data class PurchaseDetails(
    val purchase: Purchase,
    val bets: List<ConfirmedBet>,
    val serverDateTime: String = "",
)

data class TicketQuery(
    val code: String,
    val qrCode: Boolean = false,
    val retailerCode: String = "",
)

/** Result of conferir-bilhete, distinct from the confirmed-bet history contract. */
data class CheckedTicketResult(
    val bets: List<ConfirmedBet> = emptyList(),
    val statusId: Int? = null,
    val modalityId: Int? = null,
    val modalityName: String = "",
    val netPrize: BigDecimal? = null,
    val awards: List<CheckedTicketAward> = emptyList(),
)
data class CheckedTicketAward(
    val contest: Int?, val status: String, val name: String = "",
    val hitQuantity: Int? = null, val draw: Int? = null, val value: BigDecimal? = null,
)

data class TicketFilterOption(val value: Long, val description: String)

data class TicketPeriod(
    val month: Int,
    val year: Int? = null,
    val description: String,
)

data class ConfirmedBetsConfiguration(
    val defaultOrdering: Long? = null,
    val defaultStatus: Long? = null,
    val defaultPeriod: TicketPeriod? = null,
    val statuses: List<TicketFilterOption> = emptyList(),
    val orderings: List<TicketFilterOption> = emptyList(),
    val periods: List<TicketPeriod> = emptyList(),
    val betTypes: List<TicketFilterOption> = emptyList(),
    val contestTypes: List<TicketFilterOption> = emptyList(),
)

data class PurchaseMonth(val month: Int, val year: Int, val description: String)

enum class ConfirmedBetsBackend { SILCE, MICROSERVICE, NOVA_API }
data class ConfirmedBetsPage(val bets: List<ConfirmedBet>, val backend: ConfirmedBetsBackend = ConfirmedBetsBackend.SILCE)

interface TicketsRepository {
    suspend fun confirmedBets(filters: Map<String, String> = emptyMap()): List<ConfirmedBet>
    suspend fun confirmedBetsPage(filters: Map<String, String>): ConfirmedBetsPage = ConfirmedBetsPage(confirmedBets(filters))
    suspend fun recoverConfirmedBet(bet: ConfirmedBet): ConfirmedBet = bet
    suspend fun loadConfirmedBet(bet: ConfirmedBet): br.gov.caixa.loterias.core.domain.service.ServiceResult<ConfirmedBet> =
        br.gov.caixa.loterias.core.domain.service.ServiceResult.Success(recoverConfirmedBet(bet))
    suspend fun purchases(filters: Map<String, String> = emptyMap()): List<Purchase>
    suspend fun purchaseBets(purchaseId: Long): List<ConfirmedBet>
    suspend fun purchaseDetails(purchase: Purchase): PurchaseDetails =
        PurchaseDetails(purchase, purchaseBets(purchase.id))
    suspend fun savePurchaseAsFavorite(purchaseId: Long, name: String, keepSurprises: Boolean) = Unit
    suspend fun checkTicket(query: TicketQuery): List<ConfirmedBet>
    suspend fun checkTicketResult(query: TicketQuery): CheckedTicketResult = CheckedTicketResult(bets = checkTicket(query))
    suspend fun confirmedBetsConfiguration(): ConfirmedBetsConfiguration
    suspend fun purchaseMonths(): List<PurchaseMonth>
    suspend fun purchasePaymentMethods(): List<TicketFilterOption>
    suspend fun purchaseStatuses(paymentMethodId: Long?): List<TicketFilterOption>
}

fun interface RecoverConfirmedBetUseCase {
    suspend operator fun invoke(bet: ConfirmedBet): AppResult<ConfirmedBet>
}

class RecoverConfirmedBet(private val repository: TicketsRepository) : RecoverConfirmedBetUseCase {
    override suspend fun invoke(bet: ConfirmedBet) = appResultOf {
        require(bet.id > 0) { "Aposta inválida" }
        repository.recoverConfirmedBet(bet)
    }
}

fun interface GetConfirmedBetsConfigurationUseCase {
    suspend operator fun invoke(): AppResult<ConfirmedBetsConfiguration>
}

class GetConfirmedBetsConfiguration(private val repository: TicketsRepository) : GetConfirmedBetsConfigurationUseCase {
    override suspend fun invoke() = appResultOf { repository.confirmedBetsConfiguration() }
}

fun interface GetPurchaseMonthsUseCase {
    suspend operator fun invoke(): AppResult<List<PurchaseMonth>>
}

class GetPurchaseMonths(private val repository: TicketsRepository) : GetPurchaseMonthsUseCase {
    override suspend fun invoke() = appResultOf { repository.purchaseMonths() }
}

fun interface GetPurchasePaymentMethodsUseCase {
    suspend operator fun invoke(): AppResult<List<TicketFilterOption>>
}

class GetPurchasePaymentMethods(private val repository: TicketsRepository) : GetPurchasePaymentMethodsUseCase {
    override suspend fun invoke() = appResultOf { repository.purchasePaymentMethods() }
}

fun interface GetPurchaseStatusesUseCase {
    suspend operator fun invoke(paymentMethodId: Long?): AppResult<List<TicketFilterOption>>
}

class GetPurchaseStatuses(private val repository: TicketsRepository) : GetPurchaseStatusesUseCase {
    override suspend fun invoke(paymentMethodId: Long?) = appResultOf { repository.purchaseStatuses(paymentMethodId) }
}

fun interface GetConfirmedBetsUseCase {
    suspend operator fun invoke(filters: Map<String, String>): AppResult<List<ConfirmedBet>>
    suspend fun page(filters: Map<String, String>): AppResult<ConfirmedBetsPage> = when (val result = invoke(filters)) {
        is AppResult.Success -> AppResult.Success(ConfirmedBetsPage(result.value))
        is AppResult.Failure -> result
    }
}

class GetConfirmedBets(private val repository: TicketsRepository) : GetConfirmedBetsUseCase {
    override suspend fun page(filters: Map<String, String>) = appResultOf { repository.confirmedBetsPage(filters) }
    override suspend fun invoke(filters: Map<String, String>) = appResultOf { repository.confirmedBets(filters) }
}

fun interface GetPurchasesUseCase {
    suspend operator fun invoke(filters: Map<String, String>): AppResult<List<Purchase>>
}

class GetPurchases(private val repository: TicketsRepository) : GetPurchasesUseCase {
    override suspend fun invoke(filters: Map<String, String>) = appResultOf { repository.purchases(filters) }
}

fun interface GetPurchaseBetsUseCase {
    suspend operator fun invoke(purchaseId: Long): AppResult<List<ConfirmedBet>>
}

class GetPurchaseBets(private val repository: TicketsRepository) : GetPurchaseBetsUseCase {
    override suspend fun invoke(purchaseId: Long) = appResultOf {
        require(purchaseId > 0) { "Compra inválida" }
        repository.purchaseBets(purchaseId)
    }
}

fun interface GetPurchaseDetailsUseCase {
    suspend operator fun invoke(purchase: Purchase): AppResult<PurchaseDetails>
}

class GetPurchaseDetails(private val repository: TicketsRepository) : GetPurchaseDetailsUseCase {
    override suspend fun invoke(purchase: Purchase) = appResultOf {
        require(purchase.id > 0) { "Compra inválida" }
        repository.purchaseDetails(purchase)
    }
}

fun interface SavePurchaseAsFavoriteUseCase {
    suspend operator fun invoke(purchaseId: Long, name: String, keepSurprises: Boolean): AppResult<Unit>
}

class SavePurchaseAsFavorite(private val repository: TicketsRepository) : SavePurchaseAsFavoriteUseCase {
    override suspend fun invoke(purchaseId: Long, name: String, keepSurprises: Boolean) = appResultOf {
        require(purchaseId > 0) { "Compra inválida" }
        require(name.isNotBlank()) { "Informe o nome do carrinho favorito" }
        repository.savePurchaseAsFavorite(purchaseId, name.trim(), keepSurprises)
    }
}

fun interface CheckTicketUseCase {
    suspend operator fun invoke(query: TicketQuery): AppResult<List<ConfirmedBet>>
    suspend fun result(query: TicketQuery): AppResult<CheckedTicketResult> = when (val result = invoke(query)) {
        is AppResult.Success -> AppResult.Success(CheckedTicketResult(bets = result.value))
        is AppResult.Failure -> result
    }
}

class CheckTicket(private val repository: TicketsRepository) : CheckTicketUseCase {
    override suspend fun result(query: TicketQuery) = appResultOf {
        require(query.code.isNotBlank()) { "Código do bilhete obrigatório" }
        repository.checkTicketResult(query.copy(code = query.code.trim()))
    }
    override suspend fun invoke(query: TicketQuery) = appResultOf {
        require(query.code.isNotBlank()) { "Código do bilhete obrigatório" }
        repository.checkTicket(query.copy(code = query.code.trim()))
    }
}

fun interface LoadConfirmedBetUseCase {
    suspend operator fun invoke(bet: ConfirmedBet): br.gov.caixa.loterias.core.domain.service.ServiceResult<ConfirmedBet>
}

class LoadConfirmedBet(private val repository: TicketsRepository) : LoadConfirmedBetUseCase {
    override suspend fun invoke(bet: ConfirmedBet) = repository.loadConfirmedBet(bet)
}
