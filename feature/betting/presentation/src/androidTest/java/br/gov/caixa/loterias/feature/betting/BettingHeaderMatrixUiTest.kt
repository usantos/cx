package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.onRoot
import android.util.TypedValue
import kotlin.math.roundToInt

import br.gov.caixa.loterias.feature.betting.R

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingLegacyGameHeader
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.core.designsystem.R as DSR

/** Expectativas literais de paleta e arte de EstiloModalidadeMKP e cabecalho_simula da CX. */
class BettingHeaderMatrixUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun allNormalModalitiesRetainNativeColorsFontsAndMetadataSpacing() {
        val current = mutableStateOf(state(2, "mega-sena"))
        compose.setContent { CaixaDSTheme { BettingLegacyGameHeader(current.value, {}, {}, null) } }
        data class Entry(val id: Int, val name: String, val background: Long, val ink: Long = 0xFFFFFFFF)
        listOf(
            Entry(2, "mega-sena", 0xFF00A651), Entry(3, "quina", 0xFF2E3192),
            Entry(1, "quina", 0xFF2E3192), Entry(8, "lotofácil", 0xFF91278F),
            Entry(5, "lotomania", 0xFFE67200), Entry(16, "lotomania", 0xFFE67200),
            Entry(9, "+milionária", 0xFF2A3580), Entry(7, "super sete", 0xFFBED730, 0xFF038141),
            Entry(19, "loteca", 0xFFED1C24), Entry(20, "timemania", 0xFFFFF200, 0xFF038141),
            Entry(11, "dia de sorte", 0xFFE3C021, 0xFF7E6906), Entry(18, "dupla sena", 0xFFA62B43),
            // EstiloModalidadeMKP nativo não tem caso para Lotogol e usa seu cabeçalho verde padrão.
            Entry(14, "lotogol", 0xFF00A651)
        ).forEach { entry ->
            compose.runOnIdle { current.value = state(entry.id, entry.name) }
            val title = layout(entry.name)
            assertEquals(entry.name, Color(entry.ink), title.layoutInput.style.color)
            assertEquals(entry.name, 16f, title.layoutInput.style.fontSize.value, .01f)
            assertEquals(CaixaDSFuturaFontFamily, title.layoutInput.style.fontFamily)
            assertFalse(entry.name, title.hasVisualOverflow)
            val label = layout("Concurso")
            val number = layout("3073")
            assertEquals(CaixaDSFontFamily, label.layoutInput.style.fontFamily)
            assertEquals(xmlFontSize(14f), label.layoutInput.style.fontSize.value, .01f)
            assertEquals(xmlFontSize(12f), number.layoutInput.style.fontSize.value, .01f)
            val image = compose.onRoot().captureToImage().asAndroidBitmap()
            assertEquals(entry.name, entry.background.toInt(), image.getPixel(2, image.height - 2))
            val back = compose.onNodeWithContentDescription("Voltar").fetchSemanticsNode().boundsInRoot
            val help = compose.onNodeWithContentDescription("Dúvidas").fetchSemanticsNode().boundsInRoot
            assertEquals(with(compose.density) { 48.dp.toPx() }, back.width, 1f)
            assertEquals(back.width, help.width, 1f)
            compose.onNodeWithText(if (entry.id == 19) "Resultado" else "Sorteio", useUnmergedTree = true).assertExists()
            save("normal-${entry.id}", image)
        }
    }

    @Test fun specialTitlesFontReductionsAndArtworkFollowTheNativeSimulationConsumer() {
        val current = mutableStateOf(state(2, "mega-sena"))
        compose.setContent { CaixaDSTheme { BettingLegacyGameHeader(current.value, {}, {}, null) } }
        data class Entry(
            val id: Int,
            val name: String,
            val title: String,
            val font: Float,
            val art: Int,
            val config: HomeSpecialContests = HomeSpecialContests(),
        )
        listOf(
            Entry(2, "mega-sena", "mega da \nvirada", 16f, DSR.drawable.legacy_campaign_cabecalho_mega_da_virada_detalhe_aposta),
            Entry(3, "quina", "quina de \nsão joão", 16f, R.drawable.betting_quina_special_header),
            Entry(1, "quina", "quina de \nsão joão", 16f, R.drawable.betting_quina_special_header),
            Entry(8, "lotofácil", "lotofácil da \nindependência", 12.8f, R.drawable.betting_lotofacil_special_header),
            Entry(18, "dupla sena", "dupla de \npáscoa", 16f, R.drawable.betting_dupla_easter_detail_header),
            Entry(19, "loteca", "loteca especial", 12.8f, DSR.drawable.legacy_marketplace_loteca_copa_simples),
            Entry(2, "mega-sena", "mega-sena\n30 anos", 16f, R.drawable.betting_mega_thirty_years_header, HomeSpecialContests(3073)),
            // LotecaActivity usa CabecalhoSimulacaoFragment: a configuração Pais não substitui seu título e arte anuais.
            Entry(
                19,
                "loteca",
                "loteca especial",
                12.8f,
                DSR.drawable.legacy_marketplace_loteca_copa_simples,
                HomeSpecialContests(lotecaFathersDayContest = 3073)
            ),
            // Coincidir apenas o número do concurso em outra modalidade nunca seleciona Mega 30.
            Entry(3, "quina", "quina de \nsão joão", 16f, R.drawable.betting_quina_special_header, HomeSpecialContests(3073))
        ).forEachIndexed { index, entry ->
            compose.runOnIdle { current.value = state(entry.id, entry.name, "ESPECIAL").copy(specialContests = entry.config) }
            val result = layout(entry.title)
            assertEquals(entry.title, entry.font, result.layoutInput.style.fontSize.value, .01f)
            assertFalse(entry.title, result.hasVisualOverflow)
            val image = compose.onRoot().captureToImage().asAndroidBitmap()
            save("special-$index-${entry.id}", image)
            assertNativeArtworkInClearMargin(entry.art, image)
        }
        compose.runOnIdle { current.value = state(2, "mega-sena").copy(specialContests = HomeSpecialContests(3073)) }
        compose.onNodeWithText("mega-sena").assertExists()
        compose.onNodeWithText("mega-sena\n30 anos").assertDoesNotExist()
    }

    @Test fun octoberRoseChangesOnlyMegaPaletteAndRetainsSpecialArtwork() {
        val current = mutableStateOf(state(2, "mega-sena"))
        compose.setContent {
            CompositionLocalProvider(LocalCaixaDSMegaSenaCampaign provides CaixaDSCampaign.OCTOBER_ROSE) {
                CaixaDSTheme { BettingLegacyGameHeader(current.value, {}, {}, null) }
            }
        }
        var image = compose.onRoot().captureToImage().asAndroidBitmap()
        assertEquals(0xFFF4ADBC.toInt(), image.getPixel(2, image.height - 2))
        assertEquals(Color(0xFF9C005C), layout("mega-sena").layoutInput.style.color)
        compose.runOnIdle { current.value = state(2, "mega-sena", "ESPECIAL") }
        image = compose.onRoot().captureToImage().asAndroidBitmap()
        assertEquals(Color(0xFF9C005C), layout("mega da \nvirada").layoutInput.style.color)
        assertNativeArtworkInClearMargin(DSR.drawable.legacy_campaign_cabecalho_mega_da_virada_detalhe_aposta, image)
        compose.runOnIdle { current.value = state(20, "timemania") }
        image = compose.onRoot().captureToImage().asAndroidBitmap()
        assertEquals(0xFFFFF200.toInt(), image.getPixel(2, image.height - 2))
        assertEquals(Color(0xFF038141), layout("timemania").layoutInput.style.color)
    }

    private fun assertNativeArtworkInClearMargin(resource: Int, actual: Bitmap) {
        val height = with(compose.density) { 48.dp.roundToPx() }
        val expected = Bitmap.createBitmap(actual.width, height, Bitmap.Config.ARGB_8888)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val drawable = requireNotNull(ContextCompat.getDrawable(context, resource))
        drawable.setBounds(0, 0, expected.width, expected.height)
        Canvas(expected).apply {
            drawColor(android.graphics.Color.WHITE)
            drawable.draw(this)
        }
        save("expected-$resource", expected)
        val top = actual.height - height
        for (y in 2 until height - 2 step 7) {
            val want = expected.getPixel(2, y)
            val got = actual.getPixel(2, top + y)
            for (shift in listOf(0, 8, 16)) {
                assertTrue(
                    "Artwork $resource y=$y expected=${Integer.toHexString(
                        want
                    )} actual=${Integer.toHexString(got)} height=${actual.height}",
                    kotlin.math.abs((want shr shift and 255) - (got shr shift and 255)) <= 2
                )
            }
        }
    }

    private fun layout(text: String): TextLayoutResult {
        val results = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(text, useUnmergedTree = true).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        return results.single()
    }
    private fun xmlFontSize(size: Float): Float = with(compose.density) {
        // O XML arredonda cada dimensão para pixels antes de entregar o tamanho ao TextView.
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            size,
            InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics
        ).roundToInt().toSp().value
    }
    private fun state(id: Int, name: String, type: String = "NORMAL") = BettingGameUiState(
        game =
            LotteryGameParameters(
                LotteryModality(
                    id,
                    name,
                    name
                ),
                3073, "22/10/2026", 60, 6, 20, 100, emptyList(), emptyList(), contestType = type
            )
    )
    private fun save(name: String, image: Bitmap) {
        val folder = File(InstrumentationRegistry.getInstrumentation().targetContext.filesDir, "header-matrix").apply { mkdirs() }
        File(folder, "$name.png").outputStream().use { image.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
