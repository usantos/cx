package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.presentation.components.*
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceEmptyState
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceDetailScreen
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** As expectativas usam o APK ativo de validação da CX e sua fonte em /Projects/cx. */
class MarketplaceVisualNativeContractUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun emptyCardUsesNativeHorizontalInsetAndTopSpacing() {
        compose.setContent { CaixaDSTheme {
            Box(Modifier.fillMaxWidth().testTag("empty-root")) { MarketplaceEmptyState("Nenhum bolão") }
        } }
        val root = compose.onNodeWithTag("empty-root").fetchSemanticsNode().boundsInRoot
        val ops = compose.onNodeWithText("Ops!").fetchSemanticsNode().boundsInRoot
        // fragment_filtro_vazio_novo: topo do cartão em 20 + marginTop do título de 44.
        assertEquals(px(64), ops.top - root.top, 1f)
        val message = compose.onNodeWithText("Nenhum bolão").fetchSemanticsNode().boundsInRoot
        assertEquals(px(14), message.top - ops.bottom, 1f)
    }

    @Test fun favoriteEmptyUsesNativeMessage() {
        compose.setContent { CaixaDSTheme { FavoriteRetailersContent(MarketplaceUiState(), {}, {}) } }
        compose.onNodeWithText("Você ainda não possui nenhuma lotérica favorita.").assertIsDisplayed()
    }

    @Test fun modalityInkAndDarkContentKeepNativeColorsWithoutCampaign() {
        val actual = mutableMapOf<String, Pair<Color, Color>>()
        val inks = mapOf("MEGA_SENA" to Color(0xFF00A651), "QUINA" to Color(0xFF2E3192),
            "LOTOFACIL" to Color(0xFF91278F), "LOTOMANIA" to Color(0xFFE67200),
            "DUPLA_SENA" to Color(0xFFA62B43), "MAIS_MILIONARIA" to Color(0xFF2A3580),
            "LOTECA" to Color(0xFFED1C24), "TIMEMANIA" to Color(0xFF038141),
            "DIA_DE_SORTE" to Color(0xFF7E6906), "SUPER_SETE" to Color(0xFF038141))
        compose.setContent { CaixaDSTheme {
            inks.keys.forEach {
                val colors = marketplaceColors(it)
                actual[it] = colors.listText to colors.darkContent
            }
        } }
        compose.runOnIdle {
            inks.forEach { (modality, ink) ->
                assertEquals(modality, ink, actual.getValue(modality).first)
                assertEquals("$modality dark content", if (modality == "TIMEMANIA") Color(0xFF038141) else Color.White,
                    actual.getValue(modality).second)
            }
        }
    }

    @Test fun detailPurchaseMatchesActiveNativeFullWidth48dpButton() {
        compose.setContent { CaixaDSTheme {
            MarketplaceDetailScreen(MarketplaceDetailUiState(detail = detail(), isLoading = false), {}, {}, {}, {}, {})
        } }
        val panel = compose.onNodeWithTag("pool-detail-purchase-panel").fetchSemanticsNode().boundsInRoot
        val action = compose.onNodeWithTag("pool-detail-add-to-cart").fetchSemanticsNode().boundsInRoot
        assertEquals(panel.width - px(48), action.width, 1f)
        assertEquals(px(48), action.height, 1f)
        assertEquals(panel.center.x, action.center.x, 1f)
    }

    @Test fun specialDetailHeaderShowsSpecialContestName() {
        compose.setContent { CaixaDSTheme {
            DetailHeader(detail("QUINA").copy(contestType = 2), marketplaceColors("QUINA"), {})
        } }
        compose.onNodeWithText("quina de são joão").assertIsDisplayed()
    }

    @Test fun detailFooterMatchesActiveNativeThreeColumnLabels() {
        compose.setContent { CaixaDSTheme {
            MarketplaceDetailScreen(MarketplaceDetailUiState(detail = detail(), isLoading = false), {}, {}, {}, {}, {})
        } }
        compose.onNodeWithText("Valor dessa aposta").assertIsDisplayed()
        compose.onNodeWithText("Total do carrinho").assertIsDisplayed()
    }

    private fun px(value: Int) = with(compose.density) { value.dp.toPx() }
    private fun detail(modality: String = "MEGA_SENA") = MarketplacePoolDetail("B1", modality, 3070, 10,
        "Lotérica", "01.234567-8", "Cidade - SP", "21/10/2026", "1000000", 20, 50, 1, 6,
        "10", "0.50", "10.50", emptyList())
}
