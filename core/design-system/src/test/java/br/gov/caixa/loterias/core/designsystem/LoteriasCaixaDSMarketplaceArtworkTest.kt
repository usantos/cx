package br.gov.caixa.loterias.core.designsystem

import br.gov.caixa.loterias.core.designsystem.artwork.resolveLoteriasCaixaDSMarketplaceFilterClover
import br.gov.caixa.loterias.core.designsystem.artwork.resolveLoteriasCaixaDSMarketplaceTrapeze
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LoteriasCaixaDSMarketplaceArtworkTest {
    @Test fun `normal Mega keeps its native green artwork`() {
        assertEquals(R.drawable.legacy_marketplace_trapeze_megasena, resolveLoteriasCaixaDSMarketplaceTrapeze(2))
        assertEquals(
            R.drawable.legacy_marketplace_filter_trevo_megasena_fundo_escuro,
            resolveLoteriasCaixaDSMarketplaceFilterClover(2, selected = true)
        )
    }

    @Test fun `September blue cannot receive a green Mega trapeze`() {
        assertNull(resolveLoteriasCaixaDSMarketplaceTrapeze(2, campaign = LoteriasCaixaDSCampaign.SEPTEMBER_BLUE))
    }

    @Test fun `September blue cannot receive green Mega clovers in either state`() {
        for (selected in listOf(false, true)) {
            assertNull(
                resolveLoteriasCaixaDSMarketplaceFilterClover(2, selected = selected, campaign = LoteriasCaixaDSCampaign.SEPTEMBER_BLUE)
            )
        }
    }

    @Test fun `Mega campaigns leave other modalities native`() {
        for (campaign in LoteriasCaixaDSCampaign.entries) {
            assertEquals(
                R.drawable.legacy_marketplace_trapeze_lotofacil,
                resolveLoteriasCaixaDSMarketplaceTrapeze(MarketplaceArtworkTestExpectations.LOTOFACIL_ID, campaign = campaign)
            )
            assertEquals(
                R.drawable.legacy_marketplace_filter_trevo_lotofacil_fundo_branco,
                resolveLoteriasCaixaDSMarketplaceFilterClover(
                    MarketplaceArtworkTestExpectations.LOTOFACIL_ID,
                    selected = false,
                    campaign = campaign
                )
            )
        }
    }
}

private object MarketplaceArtworkTestExpectations {
    const val LOTOFACIL_ID = 8
}
