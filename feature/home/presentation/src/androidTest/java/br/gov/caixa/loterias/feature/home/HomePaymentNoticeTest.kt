package br.gov.caixa.loterias.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.home.presentation.components.HomePaymentNotice

class HomePaymentNoticeTest {
    @get:Rule val compose = createComposeRule()

    @Test fun paymentNoticeUsesLegacyTextAndPurchaseAction() {
        var opens = 0
        compose.setContent { CaixaDSTheme(darkTheme = false) { HomePaymentNotice { opens++ } } }
        compose.onNodeWithText("Atenção").assertIsDisplayed()
        compose.onNodeWithText("Você possui compra(s) cancelada(s) por falta de identificação do pagamento dentro do tempo limite para efetivação das apostas. As apostas contidas na compra não concorrem a qualquer concurso. Não se preocupe, caso tenha realizado o pagamento realizaremos o estorno do valor para a conta pagadora.").assertIsDisplayed()
        compose.onNodeWithText("Acompanhe suas compras").assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(1, opens) }
    }
}
