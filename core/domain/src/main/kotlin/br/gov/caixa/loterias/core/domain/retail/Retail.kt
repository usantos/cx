package br.gov.caixa.loterias.core.domain.retail

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf
import java.math.BigDecimal

data class Retailer(
    val id: Long,
    val code: String,
    val fantasyName: String,
    val legalName: String,
    val address: String,
    val district: String,
    val city: String,
    val state: String,
    val favorite: Boolean,
)

interface RetailRepository {
    suspend fun search(query: String): List<Retailer>
    suspend fun favorites(): List<Retailer>
    suspend fun setFavorite(retailerId: Long, favorite: Boolean)
}

fun interface SearchRetailersUseCase { suspend operator fun invoke(query: String): AppResult<List<Retailer>> }
class SearchRetailers(private val repository: RetailRepository) : SearchRetailersUseCase {
    override suspend fun invoke(query: String) = appResultOf {
        val normalized = query.trim()
        require(normalized.length >= 3) { "Informe pelo menos 3 caracteres" }
        repository.search(normalized)
    }
}

fun interface GetFavoriteRetailersUseCase { suspend operator fun invoke(): AppResult<List<Retailer>> }
class GetFavoriteRetailers(private val repository: RetailRepository) : GetFavoriteRetailersUseCase {
    override suspend fun invoke() = appResultOf(repository::favorites)
}

fun interface SetRetailerFavoriteUseCase { suspend operator fun invoke(retailerId: Long, favorite: Boolean): AppResult<Unit> }
class SetRetailerFavorite(private val repository: RetailRepository) : SetRetailerFavoriteUseCase {
    override suspend fun invoke(retailerId: Long, favorite: Boolean) = appResultOf {
        require(retailerId > 0) { "Lotérica inválida" }
        repository.setFavorite(retailerId, favorite)
    }
}

enum class PrizeRedemptionChannel(val legacyId: Long) {
    MERCADO_PAGO(1),
    PIX(6),
    LOTTERY(9660),
    AGENCY(9770),
}

data class PrizeRedemptionOption(
    val channel: PrizeRedemptionChannel,
    val title: String,
    val description: String,
)

data class PrizeDetails(
    val betId: Long,
    val netValue: BigDecimal,
    val nsbi: String,
    val options: List<PrizeRedemptionOption>,
    val incomeTaxMessage: String = "",
    val modalityId: Int = 2,
    val modalityName: String = "Mega-Sena",
    val initialContest: Int = 0,
    val targetContest: Int = 0,
    val awards: List<PrizeAward> = emptyList(),
    val isPool: Boolean = false,
    val exchange: Boolean = false,
    val combo: Boolean = false,
    val mirror: Boolean = false,
    val surprise: Boolean = false,
    val recurringDraws: Int = 0,
)

data class PrizeAward(
    val contest: Int,
    val description: String,
    val amount: BigDecimal?,
    val status: String = "",
    val hitCount: Int? = null,
    val draw: Int? = null,
    /** False only for concursosNaoPremiado placeholders; preserve received records. */
    val isAwarded: Boolean = true,
)

data class LotteryRedemptionCode(
    val qrCode: String,
    val code: String,
    val nsb: String,
    val validityMinutes: Int,
)

data class PixBeneficiary(
    val maskedCpf: String,
    val name: String,
    val key: String,
    val bankName: String,
)

sealed interface PrizeRedemptionResult {
    data class ConnectionFailed(val reason: br.gov.caixa.loterias.core.domain.service.ServiceConnectionFailure) : PrizeRedemptionResult
    data object Unchanged : PrizeRedemptionResult
    data class Redeemed(val noticeCode: String? = null) : PrizeRedemptionResult
    data class RegistrationRequired(val message: String) : PrizeRedemptionResult
    data class NoticeRequired(val notice: PrizeNotice) : PrizeRedemptionResult
    data class RedirectRequired(val destination: PrizeRedirect) : PrizeRedemptionResult
}

