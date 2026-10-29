package br.gov.caixa.loterias.core.designsystem.artwork

import br.gov.caixa.loterias.core.designsystem.R as LocalizedR

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale

/** Marcas Compose do Caixa DS usadas nas opções de canais de premiação. */
@Composable
fun CaixaDSPrizeLotteryMark(modifier: Modifier = Modifier) {
    CaixaDSLotteryMark(modifier)
}

@Composable
fun CaixaDSPrizeMercadoPagoMark(modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Image(
        imageVector = CaixaDSPaymentVectors.MercadoPago,
        contentDescription = uiStrings.getString(LocalizedR.string.ui_mercado_pago_fe6402),
        modifier = modifier,
        contentScale = ContentScale.Crop,
        alignment = Alignment.CenterStart
    )
}

@Composable
fun CaixaDSMercadoPagoBrand(modifier: Modifier = Modifier) {
    CaixaDSPaymentProviderMark(CaixaDSPaymentProvider.MERCADO_PAGO, modifier)
}
