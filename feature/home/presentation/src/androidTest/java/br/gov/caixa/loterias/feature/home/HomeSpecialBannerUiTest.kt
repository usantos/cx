package br.gov.caixa.loterias.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.home.presentation.components.LotteryBanner
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeSpecialBannerResource
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** Referência de origem: ROSA dca5d9d9, cl_especiais de card_modalidade e EstiloModalidadeMKP. */
class HomeSpecialBannerUiTest {
    @get:Rule val compose = createComposeRule()
    private val mega = HomeLottery(2, "ESPECIAL", "megasena", "Mega da Virada", 3073,
        BigDecimal.TEN, BigDecimal.ONE, "", "", true, HomeLotteryKind.LOTTERY,
        Color.Green, Color.Green, Color.White, R.drawable.legacy_home_trevo_megasena_fundo_escuro,
        R.drawable.icon_data_megasena, R.drawable.icon_horario_megasena)

    @Test fun specialModalityArtworkRequiresSpecialContestAndSupportsLegacyFallback() {
        val assets = mapOf(
            "megasena" to R.drawable.legacy_home_special_mega_virada_novo,
            "quina" to R.drawable.legacy_home_special_quina_sao_joao_novo,
            "duplasena" to R.drawable.legacy_home_special_dupla_pascoa,
            "lotofacil" to R.drawable.legacy_home_special_lotofacil_independencia,
            "loteca" to R.drawable.legacy_home_special_loteca_copa,
        )
        assets.forEach { (code, resource) ->
            assertEquals(resource, homeSpecialBannerResource(mega.copy(resultCode = code)))
            assertNull(homeSpecialBannerResource(mega.copy(resultCode = code, contestType = "NORMAL")))
        }
        assertNull(homeSpecialBannerResource(mega.copy(resultCode = "timemania")))
        assertNull(homeSpecialBannerResource(mega.copy(kind = HomeLotteryKind.COMBO)))
    }

    @Test fun configuredSpecialHeadersUseExactLegacyTwoLineTitles() {
        val lottery = mutableStateOf(mega.copy(specialBanner = br.gov.caixa.loterias.feature.home.presentation.state.HomeSpecialBanner.MEGA_THIRTY_YEARS))
        compose.setContent { CaixaDSTheme(darkTheme = false) { Box(Modifier.width(280.dp)) { LotteryBanner(lottery.value) } } }
        compose.onNodeWithText("mega-sena\n30 anos").assertIsDisplayed()
        assertEquals(R.drawable.legacy_home_special_cabecalho_mega_trinta_anos_carrossel, homeSpecialBannerResource(lottery.value))
        compose.runOnIdle { lottery.value = mega.copy(resultCode = "loteca", specialBanner = br.gov.caixa.loterias.feature.home.presentation.state.HomeSpecialBanner.LOTECA_FATHERS_DAY) }
        compose.onNodeWithText("loteca\ndos pais").assertIsDisplayed()
        assertEquals(R.drawable.legacy_home_special_cabecalho_loteca_pais_carrossel_alta, homeSpecialBannerResource(lottery.value))
    }

    @Test fun specialHeaderUsesSeventyOneDpHeightAndNinetyTenDpTextMargins() {
        compose.setContent { CaixaDSTheme(darkTheme = false) { Box(Modifier.width(280.dp)) { LotteryBanner(mega) } } }
        compose.onNodeWithTag("home-special-banner").assertWidthIsEqualTo(280.dp).assertHeightIsEqualTo(71.dp)
        val panel = compose.onNodeWithTag("home-special-banner").getUnclippedBoundsInRoot()
        val label = compose.onNodeWithTag("home-special-banner-title").getUnclippedBoundsInRoot()
        assertEquals(90f, (label.left - panel.left).value, .5f)
        assertEquals(10f, (panel.right - label.right).value, .5f)
        compose.onNodeWithText("mega da virada").assertIsDisplayed()
        val layout = mutableListOf<TextLayoutResult>()
        compose.onNodeWithTag("home-special-banner-title").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layout) }
        // Valores sp do XML são arredondados para um pixel físico neste dispositivo.
        assertEquals(18f, layout.last().layoutInput.style.fontSize.value, .5f)
    }

    @Test fun longSpecialTitleAutoFitsAndNormalContestKeepsOriginalBanner() {
        val lottery = mutableStateOf(mega.copy(name = "Lotofácil da Independência concurso especial comemorativo nacional"))
        compose.setContent { CaixaDSTheme(darkTheme = false) { Box(Modifier.width(280.dp)) { LotteryBanner(lottery.value) } } }
        val layout = mutableListOf<TextLayoutResult>()
        compose.onNodeWithTag("home-special-banner-title").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layout) }
        val result = layout.last()
        assertTrue(result.layoutInput.style.fontSize.value in 7.5f..18.5f)
        assertFalse(result.didOverflowHeight)
        assertFalse(result.didOverflowWidth)
        compose.runOnIdle { lottery.value = mega.copy(contestType = "NORMAL", name = "Mega-Sena") }
        compose.onNodeWithTag("home-special-banner").assertDoesNotExist()
        assertEquals(85f, compose.onNodeWithText("mega-sena").getUnclippedBoundsInRoot().left.value, .5f)
    }
}
