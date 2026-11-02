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
import br.gov.caixa.loterias.core.designsystem.icon.loteriasCaixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaignTheme
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LoteriasCaixaDSCampaignCloverTest {
    @get:Rule val compose = createComposeRule()

    @Test fun octoberCloversMatchOriginalAssetsOnBothSurfaces() {
        compose.setContent {
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSCampaignTheme(LoteriasCaixaDSCampaign.DEFAULT, LoteriasCaixaDSCampaign.OCTOBER_ROSE) {
                    Row {
                        Image(loteriasCaixaDSLotteryClover(2, onDark = false), null, Modifier.size(64.dp).testTag("light"))
                        Image(
                            painterResource(R.drawable.legacy_campaign_trevo_megasena_outubro_rosa_fundo_branco),
                            null,
                            Modifier.size(64.dp).testTag("expected-light")
                        )
                        Image(loteriasCaixaDSLotteryClover("megasena", onDark = true), null, Modifier.size(64.dp).testTag("dark"))
                        Image(
                            painterResource(R.drawable.legacy_campaign_trevo_megasena_outubro_rosa_fundo_escuro),
                            null,
                            Modifier.size(64.dp).testTag("expected-dark")
                        )
                    }
                }
            }
        }
        compare("light", "expected-light")
        compare("dark", "expected-dark")
    }

    @Test fun quinaLotomaniaAndUnknownIdsDoNotBecomePinkByFallback() {
        compose.setContent {
            LoteriasCaixaDSTheme {
                Column {
                    for (campaign in listOf(LoteriasCaixaDSCampaign.DEFAULT, LoteriasCaixaDSCampaign.OCTOBER_ROSE)) {
                        LoteriasCaixaDSCampaignTheme(LoteriasCaixaDSCampaign.DEFAULT, campaign) {
                            Row {
                                for (id in listOf(
                                    1,
                                    CampaignCloverTestExpectations.QUINA_ID,
                                    CampaignCloverTestExpectations.UNKNOWN_LOTTERY_ID
                                )) {
                                    Image(loteriasCaixaDSLotteryClover(id), null, Modifier.size(64.dp).testTag("$campaign-$id"))
                                }
                            }
                        }
                    }
                }
            }
        }
        listOf(1, CampaignCloverTestExpectations.QUINA_ID, CampaignCloverTestExpectations.UNKNOWN_LOTTERY_ID).forEach {
            compare("DEFAULT-$it", "OCTOBER_ROSE-$it")
        }
    }

    private fun compare(actual: String, expected: String) {
        val a = compose.onNodeWithTag(actual).captureToImage().toPixelMap()
        val b = compose.onNodeWithTag(expected).captureToImage().toPixelMap()
        assertEquals(b.width, a.width)
        assertEquals(b.height, a.height)
        for (x in 0 until a.width) {
            for (y in 0 until a.height) {
                assertEquals("$actual pixel($x,$y)", b[x, y], a[x, y])
            }
        }
    }
}

private object CampaignCloverTestExpectations {
    const val QUINA_ID = 5
    const val UNKNOWN_LOTTERY_ID = 999
}
