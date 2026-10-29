package br.gov.caixa.loterias.feature.payment

import br.gov.caixa.loterias.feature.payment.presentation.mapper.PAYMENT_CARD_PROCESSOR_GUIDANCE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PIX_EXPIRED_GUIDANCE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PIX_PENDING_EXIT_MESSAGE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PIX_RECOVERY_GUIDANCE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PURCHASE_PROCESSING_TITLE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PURCHASE_REVIEW_GUIDANCE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PURCHASE_UPDATED_TERMS_GUIDANCE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.acceptedBrands
import br.gov.caixa.loterias.feature.payment.presentation.mapper.formatRemaining
import br.gov.caixa.loterias.feature.payment.presentation.mapper.pixCopyDisplayValue
import br.gov.caixa.loterias.feature.payment.presentation.mapper.prePurchaseMessage
import br.gov.caixa.loterias.feature.payment.presentation.mapper.purchaseProcessingDestination
import br.gov.caixa.loterias.feature.payment.presentation.mapper.purchaseProcessingMessages
import br.gov.caixa.loterias.feature.payment.presentation.mapper.savedCardListKey
import br.gov.caixa.loterias.feature.payment.presentation.screen.paymentScreens
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentRulesTest {
    @Test fun `updated terms guidance matches compiled legacy resource termo_novo`() {
        assertEquals("O termo de Uso foi Atualizado. É necessário aceitar novamente.", PURCHASE_UPDATED_TERMS_GUIDANCE)
    }

    @Test fun `purchase review uses the literal string compiled into the legacy APK`() {
        assertEquals(
            "Verificamos que sua compra está em processamento e está demorando mais que o esperado. " +
                "Aguarde alguns instantes e verifique na área Minhas compras se a compra foi processada antes de tentar novamente.",
            PURCHASE_REVIEW_GUIDANCE
        )
    }

    @Test
    fun `card form only enables continue after all required legacy fields are present`() {
        assertFalse(PaymentCardUiState().formValid)
        assertFalse(
            PaymentCardUiState(
                number = "1234",
                holderName = "Titular",
                month = "12",
                minimumExpirationYear = 2026
            ).formValid
        )
        assertTrue(
            PaymentCardUiState(
                number = "1234567812345678",
                holderName = "TITULAR",
                month = "12",
                year = "2029",
                minimumExpirationYear = 2026
            ).formValid
        )
    }

    @Test
    fun `payment catalog contains every distinct card and pix step`() {
        assertTrue(
            setOf(
                "payment/method",
                "payment/cards",
                "payment/add-card",
                "payment/card-preview",
                "payment/confirm-card",
                "payment/cvv",
                "payment/pix-guidance",
                "payment/pix-copy",
                "payment/confirmation"
            ).all { route -> paymentScreens { it.toString() }.any { it.route == route } }
        )
    }

    @Test
    fun `saved card list key is scoped by payment processor`() {
        assertEquals("10:card-7", savedCardListKey(10, "card-7"))
        assertNotEquals(savedCardListKey(10, "card-7"), savedCardListKey(20, "card-7"))
    }

    @Test
    fun `purchase processing keeps exact legacy copy and always opens purchases`() {
        assertEquals("Estamos processando sua compra.", PURCHASE_PROCESSING_TITLE)
        assertEquals(
            listOf(
                "Para participar dos sorteios, a aposta precisa estar EFETIVADA.",
                "O pagamento realizado não garante automaticamente a efetivação da aposta.",
                "Se a aposta não for registrada, o valor será devolvido.",
                "Você pode acompanhar tudo em Minhas Compras."
            ),
            purchaseProcessingMessages
        )
        assertEquals("tickets/purchases", purchaseProcessingDestination())
    }

    @Test
    fun `pix pending exit warning keeps every legacy consequence`() {
        assertTrue(PIX_PENDING_EXIT_MESSAGE.contains("Pagamento ainda não identificado."))
        assertTrue(PIX_PENDING_EXIT_MESSAGE.contains("fora do tempo limite"))
        assertTrue(PIX_PENDING_EXIT_MESSAGE.contains("Minhas Compras"))
        assertTrue(PIX_PENDING_EXIT_MESSAGE.endsWith("Você deseja aguardar a confirmação de pagamento?"))
    }

    @Test
    fun `pre purchase question keeps the exact legacy sentence`() {
        assertEquals(
            "O Valor total da sua compra é de R$ 25,50. Confirma?",
            prePurchaseMessage("R$ 25,50")
        )
    }

    @Test
    fun `processor brands keep the exact legacy order and availability`() {
        assertEquals(
            "Escolha um dos processadores abaixo e cadastre um cartão de crédito de sua titularidade ou escolha um cartão salvo para continuar.",
            PAYMENT_CARD_PROCESSOR_GUIDANCE
        )
        assertEquals(listOf("ELO", "MASTERCARD", "VISA"), acceptedBrands(5))
        assertEquals(
            listOf("AMERICANEXPRESS", "ELO", "HIPERCARD", "MASTERCARD", "VISA"),
            acceptedBrands(1)
        )
    }

    @Test
    fun `pending exit and expired guidance use installed APK literals`() {
        // aapt2 resources.txt 0x7f1304d9 e 0x7f1305b1: as aspas do XML de origem
        // foram removidas pela compilação de recursos Android e estão ausentes em produção.
        assertEquals(
            "Pagamento ainda não identificado.\n" +
                "Caso tenha realizado o pagamento, aguarde enquanto realizamos o processamento.\n" +
                "Pagamentos identificados fora do tempo limite para efetivação de apostas serão estornados.\n" +
                "Acompanhe o processamento do seu pedido na opção Minhas Compras.\n\n" +
                "Você deseja aguardar a confirmação de pagamento?",
            PIX_PENDING_EXIT_MESSAGE
        )
        assertEquals(
            "A validade do seu código de pagamento Pix expirou.\n" +
                "Caso tenha realizado o pagamento, aguarde enquanto realizamos o processamento.\n" +
                "Pagamentos identificados fora do tempo limite para efetivação das apostas serão estornados.\n" +
                "Acompanhe o status da sua compra acessando o menu Minhas Compras.",
            PIX_EXPIRED_GUIDANCE
        )
    }

    @Test
    fun `pix copy timer and expiration messages reproduce the legacy contract`() {
        assertEquals("1234567890123456789...", pixCopyDisplayValue("123456789012345678901"))
        assertEquals("short", pixCopyDisplayValue("short"))
        assertEquals("00:00", formatRemaining(0))
        assertEquals("02:05", formatRemaining(125))
        assertTrue(PIX_RECOVERY_GUIDANCE.contains("Minhas Compras"))
        assertTrue(PIX_EXPIRED_GUIDANCE.startsWith("A validade do seu código de pagamento Pix expirou."))
        assertTrue(PIX_EXPIRED_GUIDANCE.contains("serão estornados"))
    }
}
