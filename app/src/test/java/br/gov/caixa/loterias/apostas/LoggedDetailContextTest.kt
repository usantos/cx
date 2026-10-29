package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.navigation.state.DetailRestorationOutcome
import br.gov.caixa.loterias.core.navigation.state.LoggedDetailContext
import br.gov.caixa.loterias.core.navigation.state.LoggedDetailContextCodec
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotogolMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.home.presentation.mapper.date
import br.gov.caixa.loterias.feature.home.presentation.mapper.prize
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Test

class LoggedDetailContextTest {
    @Test fun restoresDetailAndSportsEditSeedForSameOwner() {
        val team = SportsTeam(7, "Time", countryNumber = 55, selectionIndicator = true)
        val match = SportsMatch(1, team, SportsTeam(2, "Visitante"), "Legenda", "A")
        val bet = ConfirmedBet(17, "Loteca", 42, "05/10/2026", "Confirmada", listOf(1, 2),
            prize = BigDecimal("123.45"), originalRecurringBet = true, redeemedPrize = true,
            exchangeBetId = 25, modalityId = 9, statusId = 3, contestTypeId = 2, poolShare = true,
            surprise = true, time = "12:34", numberColumns = listOf(listOf(1), listOf(2)),
            clovers = listOf(3, 4), favoriteTeam = team, luckyMonth = LuckyMonth(5, "Maio", "MAI"),
            lotecaSelections = listOf(LotecaMatchSelection(match, true, false, true)),
            lotogolSelections = listOf(LotogolMatchSelection(match, 2, 1)), recurringDraws = 5,
            quantity = 8, mirror = true, exchange = true, combo = true)
        val purchase = Purchase(11, "05/10/2026", "Paga", "Pix", BigDecimal("99.01"), "pix", "expira", 3, 4, "12:34", "e2e")
        val filter = MarketplaceFilter("1.23", "4.56", 1, 4, 5, 6, "123", "Mega", listOf(7, 8), listOf(9), 1, 2)
        val retailer = Retailer(123, "001", "Nome", "Legal", "Rua", "Bairro", "Cidade", "SP", true)
        val original = LoggedDetailContext("owner").apply {
            selectedBet = bet; betToEdit = bet; selectedPurchase = purchase
            marketplaceFilter = filter; selectedMarketplaceRetailer = retailer; checkedTicketBets = listOf(bet)
        }
        val restored = LoggedDetailContextCodec.restore(LoggedDetailContextCodec.save(original)!!, "owner")!!
        assertEquals(bet, restored.selectedBet)
        assertEquals(bet, restored.betToEdit)
        assertEquals(purchase, restored.selectedPurchase)
        assertEquals(filter, restored.marketplaceFilter)
        assertEquals(retailer, restored.selectedMarketplaceRetailer)
        assertEquals(listOf(bet), restored.checkedTicketBets)
    }
    @Test fun restoredMarketplaceKeepsClearBaselineAndAppliedRetailerForSameOwnerOnly() {
        val baseline = MarketplaceFilter.forModality(2, 1)
        val retailer = Retailer(123, "000123", "Lotérica escolhida", "", "", "", "", "SP", false)
        val original = LoggedDetailContext("owner").apply {
            marketplaceClearFilter = baseline
            marketplaceFilter = baseline.withLottery("123")
            lastAppliedFilterRetailer = retailer
            retailerMarketplaceFilter = MarketplaceFilter.empty().withLottery("123")
            marketplaceFilterApplied = true
            retailerMarketplaceFilterApplied = true
        }
        val bytes = LoggedDetailContextCodec.save(original)!!
        val pending = LoggedDetailContextCodec.defer(bytes)!!
        pending.reconcile("owner")
        assertEquals(baseline, pending.marketplaceClearFilter)
        assertEquals(retailer, pending.lastAppliedFilterRetailer)
        assertEquals("123", pending.retailerMarketplaceFilter.lottery)
        assertNull(pending.retailerMarketplaceFilter.modalityId)
        assertTrue(pending.marketplaceFilterApplied)
        assertTrue(pending.retailerMarketplaceFilterApplied)
        pending.clear()
        assertEquals(MarketplaceFilter.empty(), pending.marketplaceClearFilter)
        assertNull(pending.lastAppliedFilterRetailer)
        assertEquals(MarketplaceFilter.empty(), pending.retailerMarketplaceFilter)
        assertFalse(pending.marketplaceFilterApplied)
        assertFalse(pending.retailerMarketplaceFilterApplied)
        assertNull(LoggedDetailContextCodec.restore(bytes, "other"))
    }

