@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.web.CaixaDSWebPage

internal val webPreviews0 = mapOf(
    preview("br.gov.caixa.loterias.core.designsystem.web.CaixaDSWebPage") { state ->
        with(state) {
            CaixaDSWebPage(
                url = "file:///android_asset/catalog.html",
                modifier = Modifier.fillMaxWidth().height(220.dp),
                isUrlAllowed = { it.startsWith("file:///android_asset/") },
                javaScriptEnabled = false
            )
        }
    }
)
