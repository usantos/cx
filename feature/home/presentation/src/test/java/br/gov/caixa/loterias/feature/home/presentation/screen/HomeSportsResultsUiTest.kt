// Literais numéricos descrevem entradas explícitas de teste e valores esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.home.presentation.screen

import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import br.gov.caixa.loterias.core.designsystem.testing.captureToImage
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam
import java.io.ByteArrayOutputStream
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class HomeSportsResultsUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun lotecaUsesTeamRegionsLegacyDayAndMatchPagination() {
        val first = DrawSportsMatch(
            1,
            DrawSportsTeam("Casa", "SP", won = true, countryCode = "BRA"),
            DrawSportsTeam("Fora", "RJ", won = false, countryCode = "BRA"),
            legendDescription = "ABCDInformação"
        )
        val second = DrawSportsMatch(
            2,
            DrawSportsTeam("Brasil", countryCode = "BRA", nationalTeam = true),
            DrawSportsTeam("Chile", countryCode = "CHI", nationalTeam = true),
            draw = true
        )
        show(result().copy(lotecaMatches = listOf(first, second)))
        compose.onNodeWithText("Domingo").assertIsDisplayed()
        compose.onNodeWithText("Casa").assertIsDisplayed()
        compose.onNodeWithText("/SP  (Informação)").assertIsDisplayed()
        compose.onNodeWithText("1 de 2").assertIsDisplayed()
        compose.onNodeWithTag("home-result-sports-pager").performTouchInput { swipeLeft() }
        compose.onNodeWithText("2 de 2").assertIsDisplayed()
        compose.onNodeWithText("Brasil").assertIsDisplayed()
        compose.onRoot().captureToImage()
    }

    @Test fun lotogolUsesExplicitScoresAndCapsFallbackAtFourOrMore() {
        show(
            result().copy(
                lotogolMatches = listOf(
                    DrawSportsMatch(
                        1,
                        DrawSportsTeam("Brasil", score = "2", countryCode = "BRA", nationalTeam = true),
                        DrawSportsTeam("Visitante", state = "SP"),
                        homeScoreIndex = 1,
                        awayScoreIndex = 5
                    )
                )
            )
        )
        compose.onNodeWithText("Brasil / BRA").assertIsDisplayed()
        compose.onNodeWithText("Visitante / SP").assertIsDisplayed()
        compose.onNodeWithText("2").assertIsDisplayed()
        compose.onNodeWithText("4 ou mais").assertIsDisplayed()
        compose.onNodeWithText("Empate").assertDoesNotExist()
    }

    @Test fun lotogolRetainsZeroScoreIndexAndAbsentScoreAsEmpty() {
        show(
            result().copy(
                lotogolMatches = listOf(
                    DrawSportsMatch(
                        null,
                        DrawSportsTeam("Mandante"),
                        DrawSportsTeam("Visitante"),
                        homeScoreIndex = 0
                    )
                )
            )
        )
        compose.onNodeWithText("0").assertIsDisplayed()
        compose.onNodeWithText("Mandante / ").assertIsDisplayed()
        compose.onNodeWithText("1 de 1").assertIsDisplayed()
    }

    @Test fun shieldsAcceptPlainAndDataUriPngAndRejectInvalidPayloadsWithoutCrashing() {
        val bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888).apply { eraseColor(android.graphics.Color.RED) }
        val bytes = ByteArrayOutputStream().also { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }.toByteArray()
        val encoded = Base64.encodeToString(bytes, Base64.NO_WRAP)
        compose.setContent {
            CaixaDSTheme {
                Column {
                    HomeResultShield(encoded)
                    HomeResultShield("data:image/png;base64,$encoded")
                    HomeResultShield("%%%invalid")
                    HomeResultShield(null)
                }
            }
        }
        val image = compose.onRoot().captureToImage()
        val pixels = image.toPixelMap()
        assertTrue(
            "The decoded PNG shields must actually be drawn",
            (0 until image.height).any { y ->
                (0 until image.width).any { x -> pixels[x, y].red > .9f && pixels[x, y].green < .1f }
            }
        )
    }

    private fun show(result: DrawResult) {
        compose.setContent { CaixaDSTheme { HomeSportsResults(result, Color.Black) } }
    }

    private fun result() = DrawResult(
        "loteca", 123, "22/10/2026", "", emptyList(), emptyList(), emptyList(),
        false, "", "", "", emptyList()
    )
}
