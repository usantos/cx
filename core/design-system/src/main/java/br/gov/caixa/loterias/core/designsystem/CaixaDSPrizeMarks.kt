package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale

/** Caixa DS Compose marks used by the prize-channel options. */
@Composable
fun CaixaDSPrizeLotteryMark(modifier: Modifier = Modifier) {
    CaixaDSLotteryMark(modifier)
}

@Composable
fun CaixaDSPrizeMercadoPagoMark(modifier: Modifier = Modifier) {
    Image(
        imageVector = CaixaDSPaymentVectors.MercadoPago,
        contentDescription = "Mercado Pago",
        modifier = modifier,
        contentScale = ContentScale.Crop,
        alignment = Alignment.CenterStart,
    )
}

@Composable
fun CaixaDSMercadoPagoBrand(modifier: Modifier = Modifier) {
    CaixaDSPaymentProviderMark(CaixaDSPaymentProvider.MERCADO_PAGO, modifier)
}
