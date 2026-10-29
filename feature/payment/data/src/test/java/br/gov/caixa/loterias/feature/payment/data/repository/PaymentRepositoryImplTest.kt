package br.gov.caixa.loterias.feature.payment.data.repository

import br.gov.caixa.loterias.feature.payment.domain.model.CardPaymentRequest
import br.gov.caixa.loterias.feature.payment.domain.model.CardSubmissionOutcome
import br.gov.caixa.loterias.feature.payment.domain.usecase.SubmitCardPayment
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentCardDraft
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentCardSelection
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.api.MercadoPagoApi
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.mock.MockBackendScenario
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.provider.PaymentApis
import br.gov.caixa.loterias.core.network.provider.PaymentApiProvider
import br.gov.caixa.loterias.core.network.api.PurchaseBackendApi
import br.gov.caixa.loterias.core.network.api.PurchaseBackendApiProvider
import br.gov.caixa.loterias.core.network.api.PurchaseBackendClient
import br.gov.caixa.loterias.feature.payment.data.dto.PurchaseEnvelopeDto
import br.gov.caixa.loterias.core.network.api.PurchaseBackendResponse
import br.gov.caixa.loterias.core.network.api.PurchaseHttpException
import br.gov.caixa.loterias.feature.payment.data.dto.PurchaseErrorEnvelopeDto
import br.gov.caixa.loterias.feature.payment.domain.model.PurchasePolling
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNotice
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNoticeReason
import br.gov.caixa.loterias.feature.payment.data.dto.PurchaseProcessingDto
import com.google.gson.Gson
import retrofit2.Response
import br.gov.caixa.loterias.core.network.api.RecargaPayApi
import br.gov.caixa.loterias.feature.account.data.dto.UserProfileDto
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.payment.data.datasource.remote.PaymentRemoteDataSource
import br.gov.caixa.loterias.feature.payment.data.datasource.remote.PaymentRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.payment.data.repository.PaymentRepositoryImpl

class PaymentRepositoryImplTest {
    @Test fun `Pix generation sends explicit null parameters and unchanged favorite flags`() = runTest {
        val source = FakeSource()

        PaymentRepositoryImpl(source).generatePix()

        assertEquals(json("""{"meioPagamento":6,"parametrosPagamento":null,"salvarCarrinhoFavorito":false,"nomeCarrinhoFavorito":""}"""), source.pixBody)
    }

    @Test fun `card registration keeps string flags and explicit nulls for new and saved selections`() = runTest {
        val source = FakeSource()
        val repository = PaymentRepositoryImpl(source)
        repository.submitCardPayment(CardPaymentRequest(1, PaymentCardSelection.New(
            PaymentCardDraft("4111111111111111", "PESSOA", 12, 2030, false),
        ), "123"))
        assertEquals(json("""{"meioPagamento":1,"parametrosPagamento":{"TOKEN":"token","SAVED_CARD":null,"PAYMENT_METHOD_ID":"visa","SAVE_CARD":"false"}}"""), source.registerBody)

        // Cartões salvos legados mantêm seu meio de pagamento conhecido quando as parcelas estão vazias.
        source.installmentsValue = json("[]")
        repository.submitCardPayment(CardPaymentRequest(1, PaymentCardSelection.Saved(savedCard(1)), "123"))
        assertEquals(json("""{"meioPagamento":1,"parametrosPagamento":{"TOKEN":"token","SAVED_CARD":"true","PAYMENT_METHOD_ID":"visa","SAVE_CARD":null}}"""), source.registerBody)
    }

    @Test fun `purchase progress propagates cancellation without converting it to a payment decision`() = runTest {
        val cancellation = kotlinx.coroutines.CancellationException("progress")
        val source = FakeSource().apply { progressFailure = cancellation }

        org.junit.Assert.assertSame(cancellation, runCatching {
            PaymentRepositoryImpl(source).purchaseProgress(91)
        }.exceptionOrNull())
    }