interface PrizeRedemptionRepository {
    suspend fun details(betId: Long): PrizeDetails
    suspend fun lotteryCode(betId: Long): LotteryRedemptionCode
    suspend fun consultPixKey(betId: Long, key: String): PixBeneficiary
    suspend fun redeemPix(betId: Long, key: String): PrizeRedemptionResult
    suspend fun cancelPix(betId: Long)
    suspend fun redeemMercadoPago(betId: Long, email: String): PrizeRedemptionResult
    suspend fun betReceiptPdf(betId: Long): ByteArray
    suspend fun prizeReceiptPdf(betId: Long): ByteArray
}

fun interface GetPrizeDetailsUseCase { suspend operator fun invoke(betId: Long): AppResult<PrizeDetails> }
class GetPrizeDetails(private val repository: PrizeRedemptionRepository) : GetPrizeDetailsUseCase {
    override suspend fun invoke(betId: Long) = appResultOf {
        require(betId > 0) { "Aposta inválida" }
        repository.details(betId)
    }
}

fun interface GenerateLotteryRedemptionCodeUseCase {
    suspend operator fun invoke(betId: Long): AppResult<LotteryRedemptionCode>
}
class GenerateLotteryRedemptionCode(private val repository: PrizeRedemptionRepository) : GenerateLotteryRedemptionCodeUseCase {
    override suspend fun invoke(betId: Long) = appResultOf {
        require(betId > 0) { "Aposta inválida" }
        repository.lotteryCode(betId)
    }
}

fun interface ConsultPixRedemptionKeyUseCase {
    suspend operator fun invoke(betId: Long, key: String): AppResult<PixBeneficiary>
}
class ConsultPixRedemptionKey(private val repository: PrizeRedemptionRepository) : ConsultPixRedemptionKeyUseCase {
    override suspend fun invoke(betId: Long, key: String) = appResultOf {
        require(betId > 0) { "Aposta inválida" }
        require(key.trim().isNotEmpty()) { "Chave Pix obrigatória" }
        repository.consultPixKey(betId, key.trim())
    }
}

fun interface RedeemPrizeUseCase {
    suspend operator fun invoke(betId: Long, channel: PrizeRedemptionChannel, value: String): AppResult<PrizeRedemptionResult>
}
class RedeemPrize(private val repository: PrizeRedemptionRepository) : RedeemPrizeUseCase {
    override suspend fun invoke(betId: Long, channel: PrizeRedemptionChannel, value: String) = appResultOf {
        require(betId > 0) { "Aposta inválida" }
        when (channel) {
            PrizeRedemptionChannel.PIX -> {
                require(value.trim().isNotEmpty()) { "Chave Pix obrigatória" }
                repository.redeemPix(betId, value.trim())
            }
            PrizeRedemptionChannel.MERCADO_PAGO -> {
                require(value.trim().isNotEmpty()) { "E-mail obrigatório" }
                repository.redeemMercadoPago(betId, value.trim())
            }
            else -> error("O canal selecionado não realiza resgate online")
        }
    }
}

fun interface CancelPixRedemptionUseCase { suspend operator fun invoke(betId: Long): AppResult<Unit> }
class CancelPixRedemption(private val repository: PrizeRedemptionRepository) : CancelPixRedemptionUseCase {
    override suspend fun invoke(betId: Long) = appResultOf {
        require(betId > 0) { "Aposta inválida" }
        repository.cancelPix(betId)
    }
}

fun interface DownloadBetReceiptUseCase { suspend operator fun invoke(betId: Long): AppResult<ByteArray> }
class DownloadBetReceipt(private val repository: PrizeRedemptionRepository) : DownloadBetReceiptUseCase {
    override suspend fun invoke(betId: Long) = appResultOf {
        require(betId > 0) { "Aposta inválida" }
        repository.betReceiptPdf(betId)
    }
}

fun interface DownloadPrizeReceiptUseCase { suspend operator fun invoke(betId: Long): AppResult<ByteArray> }
class DownloadPrizeReceipt(private val repository: PrizeRedemptionRepository) : DownloadPrizeReceiptUseCase {
    override suspend fun invoke(betId: Long) = appResultOf {
        require(betId > 0) { "Aposta inválida" }
        repository.prizeReceiptPdf(betId)
    }
}
