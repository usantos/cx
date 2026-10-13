package br.gov.caixa.loterias.feature.marketplacefilter

import br.gov.caixa.loterias.core.domain.retail.Retailer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MarketplaceFilterReducerTest {
    private val mega = modality(2, "Mega-Sena", 60, listOf(6, 7))

    @Test
    fun `selection and text actions update their fields`() {
        val initial = MarketplaceFilterUiState()
        assertEquals(mega, initial.reduce(MarketplaceFilterAction.SelectModality(mega)).selectedModality)
        assertEquals("Centro", initial.reduce(MarketplaceFilterAction.ChangeLottery("Centro")).lottery)
        assertEquals("1,00", initial.reduce(MarketplaceFilterAction.ChangeMinimumValue("1,00")).minimumValue)
        assertEquals("2,00", initial.reduce(MarketplaceFilterAction.ChangeMaximumValue("2,00")).maximumValue)
        assertEquals("12", initial.reduce(MarketplaceFilterAction.ChangeMinimumShares("1a2")).minimumShares)
        assertEquals("34", initial.reduce(MarketplaceFilterAction.ChangeMaximumShares("3-4")).maximumShares)
        assertEquals(5, initial.reduce(MarketplaceFilterAction.SelectBetQuantity(5)).selectedBetQuantity)
        assertEquals(6, initial.reduce(MarketplaceFilterAction.SelectNumberQuantity(6)).selectedNumberQuantity)
        assertEquals(listOf(1, 2), initial.reduce(MarketplaceFilterAction.ChangeWantedNumbers(listOf(2, 1, 2))).wantedNumbers)
        assertEquals(listOf(3, 4), initial.reduce(MarketplaceFilterAction.ChangeUnwantedNumbers(listOf(4, 3))).unwantedNumbers)
    }

    @Test
    fun `changing modality resets every modality dependent filter`() {
        val state = MarketplaceFilterUiState(
            selectedBetQuantity = 4,
            selectedNumberQuantity = 25,
            wantedNumbers = listOf(1),
            unwantedNumbers = listOf(2),
        ).reduce(MarketplaceFilterAction.SelectModality(mega))

        assertEquals(mega, state.selectedModality)
        assertEquals(listOf(6, 7), state.numberOptions)
        assertNull(state.selectedBetQuantity)
        assertNull(state.selectedNumberQuantity)
        assertEquals(emptyList<Int>(), state.wantedNumbers)
        assertEquals(emptyList<Int>(), state.unwantedNumbers)
    }

    @Test
    fun `selecting all clears modality identifiers and dependent filters`() {
        val state = MarketplaceFilterUiState(
            selectedModality = mega,
            selectedBetQuantity = 4,
            selectedNumberQuantity = 6,
        ).reduce(MarketplaceFilterAction.SelectModality(allModalities))

        assertNull(state.selectedModality)
        assertEquals(allNumberQuantityOptions, state.numberOptions)
        assertNull(state.selectedBetQuantity)
        assertNull(state.selectedNumberQuantity)
    }

    @Test
    fun `selecting selected bet toggles it off`() {
        val result = MarketplaceFilterUiState(selectedBetQuantity = 4)
            .reduce(MarketplaceFilterAction.SelectBetQuantity(4))
        assertNull(result.selectedBetQuantity)
    }

    @Test
    fun `retailer selection stores display and forces all modalities`() {
        val retailer = retailer()
        val result = MarketplaceFilterUiState(selectedModality = mega)
            .reduce(MarketplaceFilterAction.SelectRetailer(retailer))

        assertEquals("000101 - Lotérica Centro", result.lottery)
        assertEquals(retailer, result.selectedRetailer)
        assertNull(result.selectedModality)
        assertEquals(emptyList<Retailer>(), result.retailerSuggestions)
    }

    @Test
    fun `editing selected retailer removes stale selection`() {
        val retailer = retailer()
        val state = MarketplaceFilterUiState(
            lottery = retailerDisplayLabel(retailer),
            selectedRetailer = retailer,
        )
        assertEquals(retailer, state.reduce(MarketplaceFilterAction.ChangeLottery(state.lottery)).selectedRetailer)
        assertNull(state.reduce(MarketplaceFilterAction.ChangeLottery("000102")).selectedRetailer)
    }

    @Test
    fun `clear preserves backend configuration and removes selections`() {
        val result = MarketplaceFilterUiState(
            modalities = listOf(allModalities, mega),
            selectedModality = mega,
            lottery = "Centro",
            selectedRetailer = retailer(),
            betOptions = listOf(1, 2),
            numberOptions = listOf(6, 7),
            selectedBetQuantity = 2,
            lotteryEnabled = false,
        ).reduce(MarketplaceFilterAction.Clear)

        assertEquals(listOf(allModalities, mega), result.modalities)
        assertEquals(listOf(1, 2), result.betOptions)
        assertEquals(allNumberQuantityOptions, result.numberOptions)
        assertEquals(false, result.lotteryEnabled)
        assertNull(result.selectedModality)
        assertNull(result.selectedBetQuantity)
        assertNull(result.selectedRetailer)
        assertEquals("", result.lottery)
    }

    @Test
    fun `terminal actions do not mutate state`() {
        val state = MarketplaceFilterUiState(lottery = "A")
        assertEquals(state, state.reduce(MarketplaceFilterAction.Apply))
        assertEquals(state, state.reduce(MarketplaceFilterAction.Back))
    }

    @Test
    fun `share validation reproduces both legacy field rules`() {
        val tooSmall = MarketplaceFilterUiState(minimumShares = "1", maximumShares = "1")
            .withShareValidation()
        assertEquals("Quantidade mínima não pode ser menor que 2", tooSmall.minimumSharesError)
        assertEquals("Quantidade máxima não pode ser menor que 2", tooSmall.maximumSharesError)
        assertEquals(false, tooSmall.canApply)

        val inverted = MarketplaceFilterUiState(minimumShares = "9", maximumShares = "4")
            .withShareValidation()
        assertEquals("Quantidade mínima não pode ser maior que a máxima", inverted.minimumSharesError)
        assertNull(inverted.maximumSharesError)

        val tooLarge = MarketplaceFilterUiState(maximumShares = "101").withShareValidation()
        assertEquals("Quantidade máxima não pode ser maior que 100", tooLarge.maximumSharesError)
        assertEquals(true, MarketplaceFilterUiState(minimumShares = "2", maximumShares = "100").withShareValidation().canApply)
    }

    private fun modality(id: Int, name: String, maximum: Int, options: List<Int>) =
        MarketplaceFilterModalityUi(id, name, name, 1, options, maximum)

    private fun retailer() = Retailer(101, "0001-01", "Lotérica Centro", "", "", "", "", "", false)
}
