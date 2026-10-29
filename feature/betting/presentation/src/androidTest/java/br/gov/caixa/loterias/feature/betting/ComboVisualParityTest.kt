package br.gov.caixa.loterias.feature.betting

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.BetComboEntry
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.components.ComboDetailEntryCard
import br.gov.caixa.loterias.feature.betting.presentation.components.LegacyComboCatalogCard
import br.gov.caixa.loterias.feature.betting.presentation.screen.BetComboDetailScreen
import br.gov.caixa.loterias.feature.betting.presentation.screen.BetCombosScreen
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogUiState
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

private const val QUINA_ID = 3
private const val LOTOFACIL_ID = 8
private const val LOTOMANIA_ID = 16
private const val RECURRING_DRAWS = 3
private const val PIXEL_TOLERANCE = 2f
private const val CARD_HEIGHT = 70
private const val CARD_GAP = 6
private const val ROW_GAP = 3
private const val PNG_QUALITY = 100

class ComboVisualParityTest {
    @get:Rule val compose = createComposeRule()
    private val entry = BetComboEntry(LotteryModality(2, "Mega-Sena", "MEGA_SENA"), "NORMAL", 3, 0, 6, 0, 2800)
    private val combo = BetCombo(8, "Combo da Sorte", "Descrição recebida do serviço", true, BigDecimal("20.50"), listOf(entry))
    private fun oracle() = ComboNativeOracle(InstrumentationRegistry.getInstrumentation().targetContext)

    @Test fun catalogAndDetailUseTheInstalledLegacyTitle() {
        val detail = mutableStateOf(false)
        val legacy = oracle()
        compose.runOnIdle {
            val metrics = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics
            Log.i("ComboNativeMetrics", legacy.probe(metrics.widthPixels, metrics.heightPixels))
        }
        compose.setContent {
            CaixaDSTheme {
                if (detail.value) {
                    Detail(combo)
                } else {
                    BetCombosScreen(BettingCatalogUiState(loading = false, combos = listOf(combo)), {
                    }, {}, BigDecimal.ZERO, 0, null, {})
                }
            }
        }
        compose.onNodeWithText(legacy.string("titulo_combos")).assertIsDisplayed()
        compose.runOnIdle { detail.value = true }
        compose.onNodeWithText(legacy.string("titulo_combos")).assertIsDisplayed()
    }

    @Test fun detailUsesServerDescriptionInsteadOfUnrelatedGenericCopy() {
        compose.setContent { CaixaDSTheme { Detail(combo) } }
        compose.onNodeWithText(combo.description).assertIsDisplayed()
    }

    @Test fun quantityAndContestUseNativeModalityColorAndNameWeight() {
        compose.setContent { CaixaDSTheme { ComboDetailEntryCard(entry) } }
        val expected = oracle().color("mega_verde_claro_mkp")
        for (label in listOf("3 jogos", "mega-sena", "concurso 2800")) {
            assertEquals(label, expected, layout(label).layoutInput.style.color.toArgb())
        }
        assertEquals(FontWeight.SemiBold, layout("mega-sena").layoutInput.style.fontWeight)
    }

    @Test fun singleGameAndRecurringContestRangeMatchLegacyLabels() {
        compose.setContent { CaixaDSTheme { ComboDetailEntryCard(entry.copy(betQuantity = 1, recurringDraws = RECURRING_DRAWS)) } }
        compose.onNodeWithText("1 jogo").assertIsDisplayed()
        compose.onNodeWithText("concurso 2800 a 2802").assertIsDisplayed()
    }

    @Test fun specialContestUsesItsSpecialModalityName() {
        val special = entry.copy(
            modality = LotteryModality(LOTOFACIL_ID, "Lotofácil", "LOTOFACIL", "Lotofácil da Independência"),
            contestType = "ESPECIAL"
        )
        compose.setContent { CaixaDSTheme { ComboDetailEntryCard(special) } }
        compose.onNodeWithText("lotofácil da independência").assertIsDisplayed()
    }

