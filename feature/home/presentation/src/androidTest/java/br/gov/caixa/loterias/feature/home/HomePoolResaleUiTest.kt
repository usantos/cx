package br.gov.caixa.loterias.feature.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.home.presentation.components.HomePoolResaleTimer
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomePoolResaleCountdown
import org.junit.Rule
import org.junit.Test

class HomePoolResaleUiTest {
    @get:Rule val compose = createComposeRule()
    @Test fun extraWindowUsesLegacyCopyAndEightyDpContainer() {
        compose.setContent { CaixaDSTheme(darkTheme = false) { HomePoolResaleTimer(HomePoolResaleCountdown("29:59", 0xFF004680.toInt())) } }
        compose.onNodeWithText("Você tem").assertIsDisplayed()
        compose.onNodeWithText("29:59").assertIsDisplayed()
        compose.onNodeWithText("para comprar o seu bolão").assertIsDisplayed()
        compose.onNodeWithTag("home-pool-resale-timer").assertHeightIsEqualTo(80.dp)
    }
}
