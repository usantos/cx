@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.html.CaixaDSHtmlDocument

internal val htmlPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.html.CaixaDSHtmlDocument") { state ->
        with(state) {
            CaixaDSHtmlDocument(
                html = html,
                modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp)
            )
        }
    }
)
