package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaignTheme
import br.gov.caixa.loterias.core.designsystem.icon.caixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme

class CaixaDSCampaignCloverTest {
    @get:Rule val compose = createComposeRule()

    @Test fun octoberCloversMatchOriginalAssetsOnBothSurfaces() {
        compose.setContent {
            CaixaDSTheme {
                CaixaDSCampaignTheme(CaixaDSCampaign.DEFAULT, CaixaDSCampaign.OCTOBER_ROSE) {
                    Row {
                        Image(caixaDSLotteryClover(2, onDark = false), null, Modifier.size(64.dp).testTag("light"))
                        Image(painterResource(R.drawable.legacy_campaign_trevo_megasena_outubro_rosa_fundo_branco), null,
                            Modifier.size(64.dp).testTag("expected-light"))
                        Image(caixaDSLotteryClover("megasena", onDark = true), null, Modifier.size(64.dp).testTag("dark"))
                        Image(painterResource(R.drawable.legacy_campaign_trevo_megasena_outubro_rosa_fundo_escuro), null,
                            Modifier.size(64.dp).testTag("expected-dark"))
                    }
                }
            }
        }
        compare("light", "expected-light")
        compare("dark", "expected-dark")
    }

    @Test fun quinaLotomaniaAndUnknownIdsDoNotBecomePinkByFallback() {
        compose.setContent {
            CaixaDSTheme {
                Column {
                    for (campaign in listOf(CaixaDSCampaign.DEFAULT, CaixaDSCampaign.OCTOBER_ROSE)) {
                        CaixaDSCampaignTheme(CaixaDSCampaign.DEFAULT, campaign) {
                            Row {
                                for (id in listOf(1, 5, 999)) {
                                    Image(caixaDSLotteryClover(id), null, Modifier.size(64.dp).testTag("$campaign-$id"))
                                }
                            }
                        }
                    }
                }
            }
        }
        listOf(1,5,999).forEach { compare("DEFAULT-$it", "OCTOBER_ROSE-$it") }
    }

    private fun compare(actual: String, expected: String) {
        val a = compose.onNodeWithTag(actual).captureToImage().toPixelMap()
        val b = compose.onNodeWithTag(expected).captureToImage().toPixelMap()
        assertEquals(b.width, a.width)
        assertEquals(b.height, a.height)
        for (x in 0 until a.width) for (y in 0 until a.height) {
            assertEquals("$actual pixel($x,$y)", b[x,y], a[x,y])
        }
    }
}
