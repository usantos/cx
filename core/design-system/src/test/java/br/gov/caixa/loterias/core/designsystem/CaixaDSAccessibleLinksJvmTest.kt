package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import br.gov.caixa.loterias.core.designsystem.text.withCaixaDSAccessibleLinks
import org.junit.Assert.assertEquals
import org.junit.Test

class CaixaDSAccessibleLinksJvmTest {
    @Test fun bothUrlTagCasesPreserveRangesStylesAndDelegateActivationOnce() {
        val clicked = mutableListOf<String>()
        val fixtureText = "Termos Ajuda"
        val termsEnd = "Termos".length
        val helpStart = termsEnd + 1
        val source = AnnotatedString.Builder(fixtureText).apply {
            addStyle(SpanStyle(fontWeight = FontWeight.Bold), 0, termsEnd)
            addStringAnnotation("URL", "https://example.test/terms", 0, termsEnd)
            addStringAnnotation("url", "https://example.test/help", helpStart, fixtureText.length)
        }.toAnnotatedString()
        val result = source.withCaixaDSAccessibleLinks(clicked::add)
        assertEquals(source.text, result.text)
        assertEquals(source.spanStyles, result.spanStyles)
        assertEquals(source.getStringAnnotations(0, source.length), result.getStringAnnotations(0, result.length))
        val links = result.getLinkAnnotations(0, result.length)
        assertEquals(listOf(0, helpStart), links.map { it.start })
        links.forEach { range ->
            val annotation = range.item as LinkAnnotation.Clickable
            annotation.linkInteractionListener!!.onClick(annotation)
        }
        assertEquals(listOf("https://example.test/terms", "https://example.test/help"), clicked)
    }
}
