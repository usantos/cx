package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.assertTextEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Assert.assertArrayEquals
import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel
import br.gov.caixa.loterias.feature.betting.presentation.screen.BettingLegacyLoteca
import br.gov.caixa.loterias.feature.betting.presentation.screen.BettingLegacyTeams

class BettingSportsCoordinationTest {
    @get:Rule val compose = createComposeRule()
    private val models = mutableListOf<BettingGameViewModel>()
    private val alfa = SportsTeam(1, "Time Alfa", "SP")
    private val beta = SportsTeam(2, "Time Beta", "RJ")

    @After fun cleanup() {
        models.forEach { it.viewModelScope.cancel() }
    }

    @Test fun replacingModelUpdatesTeamQueryAndSelectionWithoutTouchingThePreviousModel() {
        val first = model(20)
        val second = model(20)
        val replace = mutableStateOf(false)
        compose.setContent {
            val current = if (replace.value) second else first
            val state by current.state.collectAsState()
            CaixaDSTheme { BettingLegacyTeams(state, current) }
        }
        compose.waitUntil { first.state.value.game != null && second.state.value.game != null }
        compose.onNodeWithContentDescription("Nome do time").performTextInput("Alfa")
        compose.onNodeWithContentDescription(beta.name).assertDoesNotExist()
        compose.runOnIdle { replace.value = true }
        compose.onNodeWithContentDescription("Nome do time").performTextClearance()
        compose.onNodeWithContentDescription("Nome do time").performTextInput("Beta")
        compose.onNodeWithContentDescription(alfa.name).assertDoesNotExist()
        compose.onNodeWithContentDescription(beta.name).performClick()
        compose.onNodeWithContentDescription("Nome do time").assertTextEquals("Time Beta-RJ")
        // O rótulo selecionado restaura a lista completa, como no ramo original do filtro.
        compose.onNodeWithContentDescription(alfa.name).assertExists()
        compose.runOnIdle {
            assertEquals("Alfa", first.state.value.teamQuery)
            assertNull(first.state.value.selectedTeam)
            assertEquals(beta, second.state.value.selectedTeam)
        }
    }

    @Test fun replacingModelAndCallbacksUpdatesLotecaOutcomesQuantityAndHelp() {
        val first = model(19)
        val second = model(19)
        val version = mutableIntStateOf(0)
        val quantity = intArrayOf(0, 0)
        val help = intArrayOf(0, 0)
        compose.waitUntil { first.state.value.game != null && second.state.value.game != null }
        compose.setContent {
            val currentVersion = version.intValue
            val current = if (currentVersion == 0) first else second
            val state by current.state.collectAsState()
            CaixaDSTheme {
                BettingLegacyLoteca(state, current, onQuantity = { quantity[currentVersion]++ }, onHelp = { help[currentVersion]++ })
            }
        }
        compose.onNodeWithTag("loteca-1-HOME").performClick()
        compose.onNodeWithTag("loteca-1-HOME").assertIsSelected()
        compose.runOnIdle { version.intValue = 1 }
        compose.onNodeWithText("15 palpites").performClick()
        compose.onNodeWithText("Como jogar").performClick()
        compose.onNodeWithTag("loteca-1-AWAY").performClick()
        compose.onNodeWithTag("loteca-1-AWAY").assertIsSelected()
        compose.onNodeWithTag("loteca-count-simples").assertTextEquals("1")
        compose.runOnIdle {
            assertArrayEquals(intArrayOf(0, 1), quantity)
            assertArrayEquals(intArrayOf(0, 1), help)
            assertEquals(mapOf(1 to setOf(LotecaOutcome.HOME)), first.state.value.lotecaOutcomes)
            assertEquals(mapOf(1 to setOf(LotecaOutcome.AWAY)), second.state.value.lotecaOutcomes)
        }
    }

    @Test fun shieldFallbackAndRawOrDataUriUpdatesKeepTheSameCardGeometry() {
        val model = model(20)
        val shield = mutableStateOf<String?>(null)
        compose.waitUntil { model.state.value.game != null }
        compose.setContent {
            val state by model.state.collectAsState()
            val game = requireNotNull(state.game)
            CaixaDSTheme {
                BettingLegacyTeams(state.copy(game = game.copy(teams = listOf(alfa.copy(shieldBase64 = shield.value)))), model)
            }
        }
        fun pixels(): IntArray {
            val bitmap = compose.onNodeWithContentDescription(alfa.name).captureToImage().asAndroidBitmap()
            return IntArray(bitmap.width * bitmap.height).also {
                bitmap.getPixels(it, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                bitmap.recycle()
            }
        }
        fun bounds() = compose.onNodeWithContentDescription(alfa.name).fetchSemanticsNode().boundsInRoot
        val originalBounds = bounds()
        assertEquals(with(compose.density) { 115.dp.toPx() }, originalBounds.height, 1f)
        val fallback = pixels()
        compose.runOnIdle { shield.value = "not-base64" }
        assertArrayEquals(fallback, pixels())
        val encoded = ByteArrayOutputStream().use { bytes ->
            val bitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888).apply { eraseColor(android.graphics.Color.RED) }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
            bitmap.recycle()
            Base64.encodeToString(bytes.toByteArray(), Base64.NO_WRAP)
        }
        compose.runOnIdle { shield.value = encoded }
        val raw = pixels()
        assertTrue(raw.count { it == android.graphics.Color.RED } > 0)
        assertEquals(originalBounds, bounds())
        compose.runOnIdle { shield.value = "data:image/png;base64,$encoded" }
        assertArrayEquals(raw, pixels())
        assertEquals(originalBounds, bounds())
        compose.runOnIdle { shield.value = null }
        assertArrayEquals(fallback, pixels())
    }

    private fun model(id: Int): BettingGameViewModel {
        val game = LotteryGameParameters(
            LotteryModality(id, "Modalidade", if (id == 19) "LOTECA" else "TIMEMANIA"),
            1275, "18/10/2026", 80, if (id == 19) 15 else 10, if (id == 19) 26 else 10, 1, listOf(0),
            listOf(BetPrice(15, 0, BigDecimal("4.00"), 1, 0)), teams = listOf(alfa, beta),
            matches = (1..14).map { SportsMatch(it, alfa, beta) }
        )
        return BettingGameViewModel(
            id,
            GetBettingParametersUseCase { AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 50, listOf(game))) },
            AddBetToCartUseCase { error("Rendering and selections must not submit a bet") },
            dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        ).also(models::add)
    }
}
