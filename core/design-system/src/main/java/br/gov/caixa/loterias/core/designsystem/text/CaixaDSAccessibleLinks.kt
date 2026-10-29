package br.gov.caixa.loterias.core.designsystem.text

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation

/** Preserva a apresentação; o chamador controla a validação da URL, a confirmação e a navegação. */
fun AnnotatedString.withCaixaDSAccessibleLinks(onClick: (String) -> Unit): AnnotatedString {
    val builder = AnnotatedString.Builder(this)
    getStringAnnotations(0, length).filter { it.tag.equals("URL", ignoreCase = true) }.forEach { annotation ->
        if (annotation.start < annotation.end) {
            builder.addLink(
                LinkAnnotation.Clickable(annotation.item, linkInteractionListener = { onClick(annotation.item) }),
                annotation.start,
                annotation.end
            )
        }
    }
    return builder.toAnnotatedString()
}
