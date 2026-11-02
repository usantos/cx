package br.gov.caixa.loterias.feature.onboarding.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import br.gov.caixa.loterias.feature.onboarding.R as LocalizedR

/** Selo de jogo responsável original da tela de restrição etária. */
@Composable
internal fun ResponsibleGamingIcon(modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Image(
        painter = painterResource(LocalizedR.drawable.responsible_gaming_icon),
        contentDescription = uiStrings.getString(LocalizedR.string.ui_apostar_e_para_maiores_de_18_anos_83aea7),
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
