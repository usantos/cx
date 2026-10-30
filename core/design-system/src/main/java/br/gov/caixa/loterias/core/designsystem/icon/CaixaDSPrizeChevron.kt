package br.gov.caixa.loterias.core.designsystem.icon

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import br.gov.caixa.loterias.core.designsystem.R

/** Bitmap seta_direita_preta específico por densidade da opção de premiação instalada. */
@Composable
fun CaixaDSPrizeChevron(modifier: Modifier = Modifier) {
    Image(painterResource(R.drawable.legacy_prize_chevron), contentDescription = null, modifier = modifier)
}