    @Test fun previousVersionSnapshotRestoresItsModalityAsClearBaseline() {
        val bytes = java.io.ByteArrayOutputStream()
        java.io.DataOutputStream(bytes).use { out ->
            out.writeInt(1); out.writeUTF("owner")
            repeat(3) { out.writeBoolean(false) }
            out.writeUTF(""); out.writeUTF("")
            repeat(4) { out.writeBoolean(false) }
            out.writeUTF(""); out.writeUTF("")
            repeat(2) { out.writeInt(0) }
            out.writeBoolean(true); out.writeInt(2)
            out.writeBoolean(true); out.writeInt(1)
            out.writeBoolean(false); out.writeInt(0)
        }
        val pending = LoggedDetailContextCodec.defer(bytes.toByteArray())!!
        pending.reconcile("owner")
        assertEquals(MarketplaceFilter.forModality(2, 1), pending.marketplaceClearFilter)
        assertNull(pending.lastAppliedFilterRetailer)
        assertEquals(MarketplaceFilter.empty(), pending.retailerMarketplaceFilter)
    }

    @Test fun refusesAnotherOwnerAndLoggedOutSession() {
        val bytes = LoggedDetailContextCodec.save(LoggedDetailContext("owner"))!!
        assertNull(LoggedDetailContextCodec.restore(bytes, "another"))
        assertNull(LoggedDetailContextCodec.restore(bytes, null))
        assertNull(LoggedDetailContextCodec.save(LoggedDetailContext(null)))
    }
    @Test fun malformedAndOversizedPayloadsAreDiscarded() {
        assertNull(LoggedDetailContextCodec.restore(byteArrayOf(1, 2, 3), "owner"))
        assertNull(LoggedDetailContextCodec.restore(ByteArray(200_000), "owner"))
        val context = LoggedDetailContext("owner").apply {
            selectedPurchase = Purchase(1, "date", "status", "pix", BigDecimal.ONE, "x".repeat(200_000))
        }
        assertNull(LoggedDetailContextCodec.save(context))
    }
    @Test fun logoutClearRemovesAllPrivateSelections() {
        val context = LoggedDetailContext("owner").apply {
            selectedBet = ConfirmedBet(1, "Mega", 1, "date", "status", listOf(1))
            betToEdit = selectedBet; checkedTicketBets = listOf(selectedBet!!)
            marketplaceFilter = MarketplaceFilter.forModality(1, 2)
        }
        context.clear()
        assertNull(context.selectedBet); assertNull(context.betToEdit)
        assertEquals(emptyList<ConfirmedBet>(), context.checkedTicketBets)
        assertEquals(MarketplaceFilter.empty(), context.marketplaceFilter)
    }
    @Test fun defersPrivateFieldsUntilSameOwnerAuthenticatesAgain() {
        val original = LoggedDetailContext("owner").apply {
            selectedBet = ConfirmedBet(17, "Mega", 42, "date", "Confirmada", listOf(1, 2))
        }
        val pending = LoggedDetailContextCodec.defer(LoggedDetailContextCodec.save(original)!!)!!
        pending.reconcile(null)
        assertEquals(DetailRestorationOutcome.Pending, pending.restorationOutcome)
        assertNull(pending.selectedBet)
        val recreatedBeforeAuthentication = LoggedDetailContextCodec.defer(LoggedDetailContextCodec.save(pending)!!)!!
        recreatedBeforeAuthentication.reconcile("owner")
        assertEquals(DetailRestorationOutcome.Restored, recreatedBeforeAuthentication.restorationOutcome)
        assertEquals(original.selectedBet, recreatedBeforeAuthentication.selectedBet)
    }
    @Test fun foreignAuthenticationPermanentlyDiscardsPendingSnapshot() {
        val original = LoggedDetailContext("owner").apply {
            selectedBet = ConfirmedBet(17, "Mega", 42, "date", "Confirmada", listOf(1, 2))
        }
        val pending = LoggedDetailContextCodec.defer(LoggedDetailContextCodec.save(original)!!)!!
        pending.reconcile("another")
        assertEquals(DetailRestorationOutcome.Discarded, pending.restorationOutcome)
        assertNull(pending.selectedBet)
        pending.reconcile("owner")
        assertNull(pending.selectedBet)
    }
    @Test fun explicitLogoutDiscardsPendingSnapshotBeforeAuthentication() {
        val original = LoggedDetailContext("owner").apply {
            selectedBet = ConfirmedBet(17, "Mega", 42, "date", "Confirmada", listOf(1, 2))
        }
        val pending = LoggedDetailContextCodec.defer(LoggedDetailContextCodec.save(original)!!)!!
        pending.clear()
        assertEquals(DetailRestorationOutcome.None, pending.restorationOutcome)
        assertNull(LoggedDetailContextCodec.save(pending))
        pending.reconcile("owner")
        assertNull(pending.selectedBet)
    }

    @Test fun losingAuthenticationImmediatelyHidesCurrentPrivateFields() {
        val bet = ConfirmedBet(17, "Mega", 42, "date", "Confirmada", listOf(1, 2))
        val context = LoggedDetailContext("owner").apply { selectedBet = bet; betToEdit = bet }
        context.reconcile(null)
        assertNull(context.selectedBet)
        assertNull(context.betToEdit)
        assertEquals(DetailRestorationOutcome.Pending, context.restorationOutcome)
        context.reconcile("owner")
        assertEquals(bet, context.selectedBet)
        assertEquals(bet, context.betToEdit)
    }

}
