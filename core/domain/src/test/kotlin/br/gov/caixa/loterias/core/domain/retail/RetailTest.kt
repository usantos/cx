package br.gov.caixa.loterias.core.domain.retail

import br.gov.caixa.loterias.core.common.AppResult
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RetailTest {
    @Test fun `search trims validates and delegates`() = runTest {
        val repository = FakeRepository()
        val result = SearchRetailers(repository)("  Centro  ")
        assertEquals("Centro", repository.query)
        assertEquals(1L, (result as AppResult.Success).value.single().id)
        assertTrue(SearchRetailers(repository)("ab") is AppResult.Failure)
    }

    @Test fun `favorites and mutations delegate and failures are wrapped`() = runTest {
        val repository = FakeRepository()
        assertTrue((GetFavoriteRetailers(repository)() as AppResult.Success).value.single().favorite)
        assertTrue(SetRetailerFavorite(repository)(1, true) is AppResult.Success)
        assertEquals(1L to true, repository.mutation)
        assertTrue(SetRetailerFavorite(repository)(0, false) is AppResult.Failure)

        repository.fail = true
        assertTrue(SearchRetailers(repository)("Centro") is AppResult.Failure)
        assertTrue(GetFavoriteRetailers(repository)() is AppResult.Failure)
        assertTrue(SetRetailerFavorite(repository)(1, false) is AppResult.Failure)
    }

    @Test fun `prize use cases validate and delegate every legacy operation`() = runTest {
        val repository = FakePrizeRepository()
        assertTrue(GetPrizeDetails(repository)(7) is AppResult.Success)
        assertTrue(GenerateLotteryRedemptionCode(repository)(7) is AppResult.Success)
        assertTrue(ConsultPixRedemptionKey(repository)(7, " key ") is AppResult.Success)
        assertEquals("key", repository.value)
        assertTrue(RedeemPrize(repository)(7, PrizeRedemptionChannel.PIX, " pix ") is AppResult.Success)
        assertEquals("pix", repository.value)
        assertTrue(RedeemPrize(repository)(7, PrizeRedemptionChannel.MERCADO_PAGO, " mail ") is AppResult.Success)
        assertEquals("mail", repository.value)
        assertTrue(CancelPixRedemption(repository)(7) is AppResult.Success)
        assertTrue(DownloadBetReceipt(repository)(7) is AppResult.Success)
        assertTrue(DownloadPrizeReceipt(repository)(7) is AppResult.Success)
        assertEquals(listOf("details", "code", "consult", "pix", "mp", "cancel", "bet-pdf", "prize-pdf"), repository.calls)
    }

    @Test fun `prize use cases reject invalid requests and unsupported online channels`() = runTest {
        val repository = FakePrizeRepository()
        assertTrue(GetPrizeDetails(repository)(0) is AppResult.Failure)
        assertTrue(GenerateLotteryRedemptionCode(repository)(0) is AppResult.Failure)
        assertTrue(ConsultPixRedemptionKey(repository)(0, "x") is AppResult.Failure)
        assertTrue(ConsultPixRedemptionKey(repository)(1, " ") is AppResult.Failure)
        assertTrue(RedeemPrize(repository)(0, PrizeRedemptionChannel.PIX, "x") is AppResult.Failure)
        assertTrue(RedeemPrize(repository)(1, PrizeRedemptionChannel.PIX, " ") is AppResult.Failure)
        assertTrue(RedeemPrize(repository)(1, PrizeRedemptionChannel.MERCADO_PAGO, " ") is AppResult.Failure)
        assertTrue(RedeemPrize(repository)(1, PrizeRedemptionChannel.LOTTERY, "") is AppResult.Failure)
        assertTrue(RedeemPrize(repository)(1, PrizeRedemptionChannel.AGENCY, "") is AppResult.Failure)
        assertTrue(CancelPixRedemption(repository)(0) is AppResult.Failure)
        assertTrue(DownloadBetReceipt(repository)(0) is AppResult.Failure)
        assertTrue(DownloadPrizeReceipt(repository)(0) is AppResult.Failure)
    }

    private class FakeRepository : RetailRepository {
        var query = ""
        var mutation: Pair<Long, Boolean>? = null
        var fail = false
        private fun check() { if (fail) error("offline") }
        override suspend fun search(query: String) = listOf(retailer(false)).also { check(); this.query = query }
        override suspend fun favorites() = listOf(retailer(true)).also { check() }
        override suspend fun setFavorite(retailerId: Long, favorite: Boolean) { check(); mutation = retailerId to favorite }
    }

    @Test fun `use case preserves registration as distinct from redeemed and trims submitted email`() = runTest {
        val repository = FakePrizeRepository()
        repository.mpResult = PrizeRedemptionResult.RegistrationRequired("Cadastrar?")
        val result = RedeemPrize(repository)(7, PrizeRedemptionChannel.MERCADO_PAGO, " selected@example.test ")
        assertEquals(AppResult.Success(repository.mpResult), result)
        assertEquals("selected@example.test", repository.value)
        assertEquals(listOf("mp"), repository.calls)
    }

    private class FakePrizeRepository : PrizeRedemptionRepository {
        val calls = mutableListOf<String>()
        var value = ""
        var mpResult: PrizeRedemptionResult = PrizeRedemptionResult.Redeemed()
        override suspend fun details(betId: Long) = PrizeDetails(betId, BigDecimal.TEN, "nsbi", emptyList()).also { calls += "details" }
        override suspend fun lotteryCode(betId: Long) = LotteryRedemptionCode("qr", "code", "nsb", 15).also { calls += "code" }
        override suspend fun consultPixKey(betId: Long, key: String) = PixBeneficiary("cpf", "name", key, "bank").also { calls += "consult"; value = key }
        override suspend fun redeemPix(betId: Long, key: String) = PrizeRedemptionResult.Redeemed().also { calls += "pix"; value = key }
        override suspend fun cancelPix(betId: Long) { calls += "cancel" }
        override suspend fun redeemMercadoPago(betId: Long, email: String) = mpResult.also { calls += "mp"; value = email }
        override suspend fun betReceiptPdf(betId: Long) = byteArrayOf(1).also { calls += "bet-pdf" }
        override suspend fun prizeReceiptPdf(betId: Long) = byteArrayOf(2).also { calls += "prize-pdf" }
    }

    companion object {
        private fun retailer(favorite: Boolean) = Retailer(1, "001", "Centro", "", "Rua", "Bairro", "Brasília", "DF", favorite)
    }
}