    @Test fun lotomaniaPreservesMirrorSuffixFromNativeHolder() {
        val mirror = entry.copy(modality = LotteryModality(LOTOMANIA_ID, "Lotomania", "LOTOMANIA"))
        compose.setContent { CaixaDSTheme { ComboDetailEntryCard(mirror) } }
        compose.onNodeWithText("Lotomania + espelho").assertIsDisplayed()
    }

    @Test fun detailCardHeightAndSpacingMatchInstalledXml() {
        val second = entry.copy(contestNumber = entry.contestNumber + 1)
        val third = entry.copy(contestNumber = entry.contestNumber + 2)
        compose.setContent { CaixaDSTheme { Detail(combo.copy(entries = listOf(entry, second, third))) } }
        val first = compose.onNodeWithTag("combo-entry-MEGA_SENA-NORMAL-2800").fetchSemanticsNode().boundsInRoot
        val next = compose.onNodeWithTag("combo-entry-MEGA_SENA-NORMAL-2801").fetchSemanticsNode().boundsInRoot
        val last = compose.onNodeWithTag("combo-entry-MEGA_SENA-NORMAL-2802").fetchSemanticsNode().boundsInRoot
        val description = compose.onNodeWithText(combo.description).fetchSemanticsNode().boundsInRoot
        val scale = compose.density.density
        assertEquals(CARD_HEIGHT * scale, first.height, PIXEL_TOLERANCE)
        assertEquals(CARD_GAP * scale, first.top - description.bottom, PIXEL_TOLERANCE)
        assertEquals(first.top, next.top, PIXEL_TOLERANCE)
        assertEquals(first.width, next.width, PIXEL_TOLERANCE)
        assertEquals(ROW_GAP * scale, last.top - first.bottom, PIXEL_TOLERANCE)
        org.junit.Assert.assertTrue("Odd final game spans both columns", last.width > first.width + next.width - PIXEL_TOLERANCE)
    }

    @Test fun footerAndPreviewOptionKeepNativeFontAndColor() {
        val nativeMode = mutableStateOf(false)
        val compared = combo.copy(
            entries = listOf(
                entry,
                entry.copy(
                    modality = LotteryModality(LOTOFACIL_ID, "Lotofácil", "LOTOFACIL"),
                    betQuantity = 1,
                    recurringDraws = RECURRING_DRAWS
                ),
                entry.copy(
                    modality = LotteryModality(QUINA_ID, "Quina", "QUINA", "Quina de São João"),
                    contestType = "ESPECIAL",
                    betQuantity = 1
                )
            )
        )
        compose.setContent {
            CaixaDSTheme {
                if (nativeMode.value) ComboReferencePreview(compared, emptyList(), true) else Detail(compared)
            }
        }
        assertEquals(oracle().color("bolao_black_loterias"), layout("Quantidade de Apostas").layoutInput.style.color.toArgb())
        assertEquals(FontWeight.ExtraBold, layout("R$ 20,50").layoutInput.style.fontWeight)
        assertEquals(CaixaDSFuturaFontFamily, layout("Quero ver os números escolhidos antes de pagar.").layoutInput.style.fontFamily)
        val clover = compose.onNodeWithTag("combo-header-clover").fetchSemanticsNode().boundsInRoot
        val native = oracle().inflate("activity_detalhe_combo")
        val icon = oracle().text(native, "txtNomeDetalheCombo").compoundDrawablesRelative[0]
        assertEquals(icon.intrinsicWidth.toFloat(), clover.width, PIXEL_TOLERANCE)
        val add = compose.onNodeWithTag("combo-add-to-cart").fetchSemanticsNode().boundsInRoot
        assertEquals(with(compose.density) { 201.dp.toPx() }, add.width, PIXEL_TOLERANCE)
        assertEquals(with(compose.density) { 59.dp.toPx() }, add.height, PIXEL_TOLERANCE)
        assertEquals(oracle().color("lotofacil_claro_mkp"), layout("lotofácil").layoutInput.style.color.toArgb())
        capture("combo-detail-after")
        compose.runOnIdle { nativeMode.value = true }
        compose.waitForIdle()
        capture("combo-detail-legacy")
    }

