package br.gov.caixa.loterias.core.designsystem

import br.gov.caixa.loterias.core.designsystem.artwork.resolveCaixaDSMarketplaceFilterClover
import br.gov.caixa.loterias.core.designsystem.artwork.resolveCaixaDSMarketplaceTrapeze
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CaixaDSMarketplaceArtworkTest {
    @Test fun `normal Mega keeps its native green artwork`() {
        assertEquals(R.drawable.legacy_marketplace_trapeze_megasena, resolveCaixaDSMarketplaceTrapeze(2))
        assertEquals(
            R.drawable.legacy_marketplace_filter_trevo_megasena_fundo_escuro,
            resolveCaixaDSMarketplaceFilterClover(2, selected = true)
        )
    }

    @Test fun `September blue cannot receive a green Mega trapeze`() {
        assertNull(resolveCaixaDSMarketplaceTrapeze(2, campaign = CaixaDSCampaign.SEPTEMBER_BLUE))
    }

    @Test fun `September blue cannot receive green Mega clovers in either state`() {
        for (selected in listOf(false, true)) {
            assertNull(resolveCaixaDSMarketplaceFilterClover(2, selected = selected, campaign = CaixaDSCampaign.SEPTEMBER_BLUE))
        }
    }

    @Test fun `Mega campaigns leave other modalities native`() {
        for (campaign in CaixaDSCampaign.entries) {
            assertEquals(R.drawable.legacy_marketplace_trapeze_lotofacil, resolveCaixaDSMarketplaceTrapeze(8, campaign = campaign))
            assertEquals(
                R.drawable.legacy_marketplace_filter_trevo_lotofacil_fundo_branco,
                resolveCaixaDSMarketplaceFilterClover(8, selected = false, campaign = campaign)
            )
        }
    }
}
