package br.gov.caixa.loterias.core.network

import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class AccountSessionContentDtoTest {
    @Test fun `notification only mock permits popup without payment priority`() = kotlinx.coroutines.runBlocking {
        val client = NetworkClientFactory.createAccountVerification("https://mock.caixa.gov.br/rest/v1/",
            mockOptions = MockBackendOptions(true, MockBackendScenario.POST_LOGIN_NOTIFICATION))
        val payload = client.verify(AccountVerificationRequestDto(), AccountVerificationSession({ "fixture" }, { true })).payload!!
        assertFalse(payload.unidentifiedPayment!!)
        assertEquals(91L, payload.latestNotification!!.id)
        assertNotNull(payload.latestNotification!!.link)
    }

    @Test fun `post login mock contains simultaneous payment notification and cart states`() = kotlinx.coroutines.runBlocking {
        val client = NetworkClientFactory.createAccountVerification("https://mock.caixa.gov.br/rest/v1/",
            mockOptions = MockBackendOptions(true, MockBackendScenario.POST_LOGIN_PAYMENT_NOT_IDENTIFIED))
        val payload = client.verify(AccountVerificationRequestDto(), AccountVerificationSession({ "fixture" }, { true })).payload!!
        assertTrue(payload.unidentifiedPayment!!)
        assertEquals(91L, payload.latestNotification!!.id)
        assertNotNull(payload.cart)
        assertEquals(MockBackendScenario.POST_LOGIN_PAYMENT_NOT_IDENTIFIED, MockBackendScenario.from("post_login_payment_not_identified"))
    }

    @Test fun `session preserves legacy cart last notification and payment metadata without importing backend token`() {
        val gson = Gson()
        val dto = gson.fromJson("""{"nome":"Nome","email":"private","primeiroNome":"N",
            "carrinho":{"id":7,"apostasIndividuais":[],"cotas":[],"combos":[]},
            "ultimaNotificacao":{"id":11,"titulo":"Título","conteudo":"Corpo","imagem":"aW1n","link":"https://example.invalid"},
            "pagamentoNaoIdentificado":true,"token":"do-not-import","tempoExpirarSegundos":200}""", AccountSessionDto::class.java)
        assertEquals(7L, dto.cart!!.id)
        assertTrue(dto.unidentifiedPayment!!)
        val notification = dto.latestNotification!!
        assertEquals(AccountNotificationDto(11, "Título", "Corpo", "aW1n", "https://example.invalid"), notification)
        assertEquals(notification, notification.copy()); assertEquals(notification.hashCode(), notification.copy().hashCode())
        val (id, title, body, image, link) = notification
        assertEquals(listOf(11L,"Título","Corpo","aW1n","https://example.invalid"), listOf(id,title,body,image,link))
        assertEquals("AccountNotificationDto(redacted)", notification.toString())
        assertEquals("AccountSessionDto(redacted)", dto.toString())
        assertEquals(dto, dto.copy()); assertEquals(dto.hashCode(), dto.copy().hashCode())
        val (name,email,first,limit,suspended,assessment,status,cart,latest,payment) = dto
        assertEquals(listOf("Nome","private","N",null,null,null,null,dto.cart,notification,true),
            listOf(name,email,first,limit,suspended,assessment,status,cart,latest,payment))
        assertFalse(gson.toJsonTree(dto).asJsonObject.has("token"))
        assertFalse(gson.toJsonTree(dto).asJsonObject.has("tempoExpirarSegundos"))
    }

    @Test fun `absent and explicit null metadata remain absent rather than fabricated notification or payment`() {
        for (json in listOf("{}", "{\"carrinho\":null,\"ultimaNotificacao\":null,\"pagamentoNaoIdentificado\":null}")) {
            val dto = Gson().fromJson(json, AccountSessionDto::class.java)
            assertNull(dto.cart); assertNull(dto.latestNotification); assertNull(dto.unidentifiedPayment)
        }
        assertNull(AccountNotificationDto().id)
    }
}
