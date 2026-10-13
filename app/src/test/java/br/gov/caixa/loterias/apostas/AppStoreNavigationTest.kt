package br.gov.caixa.loterias.apostas

import org.junit.Assert.*
import org.junit.Test

class AppStoreNavigationTest {
    @Test fun `market handler is preferred without opening browser`() {
        val opened = mutableListOf<String>()
        assertTrue(openAppStore("br.gov.caixa.loterias.apostas") { opened.add(it); true })
        assertEquals(listOf("market://details?id=br.gov.caixa.loterias.apostas"), opened)
    }

    @Test fun `missing market handler falls back to Play HTTPS using running package`() {
        val opened = mutableListOf<String>()
        assertTrue(openAppStore("br.gov.caixa.loterias.apostas.tqs") {
            opened.add(it)
            it.startsWith("https://")
        })
        assertEquals(listOf("market://details?id=br.gov.caixa.loterias.apostas.tqs",
            "https://play.google.com/store/apps/details?id=br.gov.caixa.loterias.apostas.tqs"), opened)
    }

    @Test fun `missing both handlers reports failure`() {
        var attempts = 0
        assertFalse(openAppStore("example.app") { attempts++; false })
        assertEquals(2, attempts)
    }

    @Test fun `startup failures have no generic completion destination`() {
        assertNull(nextDestination("onboarding/update"))
        assertNull(nextDestination("onboarding/unavailable"))
        assertEquals("account/login", nextDestination("onboarding/introduction"))
    }
}
