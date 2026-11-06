package br.gov.caixa.loterias.core.designsystem.artwork

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR

/** Marcas Compose do Caixa DS usadas nas opções de canais de premiação. */
@Composable
fun LoteriasCaixaDSPrizeLotteryMark(modifier: Modifier = Modifier) {
    LoteriasCaixaDSLotteryMark(modifier)
}

@Composable
fun LoteriasCaixaDSPrizeMercadoPagoMark(modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Image(
        imageVector = LoteriasCaixaDSPaymentVectors.MercadoPago,
        contentDescription = uiStrings.getString(LocalizedR.string.ui_mercado_pago_fe6402),
        modifier = modifier,
        contentScale = ContentScale.Crop,
        alignment = Alignment.CenterStart
    )
}

@Composable
fun LoteriasCaixaDSMercadoPagoBrand(modifier: Modifier = Modifier) {
    LoteriasCaixaDSPaymentProviderMark(LoteriasCaixaDSPaymentProvider.MERCADO_PAGO, modifier)
}