    @Test fun `real Retrofit mock chain preserves every outcome for both card processors`() = runTest {
        val notices = mapOf(
            MockBackendScenario.PURCHASE_MORE_BETS_REQUIRED to PurchaseNoticeReason.MORE_BETS_REQUIRED,
            MockBackendScenario.PURCHASE_BUSINESS_RULE to PurchaseNoticeReason.BUSINESS_RULE,
            MockBackendScenario.PURCHASE_SERVER_MESSAGE to PurchaseNoticeReason.SERVER_MESSAGE,
            MockBackendScenario.PURCHASE_SERVER_FAILURE to PurchaseNoticeReason.SERVER_FAILURE,
            MockBackendScenario.PURCHASE_INVALID_IDENTIFICATION to PurchaseNoticeReason.INVALID_IDENTIFICATION,
            MockBackendScenario.PURCHASE_MP_REGISTRATION_REQUIRED to PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION,
            MockBackendScenario.PURCHASE_MP_REGISTRATION_TOKEN_FAILURE to PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION,
        )
        val redirects = mapOf(
            MockBackendScenario.PURCHASE_LOGIN_REQUIRED to PurchaseRedirect.LOGIN,
            MockBackendScenario.PURCHASE_TERMS_REQUIRED to PurchaseRedirect.ACCEPT_TERMS,
            MockBackendScenario.PURCHASE_REGISTRATION_REQUIRED to PurchaseRedirect.REGISTER_ACCOUNT,
            MockBackendScenario.PURCHASE_UNAVAILABLE to PurchaseRedirect.UNAVAILABLE,
            MockBackendScenario.PURCHASE_HTTP_UNAUTHORIZED to PurchaseRedirect.LOGIN,
            MockBackendScenario.PURCHASE_CART_REDIRECT to PurchaseRedirect.CART,
            MockBackendScenario.PURCHASE_FAVORITE_CARTS_REDIRECT to PurchaseRedirect.FAVORITE_CARTS,
        )
        for (scenario in listOf(MockBackendScenario.HAPPY_PATH, MockBackendScenario.PURCHASE_RESPONSE_LOST) + redirects.keys + notices.keys) {
            val options = MockBackendOptions(true, scenario)
            val token = AuthTokenProvider { null }
            val legacy = BackendApiProvider().apply {
                replace(NetworkClientFactory.createLegacy("https://mock.caixa.gov.br/rest/v1/", token, mockOptions = options))
            }
            val loterias = LoteriasApiProvider().apply {
                replace(NetworkClientFactory.create("https://mock.caixa.gov.br/rest/v1/", token, mockOptions = options))
            }
            val providers = PaymentApiProvider().apply {
                replace(PaymentApis(
                    NetworkClientFactory.createMercadoPago("https://mock.caixa.gov.br/", mockOptions = options),
                    NetworkClientFactory.createRecargaPay("https://mock.caixa.gov.br/", mockOptions = options),
                ))
            }
            val purchase = PurchaseBackendApiProvider().apply {
                replace(NetworkClientFactory.createPurchaseBackend("https://mock.caixa.gov.br/rest/v1/", token, mockOptions = options))
            }
            val repository = PaymentRepositoryImpl(PaymentRemoteDataSourceImpl(legacy, loterias, providers, purchase))
            val submit = SubmitCardPayment(repository)
            for (methodId in listOf(1L, 5L)) {
                for (selection in listOf(
                    PaymentCardSelection.Saved(savedCard(methodId)),
                    PaymentCardSelection.New(PaymentCardDraft("4111111111111111", "PESSOA", 12, 2030, false)),
                )) {
                    val result = submit(CardPaymentRequest(methodId, selection, "123"))
                    assertTrue("$scenario, processor $methodId: $result", result is AppResult.Success)
                    val outcome = (result as AppResult.Success).value
                    if (scenario == MockBackendScenario.HAPPY_PATH) {
                        assertEquals(99L, (outcome as CardSubmissionOutcome.Submitted).purchase.purchaseId)
                    } else if (scenario in redirects) {
                        assertEquals(CardSubmissionOutcome.RedirectRequired(redirects.getValue(scenario)), outcome)
                    } else if (scenario in notices) {
                        val notice = (outcome as CardSubmissionOutcome.NoticeRequired).notice
                        assertEquals(notices.getValue(scenario), notice.reason)
                        with(repository.purchaseProgress(91)) {
                            assertEquals(notice, this.notice)
                            assertFalse(completed); assertFalse(rejected)
                        }
                    } else assertEquals(CardSubmissionOutcome.ReviewRequired(), outcome)
                }
            }
        }
    }

    @Test fun `failures after registration starts become review rather than retryable errors`() = runTest {
        val request = CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")
        for (failure in listOf(
            java.io.IOException("reply lost"),
            java.net.SocketTimeoutException("reply timed out"),
            com.google.gson.JsonSyntaxException("malformed response"),
            IllegalArgumentException("invalid post response"),
            IllegalStateException("unknown result"),
        )) {
            val source = FakeSource().apply { registrationFailure = failure }
            val result = SubmitCardPayment(PaymentRepositoryImpl(source))(request)
            assertEquals(AppResult.Success(CardSubmissionOutcome.ReviewRequired()), result)
            assertEquals(listOf("rpCustomer", "rpAuthorize", "register"), source.calls)
            assertFalse(result.toString().contains(failure.message.orEmpty()))
        }
    }

    @Test fun `preparation failures never register and remain ordinary failures`() = runTest {
        val request = CardPaymentRequest(1, PaymentCardSelection.Saved(savedCard(1)), "123")
        for (failure in listOf(java.io.IOException("offline"), IllegalArgumentException("invalid provider"), IllegalStateException("provider"))) {
            val source = FakeSource().apply { preparationFailure = failure }
            val result = SubmitCardPayment(PaymentRepositoryImpl(source))(request)
            assertTrue(result is AppResult.Failure)
            assertEquals(listOf("paymentMethod"), source.calls)
        }
    }

    @Test fun `review preserves only a positive known purchase identifier`() = runTest {
        val request = CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")
        for (id in listOf(null, -1L, 0L, 91L)) {
            val source = FakeSource().apply {
                envelopeOverride = PurchaseEnvelopeDto(PurchaseProcessingDto(id = id), message = "not exposed")
            }
            val result = PaymentRepositoryImpl(source).submitCardPayment(request)
            assertEquals(CardSubmissionOutcome.ReviewRequired(id?.takeIf { it > 0 }), result)
            assertEquals(1, source.calls.count { it == "register" })
        }
    }

    @Test fun `cancellation propagates from preparation and submission without retry`() = runTest {
        val request = CardPaymentRequest(1, PaymentCardSelection.Saved(savedCard(1)), "123")
        for (duringSubmission in listOf(false, true)) {
            val expected = kotlinx.coroutines.CancellationException("cancelled")
            val source = FakeSource().apply {
                if (duringSubmission) registrationFailure = expected else preparationFailure = expected
            }
            var observed: kotlinx.coroutines.CancellationException? = null
            try { SubmitCardPayment(PaymentRepositoryImpl(source))(request) }
            catch (failure: kotlinx.coroutines.CancellationException) { observed = failure }
            org.junit.Assert.assertSame(expected, observed)
            assertEquals(if (duringSubmission) 1 else 0, source.calls.count { it == "register" })
        }
    }

