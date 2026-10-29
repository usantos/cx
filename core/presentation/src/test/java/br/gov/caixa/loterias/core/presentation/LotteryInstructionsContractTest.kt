// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.presentation

import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign
import br.gov.caixa.loterias.core.presentation.mapper.toDSCampaign
import br.gov.caixa.loterias.core.presentation.rule.lotteryGameRulesHtml
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LotteryInstructionsContractTest {
    @Test fun `each modality describes its actual selection and supplementary rules`() {
        val expectations = mapOf(
            11 to "mês de sorte", 18 to "dois sorteios", 13 to "duplo", 19 to "duplo", 8 to "15 a 20 números",
            5 to "50 números", 16 to "aposta-espelho", 2 to "6 a 20 números", 1 to "5 a 15 números", 3 to "5 a 15 números",
            20 to "time do coração", 7 to "7 colunas", 9 to "2 a 6 trevos"
        )
        expectations.forEach { (id, expected) ->
            assertTrue("modality $id must explain $expected", lotteryGameRulesHtml(id).contains(expected))
        }
        assertEquals(lotteryGameRulesHtml(13), lotteryGameRulesHtml(19))
        assertEquals(lotteryGameRulesHtml(5), lotteryGameRulesHtml(16))
        assertEquals("", lotteryGameRulesHtml(Int.MIN_VALUE))
    }

    @Test fun `campaign adapter keeps optional default and both commemorative identities`() {
        assertEquals(CaixaDSCampaign.DEFAULT, null.toDSCampaign())
        assertEquals(CaixaDSCampaign.OCTOBER_ROSE, CommemorativeCampaign.OUTUBRO_ROSA.toDSCampaign())
        assertEquals(CaixaDSCampaign.SEPTEMBER_BLUE, CommemorativeCampaign.SETEMBRO_AZUL.toDSCampaign())
    }
}
