package br.gov.caixa.loterias.feature.onboarding.presentation.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import br.gov.caixa.loterias.feature.onboarding.R as LocalizedR

/** Selo de jogo responsável original da tela de restrição etária. */
@Composable
internal fun ResponsibleGamingIcon(modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val bitmap = remember {
        val bytes = Base64.decode(LEGACY_RESPONSIBLE_GAMING_ICON, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
    }
    Image(
        bitmap = bitmap,
        contentDescription = uiStrings.getString(LocalizedR.string.ui_apostar_e_para_maiores_de_18_anos_83aea7),
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

private const val LEGACY_RESPONSIBLE_GAMING_ICON =
    "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" +
        "QmCC"