    @Test fun `mock backend produces a complete active pix charge`() = runTest {
        val legacyProvider = BackendApiProvider().apply {
            replace(
                NetworkClientFactory.createLegacy(
                    baseUrl = "https://mock.caixa.gov.br/rest/v1/",
                    tokenProvider = AuthTokenProvider { null },
                    mockOptions = MockBackendOptions(true, MockBackendScenario.HAPPY_PATH),
                ),
            )
        }
        val source = PaymentRemoteDataSourceImpl(
            legacyProvider,
            LoteriasApiProvider(),
            PaymentApiProvider(),
            PurchaseBackendApiProvider(),
        )

        val pix = PaymentRepositoryImpl(source).generatePix()

        assertTrue(pix.copyPaste.startsWith("0002010102122685001"))
        assertEquals("MOCK-TX", pix.transactionId)
        assertEquals("25", pix.amount.stripTrailingZeros().toPlainString())
        assertTrue(pix.expiresAt.contains("-03:00"))
    }

    @Test fun `maps methods cards pix progress and dam payloads`() = runTest {
        val source = FakeSource()
        val repository = PaymentRepositoryImpl(source)
        with(repository.methods().single()) {
            assertEquals(6L, id)
            assertEquals("Pix", purchaseName)
            assertEquals("Prêmio", prizeName)
            assertTrue(default)
            assertEquals("key", publicKey)
        }
        with(repository.cards(1).single()) {
            assertEquals("42", id)
            assertEquals(1L, paymentMethodId)
            assertEquals(4, securityCodeLength)
            assertEquals("Visa", brand)
        }
        assertEquals("42", repository.deleteCard("42", 5).single().id)
        assertEquals(5L, source.deletedMethod)
        assertEquals("PIX-CODE", repository.generatePix().copyPaste)
        assertEquals(91L, repository.purchaseProgress(91).purchaseId)
        assertTrue(repository.validateDam().blocked)
        assertEquals("Represa ativa", repository.validateDam().message)
        assertTrue(source.pixBody?.asJsonObject?.get("parametrosPagamento")?.isJsonNull == true)
    }

    @Test fun `existing purchase query rejects approval payload belonging to another purchase`() = runTest {
        val source = FakeSource().apply {
            progressValue = json("{\"id\":9002,\"idSituacaoCompra\":8}")
        }
        val failure = runCatching { PaymentRepositoryImpl(source).purchaseProgress(9003) }.exceptionOrNull()
        assertTrue("A GET for 9003 must not approve payload purchase 9002", failure is IllegalArgumentException)
    }

    @Test fun `mapping safely defaults malformed optional values`() = runTest {
        val source = FakeSource().apply {
            methodsValue = json("[{\"id\":1}]")
            cardsValue = json("[{\"idCartao\":\"abc\"}]")
            progressValue = json("{\"id\":0,\"idSituacaoCompra\":3}")
            damValue = JsonNull.INSTANCE
        }
        val repository = PaymentRepositoryImpl(source)
        assertEquals("", repository.methods().single().purchaseName)
        with(repository.cards(1).single()) {
            assertEquals("abc", id)
            assertEquals(3, securityCodeLength)
            assertEquals(0, expirationMonth)
        }
        with(repository.purchaseProgress(77)) { assertEquals(77L, purchaseId); assertTrue(rejected) }
        with(repository.validateDam()) { assertFalse(blocked); assertEquals("", message) }
    }

    @Test fun `missing required pix and progress payloads fail`() = runTest {
        val source = FakeSource().apply { pixValue = null; progressValue = null; processing = true }
        val repository = PaymentRepositoryImpl(source)
        assertFails { repository.generatePix() }
        assertFails { repository.purchaseProgress(1) }
    }

    @Test fun `submits new and saved Mercado Pago cards with legacy contract`() = runTest {
        val source = FakeSource()
        val repository = PaymentRepositoryImpl(source)
        val draft = PaymentCardDraft("4111 1111 1111 1111", "PESSOA", 12, 2030, true)
        val created = repository.submitCardPayment(CardPaymentRequest(1, PaymentCardSelection.New(draft), "123"))
        assertEquals(91L, (created as CardSubmissionOutcome.Submitted).purchase.purchaseId)
        assertEquals(listOf("paymentMethod", "profile", "mpInstallments", "mpToken", "register"), source.calls)
        assertEquals("4111111", source.bin)
        assertEquals("true", source.registerBody!!.asJsonObject["parametrosPagamento"].asJsonObject["SAVE_CARD"].asString)

        source.calls.clear()
        val saved = savedCard(method = 1)
        repository.submitCardPayment(CardPaymentRequest(1, PaymentCardSelection.Saved(saved), "123"))
        assertEquals("true", source.registerBody!!.asJsonObject["parametrosPagamento"].asJsonObject["SAVED_CARD"].asString)
        assertEquals("saved", source.mpBody!!.asJsonObject["card_id"].asString)
    }

