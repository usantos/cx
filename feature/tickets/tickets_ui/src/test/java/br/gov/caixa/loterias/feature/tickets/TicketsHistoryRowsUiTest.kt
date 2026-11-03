// Literais numéricos descrevem seleções de loteria, identificadores e dados de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.tickets

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetDataCard
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyConfirmedBetRow
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

/** Transições de estado são renderizadas com semântica real do Compose e ações do usuário. */
@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [36])
class TicketsHistoryRowsUiTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun legacyHistoryStatusCodesRetainTheirCustomerLabels() {
        val current = mutableStateOf(bet())
        show { LegacyConfirmedBetRow(current.value, {}, null) }
        val labels = mapOf(
            100L to "Premiada",
            11L to "Prêmio pago",
            9L to "Aposta não premiada",
            101L to "Concurso não apurado",
            102L to "Aposta não premiada concorrendo em concursos futuros",
            10L to "Aposta prescrita",
            4L to "Aposta efetivada",
            999L to "Efetivada"
        )
        labels.forEach { (code, label) ->
            compose.runOnIdle { current.value = bet().copy(statusId = code) }
            compose.onNodeWithText("situação:\u00a0${label.lowercase(java.util.Locale.forLanguageTag("pt-BR"))}").assertExists()
        }
    }

    @Test fun legacyHistoryAllowsRepeatOnlyForNonPoolNonLotecaBets() {
        var opens = 0
        var adds = 0
        val current = mutableStateOf(bet())
        show { LegacyConfirmedBetRow(current.value, { opens++ }, { adds++ }) }
        compose.onNodeWithContentDescription("Detalhe da aposta").performClick()
        compose.onNodeWithContentDescription("Adicionar ao carrinho").performClick()
        compose.runOnIdle {
            assertEquals(1, opens)
            assertEquals(1, adds)
            current.value = bet().copy(poolShare = true)
        }
        compose.onNodeWithContentDescription("Adicionar ao carrinho").assertDoesNotExist()
        compose.runOnIdle { current.value = bet().copy(modalityId = 19) }
        compose.onNodeWithContentDescription("Adicionar ao carrinho").assertDoesNotExist()
    }

    @Test fun betCardUsesFallbackLabelsAndReportsOpen() {
        var opens = 0
        show { BetDataCard(bet().copy(modality = "", status = "", prize = BigDecimal.TEN)) { opens++ } }
        compose.onNodeWithText("Aposta • Concurso 123").performClick()
        compose.onNodeWithText("Em acompanhamento").assertExists()
        compose.onNodeWithText("01  02  03  04  05  06").assertExists()
        compose.runOnIdle { assertEquals(1, opens) }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setFeatureContent { LoteriasCaixaDSTheme { Column { content() } } }
    }

    private fun bet() = ConfirmedBet(1, "Mega-Sena", 123, "22/10/2026", "Efetivada", listOf(1, 2, 3, 4, 5, 6), modalityId = 2)
}
