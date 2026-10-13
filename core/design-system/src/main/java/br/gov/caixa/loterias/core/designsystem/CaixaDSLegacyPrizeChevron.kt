package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

/** Density-specific seta_direita_preta bitmap from the installed prize option. */
@Composable
fun CaixaDSLegacyPrizeChevron(modifier: Modifier = Modifier) {
    Image(painterResource(R.drawable.legacy_prize_chevron), contentDescription = null, modifier = modifier)
}