    @Test fun catalogPriceUsesNativeInheritedFuturaAndCapturesCorrectedCatalog() {
        val nativeMode = mutableStateOf(false)
        val catalog = listOf(combo, combo.copy(id = 9, name = "Combo Duplo"))
        compose.setContent {
            CaixaDSTheme {
                if (nativeMode.value) {
                    ComboReferencePreview(combo, catalog, false)
                } else {
                    BetCombosScreen(BettingCatalogUiState(loading = false, combos = catalog), {}, {}, BigDecimal.ZERO, 0, null, {})
                }
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onAllNodesWithText("R$ 20,50").onFirst().performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(CaixaDSFuturaFontFamily, layouts.single().layoutInput.style.fontFamily)
        val header = layout(oracle().string("txt_lista_combos_top"))
        assertEquals(header.getLineLeft(0), header.getLineLeft(1), PIXEL_TOLERANCE)
        capture("combo-catalog-after")
        compose.runOnIdle { nativeMode.value = true }
        compose.waitForIdle()
        capture("combo-catalog-legacy")
    }

    @Test fun blankServerDescriptionRetainsNativeDefaultText() {
        compose.setContent { CaixaDSTheme { Detail(combo.copy(description = "")) } }
        compose.onNodeWithText(oracle().string("texto_header_detalhe_combo")).assertIsDisplayed()
    }

    @Test fun specialComboKeepsNativeCatalogAndDetailNames() {
        val special = combo.copy(
            id = 1,
            entries = listOf(
                entry.copy(modality = LotteryModality(QUINA_ID, "Quina", "QUINA", "Quina de São João"), contestType = "ESPECIAL")
            )
        )
        val detail = mutableStateOf(false)
        compose.setContent {
            CaixaDSTheme {
                if (detail.value) Detail(special) else LegacyComboCatalogCard(special) {}
            }
        }
        compose.onNodeWithText("Quina de São João").assertIsDisplayed()
        compose.runOnIdle { detail.value = true }
        compose.onNodeWithText("Combo da Sorte Quina de São João").assertIsDisplayed()
    }

    @Test fun nativeGradientHeaderShowsHelpAndInvokesItOnce() {
        var helps = 0
        compose.setContent {
            CaixaDSTheme {
                BetCombosScreen(BettingCatalogUiState(loading = false, combos = listOf(combo)), {
                }, {}, BigDecimal.ZERO, 0, null, {}, onHelp = { helps++ })
            }
        }
        val root = compose.onRoot().captureToImage().toPixelMap()
        val title = compose.onNodeWithText(oracle().string("titulo_combos")).fetchSemanticsNode().boundsInRoot
        val y = title.center.y.toInt()
        assertEquals(oracle().color("verdeazul"), root[0, y].toArgb())
        compose.onNodeWithContentDescription("Ajuda").performClick()
        compose.runOnIdle { assertEquals(1, helps) }
    }

    private fun capture(name: String) {
        val bytes = ByteArrayOutputStream()
        compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, bytes)
        val shell = InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommandRw("dd of=/data/local/tmp/$name.png")
        android.os.ParcelFileDescriptor.AutoCloseOutputStream(shell[1]).use { it.write(bytes.toByteArray()) }
        android.os.ParcelFileDescriptor.AutoCloseInputStream(shell[0]).use { it.readBytes() }
    }

    @androidx.compose.runtime.Composable
    private fun Detail(value: BetCombo) = BetComboDetailScreen(BettingCatalogUiState(loading = false, selectedCombo = value), {
    }, BigDecimal.ZERO, 0, null, {}, {}, {}, {}, {})

    private fun layout(text: String): TextLayoutResult {
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(text).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        return layouts.single()
    }
}