    @Test fun `submits new and saved RecargaPay cards with customer token`() = runTest {
        val source = FakeSource()
        val repository = PaymentRepositoryImpl(source)
        val draft = PaymentCardDraft("5555555555554444", "PESSOA", 11, 2031, false)
        repository.submitCardPayment(CardPaymentRequest(5, PaymentCardSelection.New(draft), "123"))
        assertTrue(source.calls.containsAll(listOf("rpCustomer", "profile", "rpCreate", "register")))
        with(source.rpBody!!.asJsonObject) {
            assertEquals("MASTERCARD", get("brand").asString)
            assertEquals("+55999999999", getAsJsonObject("cardHolder").getAsJsonArray("phones")[0].asString)
        }

        source.calls.clear()
        repository.submitCardPayment(CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(method = 5)), "123"))
        assertEquals(listOf("rpCustomer", "rpAuthorize", "register"), source.calls)
        assertEquals("saved-token", source.authorizedCard)
    }

    @Test fun `RecargaPay create card normalizes profile birthdate and preserves request fields`() = runTest {
        val birthdates = listOf(
            "Jan 1, 1990" to "1990-01-01",
            "Mon Jan 01 00:00:00 1990" to "1990-01-01",
            "1990-01-01" to "1990-01-01",
            "2000-02-29" to "2000-02-29",
            null to "",
        )
        for ((profileDate, expectedDate) in birthdates) {
            val source = FakeSource().apply {
                profileValue = Gson().fromJson(json("""{
                    "cpf":"12345678901","nome":"Pessoa","email":"mock@caixa.gov.br",
                    "dataNascimento":${Gson().toJson(profileDate)},
                    "telefone":{"ddd":"61","numero":"999999999"}
                }"""), UserProfileDto::class.java)
            }
            val draft = PaymentCardDraft("5555 5555 5555 4444", "PESSOA", 11, 2031, false)

            val result = PaymentRepositoryImpl(source).submitCardPayment(
                CardPaymentRequest(5, PaymentCardSelection.New(draft), "123"),
            )

            assertTrue(result is CardSubmissionOutcome.Submitted)
            assertEquals("profile date $profileDate", json("""{
                "type":"CREDIT","brand":"MASTERCARD","number":"5555555555554444",
                "cardHolder":{"type":"NATURAL","name":"Pessoa","email":"mock@caixa.gov.br",
                    "document":"12345678901","birthdate":"$expectedDate","phones":["61999999999"]},
                "expirationDate":{"month":11,"year":2031},"securityCode":"123","shouldSave":false
            }"""), source.rpBody)
        }
    }

    @Test fun `RecargaPay rejects malformed profile birthdate before card creation`() = runTest {
        for (profileDate in listOf("not-a-date", "1990-02-30")) {
            val source = FakeSource().apply {
                profileValue = UserProfileDto(birthDate = profileDate)
            }
            val draft = PaymentCardDraft("5555555555554444", "PESSOA", 11, 2031, false)

            val result = SubmitCardPayment(PaymentRepositoryImpl(source))(
                CardPaymentRequest(5, PaymentCardSelection.New(draft), "123"),
            )

            assertTrue("profile date $profileDate", result is AppResult.Failure)
            assertEquals(listOf("rpCustomer", "profile"), source.calls)
            org.junit.Assert.assertNull(source.rpBody)
            org.junit.Assert.assertNull(source.registerBody)
        }
    }

    @Test fun `card submission rejects absent or invalid provider responses`() = runTest {
        val source = FakeSource()
        val repository = PaymentRepositoryImpl(source)
        val draft = PaymentCardDraft("4111111111111111", "PESSOA", 12, 2030, false)
        val mp = CardPaymentRequest(1, PaymentCardSelection.New(draft), "123")
        source.paymentMethodValue = null
        assertFails { repository.submitCardPayment(mp) }
        source.paymentMethodValue = json("{\"publicKey\":\"\"}")
        assertFails { repository.submitCardPayment(mp) }
        source.paymentMethodValue = json("{\"publicKey\":\"key\"}")
        source.profileValue = null
        assertFails { repository.submitCardPayment(mp) }
        source.profileValue = UserProfileDto(cpf = "1")
        source.installmentsValue = json("[]")
        assertFails { repository.submitCardPayment(mp) }
        source.installmentsValue = json("[{\"payment_method_id\":\"visa\"}]")
        source.mpTokenValue = json("{\"id\":\"token\",\"luhn_validation\":false}")
        assertFails { repository.submitCardPayment(mp) }
        source.mpTokenValue = json("{\"id\":\"\",\"luhn_validation\":true}")
        assertFails { repository.submitCardPayment(mp) }

        source.customerToken = null
        assertFails { repository.submitCardPayment(CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")) }
        source.customerToken = "customer"
        source.rpValue = json("{\"customerCardToken\":\"\",\"brand\":\"VISA\"}")
        assertFails { repository.submitCardPayment(CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")) }
        source.rpValue = json("{\"customerCardToken\":\"token\",\"brand\":\"\"}")
        assertFails { repository.submitCardPayment(CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")) }
        source.rpValue = json("{\"customerCardToken\":\"token\",\"brand\":\"VISA\"}")
        source.purchaseValue = null
        assertTrue(repository.submitCardPayment(CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")) is CardSubmissionOutcome.ReviewRequired)
        assertFails { repository.submitCardPayment(CardPaymentRequest(6, PaymentCardSelection.New(draft), "123")) }
    }

    @Test fun `retrofit data source delegates every operation`() = runTest {
        val provider = BackendApiProvider()
        val calls = mutableListOf<String>()
        provider.replace(java.lang.reflect.Proxy.newProxyInstance(
            BackendApi::class.java.classLoader,
            arrayOf(BackendApi::class.java),
        ) { _, method, _ ->
            calls += method.name
            if (method.name == "recargaPayCustomerToken") ApiEnvelope("token") else ApiEnvelope(JsonNull.INSTANCE)
        } as BackendApi)
        val loteriasProvider = LoteriasApiProvider().apply {
            replace(proxy(LoteriasApi::class.java) { method ->
                calls += method.name
                ApiEnvelope(UserProfileDto(cpf = "1"))
            })
        }
        val mercado = proxy(MercadoPagoApi::class.java) { method -> calls += method.name; JsonNull.INSTANCE }
        val recarga = proxy(RecargaPayApi::class.java) { method -> calls += method.name; JsonNull.INSTANCE }
        val paymentProvider = PaymentApiProvider().apply { replace(PaymentApis(mercado, recarga)) }
        val purchaseProvider = PurchaseBackendApiProvider().apply {
            replace(PurchaseBackendClient(proxy(PurchaseBackendApi::class.java) { method ->
                calls += method.name
                Response.success(PurchaseEnvelopeDto())
            }, Gson()))
        }
        val source = PaymentRemoteDataSourceImpl(provider, loteriasProvider, paymentProvider, purchaseProvider)
        source.methods(); source.cards(1); source.deleteCard("x", 5)
        source.generatePix(json("{}")); source.purchaseProgress(8); source.validateDam()
        source.paymentMethod(1); source.profile(); source.recargaPayCustomerToken()
        source.mercadoPagoInstallments("411111", "key"); source.mercadoPagoCardToken("key", json("{}"))
        source.recargaPayCreateCard("token", json("{}")); source.recargaPayAuthorizeCard("card", "token", json("{}"))
        source.registerCardPurchase(json("{}"))
        assertEquals(
            listOf("paymentMethods", "cards", "deleteCard", "generatePixCharge", "purchaseProcessing", "validateDam", "paymentMethod", "currentUserProfile", "recargaPayCustomerToken", "installments", "createCardToken", "createCard", "authorizeSavedCard", "registerCartBetsAsync"),
            calls,
        )
    }

    @Test fun `processing envelopes reject business errors redirects and missing payloads`() = runTest {
        val source = FakeSource().apply { processing = true }
        val repository = PaymentRepositoryImpl(source)
        val request = CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")
        for (envelope in listOf(
            PurchaseEnvelopeDto(),
            PurchaseEnvelopeDto(PurchaseProcessingDto(id = 91, status = 8), code = "E1", message = "Recusada"),
            PurchaseEnvelopeDto(PurchaseProcessingDto(id = 91, status = 8), code = ""),
            PurchaseEnvelopeDto(PurchaseProcessingDto(id = 91, status = 8), redirect = "carrinho"),
            PurchaseEnvelopeDto(redirect = "carrinhos-favoritos"),
            PurchaseEnvelopeDto(redirect = "LOGIN"),
            PurchaseEnvelopeDto(redirect = ""),
            PurchaseEnvelopeDto(redirect = " login"),
            PurchaseEnvelopeDto(PurchaseProcessingDto(id = 91, status = 8), redirect = "https://example.invalid"),
        )) {
            source.envelopeOverride = envelope
            assertFails { repository.purchaseProgress(91) }
            assertTrue(repository.submitCardPayment(request) is CardSubmissionOutcome.ReviewRequired)
        }
    }

    @Test fun `processing directive retains submission identity status and polling metadata`() = runTest {
        val source = FakeSource().apply {
            processing = true
            envelopeOverride = PurchaseEnvelopeDto(
                PurchaseProcessingDto(id = 123, intervalMillis = 2345, repetitions = 7, status = 2),
                redirect = "/carrinhos/verifica-compra-processamento",
            )
        }
        val repository = PaymentRepositoryImpl(source)
        val result = (repository.submitCardPayment(CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")) as CardSubmissionOutcome.Submitted).purchase
        assertEquals(123L, result.purchaseId)
        assertEquals(2345L, result.pollingIntervalMillis)
        assertEquals(7, result.pollingAttempts)
        assertFalse(result.completed)
        val progress = repository.purchaseProgress(123)
        assertEquals(123L, progress.purchaseId)
        assertFalse(progress.completed)
        assertEquals(PurchasePolling(2345, 7), progress.nextPoll)
    }

    @Test fun `successful HTTP processing ends without requiring a status or payload`() = runTest {
        val repository = PaymentRepositoryImpl(FakeSource().apply { progressValue = null })
        val progress = repository.purchaseProgress(91)
        assertEquals(91L, progress.purchaseId)
        assertFalse(progress.completed)
        org.junit.Assert.assertNull(progress.nextPoll)
    }

    @Test fun `303 requires polling metadata instead of silently finishing`() = runTest {
        val source = FakeSource().apply { processing = true }
        val repository = PaymentRepositoryImpl(source)
        for (value in listOf(
            PurchaseProcessingDto(id = 91),
            PurchaseProcessingDto(id = 91, intervalMillis = 0),
            PurchaseProcessingDto(id = 91, repetitions = 0),
        )) {
            source.envelopeOverride = PurchaseEnvelopeDto(value)
            assertFails { repository.purchaseProgress(91) }
            assertTrue(repository.submitCardPayment(CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")) is CardSubmissionOutcome.ReviewRequired)
        }
        source.envelopeOverride = PurchaseEnvelopeDto(PurchaseProcessingDto(id = 0, intervalMillis = -1, repetitions = -1))
        val progress = repository.purchaseProgress(91)
        assertEquals(91L, progress.purchaseId)
        assertEquals(PurchasePolling(0, 0), progress.nextPoll)
    }

    @Test fun `actual mock transport keeps processing distinct from completion and rejection`() = runTest {
        for (scenario in listOf(MockBackendScenario.PURCHASE_PROCESSING, MockBackendScenario.PURCHASE_APPROVED, MockBackendScenario.PURCHASE_REJECTED)) {
            val purchaseProvider = PurchaseBackendApiProvider().apply {
                replace(NetworkClientFactory.createPurchaseBackend(
                    "https://mock.caixa.gov.br/rest/v1/", AuthTokenProvider { null },
                    mockOptions = MockBackendOptions(true, scenario),
                ))
            }
            val repository = PaymentRepositoryImpl(PaymentRemoteDataSourceImpl(
                BackendApiProvider(), LoteriasApiProvider(), PaymentApiProvider(), purchaseProvider,
            ))
            if (scenario == MockBackendScenario.PURCHASE_REJECTED) {
                assertFails { repository.purchaseProgress(91) }
            } else {
                val result = repository.purchaseProgress(91)
                assertEquals(scenario == MockBackendScenario.PURCHASE_PROCESSING, result.nextPoll != null)
                assertEquals(scenario == MockBackendScenario.PURCHASE_APPROVED, result.completed)
            }
        }
    }

    @Test fun `submission requires positive identity and absent status never means completion`() = runTest {
        val source = FakeSource()
        val repository = PaymentRepositoryImpl(source)
        val request = CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")
        for (id in listOf(null, 0L, -1L)) {
            source.envelopeOverride = PurchaseEnvelopeDto(PurchaseProcessingDto(id = id))
            assertTrue(repository.submitCardPayment(request) is CardSubmissionOutcome.ReviewRequired)
        }
        for (interval in listOf(0L, -1L)) {
            source.envelopeOverride = PurchaseEnvelopeDto(PurchaseProcessingDto(id = 1, intervalMillis = interval, repetitions = -1))
            val submission = (repository.submitCardPayment(request) as CardSubmissionOutcome.Submitted).purchase
            assertEquals(0L, submission.pollingIntervalMillis)
            assertEquals(0, submission.pollingAttempts)
            assertFalse(submission.completed)
            assertFalse(repository.purchaseProgress(1).completed)
        }
    }

    @Test fun `known purchase redirects precede payload and error validation on 200 and 303`() = runTest {
        val destinations = mapOf(
            "login" to PurchaseRedirect.LOGIN,
            "aceitar-termo-de-uso" to PurchaseRedirect.ACCEPT_TERMS,
            "cadastrar-apostador" to PurchaseRedirect.REGISTER_ACCOUNT,
            "indisponibilidade" to PurchaseRedirect.UNAVAILABLE,
        )
        val request = CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")
        for ((wireValue, destination) in destinations) {
            for (processingResponse in listOf(false, true)) {
                for (id in listOf(null, 0L, -1L, 92L)) {
                    val source = FakeSource().apply {
                        processing = processingResponse
                        envelopeOverride = PurchaseEnvelopeDto(
                            payload = id?.let { PurchaseProcessingDto(id = it, status = 8) },
                            code = "PRECONDITION", redirect = wireValue,
                        )
                    }
                    val repository = PaymentRepositoryImpl(source)
                    assertEquals(
                        CardSubmissionOutcome.RedirectRequired(destination, id?.takeIf { it > 0 }),
                        repository.submitCardPayment(request),
                    )
                    with(repository.purchaseProgress(91)) {
                        assertEquals(destination, redirect)
                        assertEquals(id?.takeIf { it > 0 } ?: 91L, purchaseId)
                        assertFalse(completed)
                        assertFalse(rejected)
                        org.junit.Assert.assertNull(nextPoll)
                    }
                }
            }
        }
    }

    @Test fun `HTTP error handler restores six redirects before status based login`() = runTest {
        val redirects = mapOf(
            "login" to PurchaseRedirect.LOGIN,
            "aceitar-termo-de-uso" to PurchaseRedirect.ACCEPT_TERMS,
            "cadastrar-apostador" to PurchaseRedirect.REGISTER_ACCOUNT,
            "indisponibilidade" to PurchaseRedirect.UNAVAILABLE,
            "carrinho" to PurchaseRedirect.CART,
            "carrinhos-favoritos" to PurchaseRedirect.FAVORITE_CARTS,
        )
        val request = CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")
        for (status in listOf(400, 401, 404, 500, 503)) {
            for ((wireValue, destination) in redirects) {
                val exception = PurchaseHttpException(status, PurchaseErrorEnvelopeDto(code = "018003", redirect = wireValue))
                val repository = PaymentRepositoryImpl(FakeSource().apply {
                    registrationFailure = exception; progressFailure = exception
                })
                assertEquals(CardSubmissionOutcome.RedirectRequired(destination), repository.submitCardPayment(request))
                with(repository.purchaseProgress(91)) {
                    assertEquals(destination, redirect)
                    assertEquals(91L, purchaseId)
                    assertFalse(completed); assertFalse(rejected)
                    org.junit.Assert.assertNull(nextPoll)
                }
            }
        }
    }

    @Test fun `cart redirects remain exclusive to HTTP errors and cannot pass through 200 or 303 handlers`() = runTest {
        for (processingResponse in listOf(false, true)) {
            for (directive in listOf("carrinho", "carrinhos-favoritos")) {
                val repository = PaymentRepositoryImpl(FakeSource().apply {
                    processing = processingResponse
                    envelopeOverride = PurchaseEnvelopeDto(
                        PurchaseProcessingDto(91, 1, 2, 8), redirect = directive,
                    )
                })
                val request = CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")
                assertEquals(CardSubmissionOutcome.ReviewRequired(91), repository.submitCardPayment(request))
                assertFails { repository.purchaseProgress(91) }
            }
        }
    }

    @Test fun `401 without directive requests authentication including absent error metadata`() = runTest {
        for (envelope in listOf(null, PurchaseErrorEnvelopeDto(), PurchaseErrorEnvelopeDto(code = "expired"))) {
            val exception = PurchaseHttpException(401, envelope)
            val repository = PaymentRepositoryImpl(FakeSource().apply {
                registrationFailure = exception; progressFailure = exception
            })
            assertEquals(PurchaseRedirect.LOGIN, repository.purchaseProgress(91).redirect)
            assertEquals(CardSubmissionOutcome.RedirectRequired(PurchaseRedirect.LOGIN),
                repository.submitCardPayment(CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")))
        }
    }

    @Test fun `unknown HTTP directives never navigate or silently fall through to login`() = runTest {
        val request = CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")
        for (status in listOf(401, 400, 500)) {
            for (directive in listOf("", "LOGIN", " carrinho", "https://untrusted.invalid", "/carrinhos/verifica-compra-processamento")) {
                val exception = PurchaseHttpException(status, PurchaseErrorEnvelopeDto(code = "011010", redirect = directive))
                val repository = PaymentRepositoryImpl(FakeSource().apply {
                    registrationFailure = exception; progressFailure = exception
                })
                assertEquals(CardSubmissionOutcome.ReviewRequired(), repository.submitCardPayment(request))
                org.junit.Assert.assertSame(exception, runCatching { repository.purchaseProgress(91) }.exceptionOrNull())
            }
        }
        val exception = PurchaseHttpException(400, null)
        val repository = PaymentRepositoryImpl(FakeSource().apply { registrationFailure = exception; progressFailure = exception })
        assertEquals(CardSubmissionOutcome.ReviewRequired(), repository.submitCardPayment(request))
        org.junit.Assert.assertSame(exception, runCatching { repository.purchaseProgress(91) }.exceptionOrNull())
    }

    @Test fun `HTTP business notices preserve legacy priority and distinguish 400 404 and 500`() = runTest {
        data class Case(val status: Int, val code: String?, val type: String?, val reason: PurchaseNoticeReason)
        val cases = listOf(
            Case(400, "011010", null, PurchaseNoticeReason.MORE_BETS_REQUIRED),
            Case(400, "011011", null, PurchaseNoticeReason.MORE_BETS_REQUIRED),
            Case(400, "011012", null, PurchaseNoticeReason.MORE_BETS_REQUIRED),
            Case(404, "011010", null, PurchaseNoticeReason.MORE_BETS_REQUIRED),
            Case(404, "011011", null, PurchaseNoticeReason.MORE_BETS_REQUIRED),
            Case(404, "011012", null, PurchaseNoticeReason.MORE_BETS_REQUIRED),
            Case(400, null, null, PurchaseNoticeReason.BUSINESS_RULE),
            Case(404, "", "1", PurchaseNoticeReason.BUSINESS_RULE),
            Case(400, "E1", "2", PurchaseNoticeReason.BUSINESS_RULE),
            Case(500, "2067", "1", PurchaseNoticeReason.INVALID_IDENTIFICATION),
            Case(500, "2067", null, PurchaseNoticeReason.INVALID_IDENTIFICATION),
            Case(500, "011010", "1", PurchaseNoticeReason.SERVER_MESSAGE),
            Case(500, "018003", "1", PurchaseNoticeReason.SERVER_MESSAGE),
            Case(500, "E1", "2", PurchaseNoticeReason.SERVER_FAILURE),
            Case(500, null, null, PurchaseNoticeReason.SERVER_FAILURE),
        )
        for (case in cases) {
            val exception = PurchaseHttpException(case.status, PurchaseErrorEnvelopeDto(case.code, message = "Mensagem", type = case.type))
            val repository = PaymentRepositoryImpl(FakeSource().apply { registrationFailure = exception; progressFailure = exception })
            val expected = PurchaseNotice(case.reason, "Mensagem", case.code)
            assertEquals(CardSubmissionOutcome.NoticeRequired(expected),
                repository.submitCardPayment(CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")))
            with(repository.purchaseProgress(91)) {
                assertEquals(expected, notice)
                assertEquals(91L, purchaseId)
                assertFalse(completed); assertFalse(rejected)
                org.junit.Assert.assertNull(redirect)
                org.junit.Assert.assertNull(nextPoll)
            }
        }
    }

    @Test fun `other unknown error codes remain inconclusive rather than a dismissible notice`() = runTest {
        for ((status, code) in listOf(422 to "E1", 503 to "E1")) {
            val exception = PurchaseHttpException(status, PurchaseErrorEnvelopeDto(code = code))
            val repository = PaymentRepositoryImpl(FakeSource().apply { registrationFailure = exception; progressFailure = exception })
            assertEquals(CardSubmissionOutcome.ReviewRequired(),
                repository.submitCardPayment(CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")))
            org.junit.Assert.assertSame(exception, runCatching { repository.purchaseProgress(91) }.exceptionOrNull())
        }
    }

    @Test fun `018003 requests registration for business errors but retains 401 and 500 priority`() = runTest {
        for (status in listOf(400, 404, 408, 422, 503)) {
            val exception = PurchaseHttpException(status, PurchaseErrorEnvelopeDto(code = "018003", message = "Cadastrar?"))
            val repository = PaymentRepositoryImpl(FakeSource().apply { registrationFailure = exception; progressFailure = exception })
            val expected = PurchaseNotice(PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION,
                if (status == 408) "Tempo de resposta esgotou" else "Cadastrar?", "018003")
            assertEquals(CardSubmissionOutcome.NoticeRequired(expected),
                repository.submitCardPayment(CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")))
            assertEquals(expected, repository.purchaseProgress(91).notice)
        }
    }

    @Test fun `303 registration code is distinct from 200 and a processing redirect with an error`() = runTest {
        for (processingResponse in listOf(false, true)) {
            for (directive in listOf(null, "/carrinhos/verifica-compra-processamento")) {
                val repository = PaymentRepositoryImpl(FakeSource().apply {
                    processing = processingResponse
                    envelopeOverride = PurchaseEnvelopeDto(code = "018003", message = "Cadastrar?", redirect = directive)
                })
                val request = CardPaymentRequest(5, PaymentCardSelection.Saved(savedCard(5)), "123")
                if (processingResponse && directive == null) {
                    val expected = PurchaseNotice(PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION, "Cadastrar?", "018003")
                    assertEquals(CardSubmissionOutcome.NoticeRequired(expected), repository.submitCardPayment(request))
                    assertEquals(expected, repository.purchaseProgress(91).notice)
                } else {
                    assertEquals(CardSubmissionOutcome.ReviewRequired(), repository.submitCardPayment(request))
                    assertFails { repository.purchaseProgress(91) }
                }
            }
        }
    }

    private fun assertFails(block: suspend () -> Unit) = runTest {
        var failed = false
        try { block() } catch (_: RuntimeException) { failed = true }
        assertTrue(failed)
    }

    private class FakeSource : PaymentRemoteDataSource {
        var methodsValue: JsonElement? = json("[{\"id\":6,\"nomePagamentoCompra\":\"Pix\",\"nomePagamentoPremio\":\"Prêmio\",\"padrao\":true,\"publicKey\":\"key\"}]")
        var cardsValue: JsonElement? = json("[{\"idCartao\":42,\"primeirosDigitos\":\"411111\",\"ultimosDigitos\":\"1111\",\"mesValidade\":12,\"anoValidade\":2030,\"tamanhoCodigoSeguranca\":4,\"nomeMetodoPagamento\":\"Visa\",\"idMetodoPagamento\":\"visa\",\"tokenCartao\":\"token\"}]")
        var pixValue: JsonElement? = json("{\"pixCopiaECola\":\"PIX-CODE\",\"txId\":\"tx\",\"qrcodeArrByte\":\"qr\",\"dataHoraCriacao\":\"now\",\"dataHoraExpiracao\":\"later\",\"idCompra\":91,\"valorCobranca\":10.5}")
        var progressValue: JsonElement? = json("{\"id\":91,\"idSituacaoCompra\":2}")
        var envelopeOverride: PurchaseEnvelopeDto? = null
        var processing = false
        var preparationFailure: Exception? = null
        var registrationFailure: Exception? = null
        var progressFailure: Exception? = null
        var damValue: JsonElement? = json("{\"payload\":true,\"mensagem\":\"Represa ativa\"}")
        var paymentMethodValue: JsonElement? = json("{\"publicKey\":\"key\"}")
        var profileValue: UserProfileDto? = UserProfileDto(cpf = "12345678901", name = "Pessoa", email = "mock@caixa.gov.br")
        var customerToken: String? = "customer"
        var installmentsValue: JsonElement = json("[{\"payment_method_id\":\"visa\"}]")
        var mpTokenValue: JsonElement = json("{\"id\":\"token\",\"luhn_validation\":true}")
        var rpValue: JsonElement = json("{\"customerCardToken\":\"rp-token\",\"brand\":\"VISA\"}")
        var purchaseValue: JsonElement? = json("{\"id\":91,\"periodicidade\":1,\"repeticoes\":2,\"idSituacaoCompra\":1}")
        var deletedMethod = 0L
        var pixBody: JsonElement? = null
        var registerBody: JsonElement? = null
        var mpBody: JsonElement? = null
        var rpBody: JsonElement? = null
        var bin = ""
        var authorizedCard = ""
        val calls = mutableListOf<String>()
        override suspend fun methods() = methodsValue
        override suspend fun cards(paymentMethodId: Long) = cardsValue
        override suspend fun deleteCard(cardId: String, paymentMethodId: Long) = cardsValue.also { deletedMethod = paymentMethodId }
        override suspend fun generatePix(body: JsonElement) = pixValue.also { pixBody = body }
        override suspend fun purchaseProgress(purchaseId: Long): PurchaseBackendResponse {
            progressFailure?.let { throw it }
            return PurchaseBackendResponse(envelopeOverride ?: envelope(progressValue), processing)
        }
        override suspend fun validateDam() = damValue
        override suspend fun paymentMethod(paymentMethodId: Long) = paymentMethodValue.also {
            calls += "paymentMethod"
            preparationFailure?.let { throw it }
        }
        override suspend fun profile() = profileValue.also { calls += "profile" }
        override suspend fun recargaPayCustomerToken() = customerToken.also { calls += "rpCustomer" }
        override suspend fun mercadoPagoInstallments(bin: String, publicKey: String) = installmentsValue.also { calls += "mpInstallments"; this.bin = bin }
        override suspend fun mercadoPagoCardToken(publicKey: String, body: JsonElement) = mpTokenValue.also { calls += "mpToken"; mpBody = body }
        override suspend fun recargaPayCreateCard(token: String, body: JsonElement) = rpValue.also { calls += "rpCreate"; rpBody = body }
        override suspend fun recargaPayAuthorizeCard(cardToken: String, token: String, body: JsonElement) = rpValue.also { calls += "rpAuthorize"; authorizedCard = cardToken; rpBody = body }
        override suspend fun registerCardPurchase(body: JsonElement): PurchaseBackendResponse {
            calls += "register"; registerBody = body
            registrationFailure?.let { throw it }
            return PurchaseBackendResponse(envelopeOverride ?: envelope(purchaseValue), processing)
        }
    }

    companion object {
        private fun json(value: String): JsonElement = JsonParser.parseString(value)
        private fun envelope(value: JsonElement?) = PurchaseEnvelopeDto(Gson().fromJson(value, PurchaseProcessingDto::class.java))
        private fun savedCard(method: Long) = SavedPaymentCard("saved", method, "411111", "1111", 12, 2030, 3, "Visa", "visa", "saved-token")
        @Suppress("UNCHECKED_CAST")
        private fun <T> proxy(type: Class<T>, handler: (java.lang.reflect.Method) -> Any?): T =
            java.lang.reflect.Proxy.newProxyInstance(type.classLoader, arrayOf(type)) { _, method, _ -> handler(method) } as T
    }
}
