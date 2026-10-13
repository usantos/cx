package br.gov.caixa.loterias.feature.betting

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BettingTutorialTest {
    @Test fun `pool tutorial preserves legacy page order speeds and controls`() {
        val definition = tutorialDefinition("betting/tutorial")

        assertEquals(
            listOf(
                "tutorial_bolao_introducao.json",
                "tutorial_bolao_passo_2.json",
                "tutorial_bolao_passo_4.json",
                "tutorial_bolao_passo_3.json",
                "tutorial_bolao_final.json",
            ),
            definition.pages.map(TutorialPage::media),
        )
        assertTrue(definition.usesLegacyOverlayControls)
        assertEquals(.8f, definition.pages.first().speed)
        assertFalse(definition.pages.first().loops)
        assertEquals("Vamos começar", tutorialPrimaryLabel(definition, 0))
        assertNull(tutorialPrimaryLabel(definition, 2))
        assertEquals("Escolher\nmeu bolão", tutorialPrimaryLabel(definition, 4))
        assertEquals(4, tutorialPageAfterSecondary(definition, 1))
        assertNull(tutorialPageAfterSecondary(definition, 4))
    }

    @Test fun `plus tutorial exposes seven legacy lottie pages`() {
        val definition = tutorialDefinition("betting/tutorial-plus")

        assertEquals(7, definition.pages.size)
        assertEquals("tutorial_introducao_mm.json", definition.pages.first().media)
        assertEquals("tutorial_mm_final.json", definition.pages.last().media)
        assertEquals("Aposte agora", definition.finalPrimaryLabel)
        assertEquals("Fechar", tutorialSecondaryLabel(definition, definition.pages.lastIndex))
    }

    @Test fun `super seven preserves three animated steps and button states`() {
        val definition = tutorialDefinition("betting/tutorial-super-seven")

        assertEquals(listOf("super7_passo1", "super7_passo2", "super7_passo3"), definition.pages.map(TutorialPage::media))
        assertTrue(definition.pages.all { it.mediaKind == TutorialMediaKind.DRAWABLE_GIF })
        assertFalse(definition.usesLegacyOverlayControls)
        assertEquals("Avançar", tutorialPrimaryLabel(definition, 0))
        assertEquals("Iniciar aposta", tutorialPrimaryLabel(definition, 2))
        assertNull(tutorialPageAfterPrimary(definition, 2))
        assertNull(tutorialPageAfterSecondary(definition, 0))
    }

    @Test(expected = IllegalStateException::class)
    fun `unknown tutorial fails instead of silently rendering generic content`() {
        tutorialDefinition("betting/unknown")
    }
}
