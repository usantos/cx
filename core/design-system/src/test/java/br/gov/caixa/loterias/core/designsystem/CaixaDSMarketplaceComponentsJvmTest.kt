package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceBack
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFavoriteRetailer
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceListFooter
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplacePoolExpansion
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplacePoolSummary
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceTab
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceToolbarIcon
import br.gov.caixa.loterias.core.designsystem.components.caixaDSMarketplaceCanvas
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSMarketplaceComponentsJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun poolSummaryTogglesExpansionAndFavoriteSeparately() {
        val expanded = mutableStateOf(false)
        val favorite = mutableStateOf(false)
        var toggles = 0
        var favorites = 0
        compose.setContent {
            CaixaDSTheme {
                CaixaDSMarketplacePoolSummary(
                    "Lotérica Central", "Brasília", "10 apostas", "R$ 10,00", false, "", "", Color.Green,
                    expanded.value, favorite.value, true, {
                        favorites++
                        favorite.value = !favorite.value
                    }, {
                        toggles++
                        expanded.value =
                            !expanded.value
                    }
                )
            }
        }
        compose.onNodeWithText("Brasília").assertIsDisplayed()
        compose.onNodeWithText("10 apostas").assertIsDisplayed()
        compose.onNodeWithContentDescription("Favoritar lotérica", useUnmergedTree = true).performClick()
        compose.onNodeWithContentDescription("Excluir lotérica favorita", useUnmergedTree = true).assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, favorites)
            assertEquals(0, toggles)
        }
        compose.onNodeWithContentDescription("Ver mais").performClick()
        compose.onNodeWithText("10 apostas").assertDoesNotExist()
        compose.onNodeWithContentDescription("Recolher bolão").performClick()
        compose.onNodeWithText("10 apostas").assertIsDisplayed()
        compose.runOnIdle { assertEquals(2, toggles) }
    }

    @Test
    fun lotteryFilteredSummaryShowsPrizeInsteadOfRetailerLocationAndFavorite() {
        compose.setContent {
            CaixaDSTheme {
                CaixaDSMarketplacePoolSummary(
                    "Mega-Sena", "Cidade oculta", "6 dezenas", "R$ 8,00", true, "R$ 10 milhões", "Prêmio estimado", Color.Green,
                    false, false, false, {}, {}
                )
            }
        }
        compose.onNodeWithText("Mega-Sena").assertIsDisplayed()
        compose.onNodeWithText("R$ 10 milhões").assertIsDisplayed()
        compose.onNodeWithText("Prêmio estimado").assertIsDisplayed()
        compose.onNodeWithText("Cidade oculta").assertDoesNotExist()
        compose.onNodeWithContentDescription("Favoritar lotérica").assertDoesNotExist()
    }

    @Test
    fun expansionDispatchesActionsAndDisablesQuantityAndAddWhileAdding() {
        val adding = mutableStateOf(false)
        val canDecrease = mutableStateOf(false)
        val calls = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSMarketplacePoolExpansion(
                    "Concurso 2000", AnnotatedString("6 números"), AnnotatedString("10 cotas disponíveis"),
                    2, canDecrease.value, true, adding.value, true, Color.Blue, Color.White, Color.Blue,
                    { calls.add("detail") }, { calls.add("decrease") }, { calls.add("increase") }, { calls.add("add") }
                )
            }
        }
        compose.onNodeWithText("Concurso 2000").assertIsDisplayed()
        compose.onNodeWithText("6 números").assertIsDisplayed()
        compose.onNodeWithText("10 cotas disponíveis").assertIsDisplayed()
        compose.onNodeWithText("-").assertIsNotEnabled()
        compose.onNodeWithText("Detalhes").performClick()
        compose.onNodeWithText("+").performClick()
        compose.runOnIdle { canDecrease.value = true }
        compose.onNodeWithText("-").performClick()
        compose.onNodeWithText("Adicionar ao Carrinho").performClick()
        compose.runOnIdle {
            assertEquals(listOf("detail", "increase", "decrease", "add"), calls)
            adding.value = true
        }
        compose.onNodeWithText("Adicionando…").assertIsNotEnabled()
        compose.onNodeWithText("+").assertIsNotEnabled()
        compose.onNodeWithText("-").assertIsNotEnabled()
    }

    @Test
    fun favoriteRetailerKeepsOpenAndRemoveCallbacksDistinct() {
        val calls = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme { CaixaDSMarketplaceFavoriteRetailer("Central", "Recife", { calls.add("open") }, { calls.add("remove") }) }
        }
        compose.onNodeWithText("Central").assertIsDisplayed()
        compose.onNodeWithText("Recife").assertIsDisplayed()
        compose.onNodeWithText("Ver Bolões").performClick()
        compose.onNodeWithContentDescription("Excluir Central").performClick()
        compose.runOnIdle { assertEquals(listOf("open", "remove"), calls) }
    }

    @Test
    fun tabsToolbarBackAndFooterExposeTheirNavigationAndLoadStates() {
        val loading = mutableStateOf(true)
        val exhausted = mutableStateOf(false)
        val selected = mutableStateOf(false)
        var tabClicks = 0
        var backs = 0
        compose.setContent {
            CaixaDSTheme {
                Column(Modifier.caixaDSMarketplaceCanvas(selected.value)) {
                    Row(Modifier.height(48.dp)) {
                        CaixaDSMarketplaceTab("Favoritas", selected.value, {
                            tabClicks++
                            selected.value = !selected.value
                        })
                        CaixaDSMarketplaceToolbarIcon(true, "Ajuda")
                        CaixaDSMarketplaceToolbarIcon(false, "Buscar", search = true)
                        CaixaDSMarketplaceToolbarIcon(false, "Filtros")
                    }
                    Box(Modifier.size(72.dp, 48.dp)) { CaixaDSMarketplaceBack { backs++ } }
                    CaixaDSMarketplaceListFooter(loading.value, exhausted.value)
                }
            }
        }
        listOf("Ajuda", "Buscar", "Filtros").forEach { compose.onNodeWithContentDescription(it).assertIsDisplayed() }
        compose.onNodeWithText("Favoritas").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        val progress = SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)
        compose.onNode(progress).assertIsDisplayed()
        compose.onNodeWithText("Fim da lista").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, tabClicks)
            assertEquals(1, backs)
            loading.value = false
            exhausted.value = true
        }
        compose.onNode(progress).assertDoesNotExist()
        compose.onNodeWithText("Fim da lista").assertIsDisplayed()
    }
}
