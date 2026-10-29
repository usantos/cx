// Literais numéricos descrevem dados sintéticos, níveis de API da plataforma e limites esperados dos dados de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.provider.PrizeBackendApiProvider
import br.gov.caixa.loterias.feature.retail.di.RetailModule
import br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode
import br.gov.caixa.loterias.feature.retail.domain.model.PixBeneficiary
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionResult
import br.gov.caixa.loterias.feature.retail.domain.model.RetailLocationEligibility
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import br.gov.caixa.loterias.feature.retail.domain.port.RetailCountrySource
import br.gov.caixa.loterias.feature.retail.domain.repository.PrizeRedemptionRepository
import br.gov.caixa.loterias.feature.retail.domain.repository.RetailRepository
import java.math.BigDecimal
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class RetailModuleContractTest {
    @Test fun `module aliases preserve single repository and remote source identity`() {
        val source = RetailModule.retrofitRetailDataSource(BackendApiProvider(), PrizeBackendApiProvider())
        assertSame(source, RetailModule.retailDataSource(source))
        assertSame(source, RetailModule.prizeRedemptionDataSource(source))
        val repository = RetailModule.retailRepository(source, source)
        assertSame(repository, RetailModule.retailContract(repository))
        assertSame(repository, RetailModule.prizeRedemptionContract(repository))
        assertSame(repository, RetailModule.mercadoPagoRegistrationContract(repository))
    }

    @Test fun `provided retail use cases forward exact query and favorite identity`() = runBlocking {
        val calls = mutableListOf<String>()
        val retailer = Retailer(8, "code", "name", "", "", "", "", "", false)
        val repository = object : RetailRepository {
            override suspend fun search(query: String): List<Retailer> {
                calls += "search:$query"
                return listOf(retailer)
            }
            override suspend fun favorites(): List<Retailer> {
                calls += "favorites"
                return listOf(retailer.copy(favorite = true))
            }
            override suspend fun setFavorite(retailerId: Long, favorite: Boolean) {
                calls += "favorite:$retailerId:$favorite"
            }
        }
        assertEquals(AppResult.Success(listOf(retailer)), RetailModule.searchRetailers(repository)(" casa "))
        assertEquals(AppResult.Success(listOf(retailer.copy(favorite = true))), RetailModule.favoriteRetailers(repository)())
        assertEquals(AppResult.Success(Unit), RetailModule.setRetailerFavorite(repository)(8, true))
        assertEquals(listOf("search:casa", "favorites", "favorite:8:true"), calls)
        assertEquals(RetailLocationEligibility.ALLOWED, RetailModule.checkRetailLocation(RetailCountrySource { "BR" })())
        assertEquals(RetailLocationEligibility.OUTSIDE_BRAZIL, RetailModule.checkRetailLocation(RetailCountrySource { "US" })())
    }

    @Test fun `provided prize use cases bind only selected synthetic repository operations`() = runBlocking {
        val calls = mutableListOf<String>()
        val details = PrizeDetails(9, BigDecimal.TEN, "synthetic", emptyList())
        val code = LotteryRedemptionCode("qr", "code", "nsb", 15)
        val beneficiary = PixBeneficiary("masked", "name", "key", "bank")
        val bytes = "%PDF-synthetic".toByteArray()
        val repository = object : PrizeRedemptionRepository {
            override suspend fun details(betId: Long): PrizeDetails {
                calls += "details:$betId"
                return details
            }
            override suspend fun lotteryCode(betId: Long): LotteryRedemptionCode {
                calls += "code:$betId"
                return code
            }
            override suspend fun consultPixKey(betId: Long, key: String): PixBeneficiary {
                calls += "consult:$betId:$key"
                return beneficiary
            }
            override suspend fun redeemPix(betId: Long, key: String): PrizeRedemptionResult {
                calls += "pix:$betId:$key"
                return PrizeRedemptionResult.Redeemed()
            }
            override suspend fun cancelPix(betId: Long) {
                calls += "cancel:$betId"
            }
            override suspend fun redeemMercadoPago(betId: Long, email: String): PrizeRedemptionResult {
                calls += "mp:$betId:$email"
                return PrizeRedemptionResult.Redeemed()
            }
            override suspend fun betReceiptPdf(betId: Long): ByteArray {
                calls += "bet:$betId"
                return bytes
            }
            override suspend fun prizeReceiptPdf(betId: Long): ByteArray {
                calls += "prize:$betId"
                return bytes
            }
        }
        assertEquals(AppResult.Success(details), RetailModule.prizeDetails(repository)(9))
        assertEquals(AppResult.Success(code), RetailModule.lotteryRedemptionCode(repository)(9))
        assertEquals(AppResult.Success(beneficiary), RetailModule.consultPixRedemptionKey(repository)(9, "key"))
        assertEquals(
            AppResult.Success(PrizeRedemptionResult.Redeemed()),
            RetailModule.redeemPrize(repository)(9, PrizeRedemptionChannel.PIX, "key")
        )
        assertEquals(
            AppResult.Success(PrizeRedemptionResult.Redeemed()),
            RetailModule.redeemPrize(repository)(9, PrizeRedemptionChannel.MERCADO_PAGO, "fixture@example.test")
        )
        assertEquals(AppResult.Success(Unit), RetailModule.cancelPixRedemption(repository)(9))
        assertArrayEquals(bytes, (RetailModule.downloadBetReceipt(repository)(9) as AppResult.Success<ByteArray>).value)
        assertArrayEquals(bytes, (RetailModule.downloadPrizeReceipt(repository)(9) as AppResult.Success<ByteArray>).value)
        assertEquals(
            listOf("details:9", "code:9", "consult:9:key", "pix:9:key", "mp:9:fixture@example.test", "cancel:9", "bet:9", "prize:9"),
            calls
        )
    }
}
