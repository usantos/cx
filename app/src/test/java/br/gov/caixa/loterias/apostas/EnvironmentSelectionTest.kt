package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.environment.model.EffectiveEnvironment
import br.gov.caixa.loterias.core.domain.environment.usecase.EnvironmentSelection
import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment
import br.gov.caixa.loterias.core.domain.environment.usecase.applyEnvironmentChange
import br.gov.caixa.loterias.core.domain.environment.usecase.reconcilePendingEnvironment
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class EnvironmentSelectionTest {
    private val defaults = EffectiveEnvironment(
        apiBaseUrl = "https://fixed.test/", ssoAuthUrl = "https://sso.test/auth",
        ssoTokenUrl = "https://sso.test/token", ssoLogoutUrl = "https://sso.test/logout",
        ssoAccountUrl = "https://sso.test/account", ssoClientId = "client",
        ssoRedirectUri = "app:/callback", mercadoPagoBaseUrl = "https://mp.test/",
        recargaPayBaseUrl = "https://rp.test/", queueAccount = "account", queueSystem = "queue",
    )

    @Test fun `missing and invalid TQS preferences resolve to legacy default`() {
        listOf(null, "", "bad", "HML").forEach { saved ->
            val selection = EnvironmentSelection("tqs", saved, defaults) {}
            assertEquals(TestEnvironment.EXTERNO_ESTEIRA, selection.selected)
            assertEquals("https://silce.simlo.tqs.caixa.gov.br/silce-servico-rest/rest/v1/", selection.effective.apiBaseUrl)
            assertEquals(selection.effective.apiBaseUrl, selection.effective.sessionNamespace)
        }
    }

    @Test fun `production and automation ignore the saved environment for every service`() {
        listOf("prd", "automacao").forEach { flavor ->
            val selection = EnvironmentSelection(flavor, "PILOTO", defaults) {}
            assertEquals(defaults, selection.effective)
            assertEquals(flavor != "prd", selection.selectorVisible)
        }
    }

    @Test fun `change persists for restart without mutating the active service snapshot`() {
        var saved: String? = "EXTERNO_ESTEIRA"
        val selection = EnvironmentSelection("tqs", saved, defaults) { saved = it }
        val active = selection.effective
        assertFalse(selection.select(TestEnvironment.EXTERNO_ESTEIRA))
        assertTrue(selection.select(TestEnvironment.PILOTO))
        assertEquals("PILOTO", saved)
        assertSame(active, selection.effective)
        val reopened = EnvironmentSelection("tqs", saved, defaults) {}.effective
        assertEquals("https://pilotoloterias.caixa.gov.br/silce-servico-rest/rest/v1/", reopened.apiBaseUrl)
        assertEquals("loteriasplt", reopened.queueSystem)
        assertEquals("caixaloteriasdev", reopened.queueAccount)
        assertEquals(reopened.apiBaseUrl, reopened.sessionNamespace)
        assertEquals("https://logindes.caixa.gov.br/auth/realms/internet/protocol/openid-connect/auth", reopened.ssoAuthUrl)
        assertEquals("https://logindes.caixa.gov.br/auth/realms/internet/protocol/openid-connect/token", reopened.ssoTokenUrl)
        assertEquals("https://logindes.caixa.gov.br/auth/realms/internet/protocol/openid-connect/logout", reopened.ssoLogoutUrl)
        assertEquals("https://logindes.caixa.gov.br/auth/realms/internet/account", reopened.ssoAccountUrl)
        assertEquals("https://api.mercadopago.com/", reopened.mercadoPagoBaseUrl)
        assertEquals("https://secure-sandbox.recargapay.com/", reopened.recargaPayBaseUrl)
        assertEquals("cli-mob-lce", reopened.ssoClientId)
        assertEquals("br.gov.caixa.caixaloterias:/oauth2Callback", reopened.ssoRedirectUri)
    }

    @Test fun `production cannot persist selections and automation only persists the spinner`() {
        var saved: String? = null
        val prd = EnvironmentSelection("prd", null, defaults) { saved = it }
        assertFalse(prd.select(TestEnvironment.PILOTO))
        assertNull(saved)
        val automation = EnvironmentSelection("automacao", null, defaults) { saved = it }
        assertTrue(automation.select(TestEnvironment.PILOTO))
        assertEquals("PILOTO", saved)
        assertEquals(defaults, automation.effective)
    }
    @Test fun `switch clears the previous session before saving and cleanup failure retains old environment`() = runTest {
        val events = mutableListOf<String>()
        val selection = EnvironmentSelection("tqs", "EXTERNO_ESTEIRA", defaults) { events += "persist:$it" }
        assertTrue(applyEnvironmentChange(selection, TestEnvironment.INTERNO) { events += "clear-session" })
        assertEquals(listOf("clear-session", "persist:INTERNO"), events)
        events.clear()
        try {
            applyEnvironmentChange(selection, TestEnvironment.PILOTO) {
                events += "cleanup-failed"
                throw IllegalStateException("storage unavailable")
            }
            fail("A cleanup failure must block the environment change")
        } catch (_: IllegalStateException) {
            assertEquals(listOf("cleanup-failed"), events)
            assertEquals("https://silce.simlo.tqs.caixa.gov.br/silce-servico-rest/rest/v1/", selection.effective.apiBaseUrl)
        }
    }

    @Test fun `same environment and production do not invalidate the session`() = runTest {
        val selection = EnvironmentSelection("tqs", "INTERNO", defaults) { fail("must not persist") }
        assertFalse(applyEnvironmentChange(selection, TestEnvironment.INTERNO) { fail("must not clear") })
        val production = EnvironmentSelection("prd", "INTERNO", defaults) { fail("must not persist") }
        assertFalse(applyEnvironmentChange(production, TestEnvironment.PILOTO) { fail("must not clear") })
    }

    @Test fun `environment change discards the volatile access token while old configuration stays active`() = runTest {
        val tokens = SessionTokenStore().apply { replace("old-environment-access-token") }
        var saved: String? = "INTERNO"
        val selection = EnvironmentSelection("tqs", saved, defaults) { saved = it }
        assertTrue(applyEnvironmentChange(selection, TestEnvironment.PILOTO) { tokens.clear() })
        assertNull(tokens.token())
        assertEquals("PILOTO", saved)
        assertEquals("https://simlo.tqs.caixa/silce-servico-rest/rest/v1/", selection.effective.sessionNamespace)
        val nextLaunch = EnvironmentSelection("tqs", saved, defaults) {}
        assertEquals("https://pilotoloterias.caixa.gov.br/silce-servico-rest/rest/v1/", nextLaunch.effective.sessionNamespace)
    }

    @Test fun `restored pending choice already active is cleared even before persisted flag was saved`() {
        assertNull(reconcilePendingEnvironment("PILOTO", TestEnvironment.PILOTO))
        assertEquals("PILOTO", reconcilePendingEnvironment("PILOTO", TestEnvironment.EXTERNO_ESTEIRA))
        assertNull(reconcilePendingEnvironment(null, TestEnvironment.PILOTO))
    }

}
