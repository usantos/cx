package br.gov.caixa.loterias.feature.payment.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentBrandMark
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentProvider
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentProviderMark
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.presentation.mapper.acceptedBrands
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentVisualAssets
import br.gov.caixa.loterias.feature.payment.ui.R as DeliveryR

@Composable
internal fun AcceptedBrandMarks(paymentMethodId: Long, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.End) {
        acceptedBrands(paymentMethodId).chunked(ACCEPTED_BRAND_COLUMN_COUNT).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { brand -> LoteriasCaixaDSPaymentBrandMark(brand, (Modifier.size(30.dp)).uiId("pay.0040")) }
            }
        }
    }
}

internal fun PaymentMethod.toLoteriasCaixaDSProvider(): LoteriasCaixaDSPaymentProvider =
    if (id == PaymentMethod.RECARGA_PAY_ID) LoteriasCaixaDSPaymentProvider.RECARGA_PAY else LoteriasCaixaDSPaymentProvider.MERCADO_PAGO

@Composable
fun LargeFontProviderIdentity(method: PaymentMethod, visualAssets: PaymentVisualAssets) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_pagar_via_191e93),
            (Modifier.fillMaxWidth()).uiId("pay.0047"),
            color = LoteriasCaixaDS.colors.graphite
        )
        LoteriasCaixaDSPaymentProviderMark(
            provider = method.toLoteriasCaixaDSProvider(),
            recargaPayResourceId = visualAssets.recargaPayLogoResourceId,
            modifier = (
                if (method.id == PaymentMethod.MERCADO_PAGO_ID) {
                    Modifier.width(107.dp).height(26.dp)
                } else {
                    Modifier.width(129.dp).height(43.dp)
                }
                ).uiId("pay.0048")
        )
        Text(
            uiStrings.getString(DeliveryR.string.ui_bandeiras_aceitas_bf739a),
            (Modifier.fillMaxWidth()).uiId("pay.0049"),
            color = LoteriasCaixaDS.colors.graphite
        )
        AcceptedBrandMarks(method.id, modifier = Modifier.uiId("pay.0050"))
    }
}

@Composable
fun LegacyProviderIdentity(method: PaymentMethod, visualAssets: PaymentVisualAssets) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier
            .fillMaxWidth()
            .height(92.dp)
            .padding(start = 16.dp, top = 8.dp, end = 16.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_pagar_via_191e93),
                color = LoteriasCaixaDS.colors.graphite,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                modifier = Modifier.uiId("pay.0051")
            )
            LoteriasCaixaDSPaymentProviderMark(
                provider = method.toLoteriasCaixaDSProvider(),
                recargaPayResourceId = visualAssets.recargaPayLogoResourceId,
                modifier = (
                    if (method.id == PaymentMethod.MERCADO_PAGO_ID) {
                        Modifier.padding(top = 16.dp).width(107.dp).height(26.dp)
                    } else {
                        Modifier.padding(top = 16.dp).width(129.dp).height(43.dp)
                    }
                    ).uiId("pay.0052")
            )
        }
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_bandeiras_aceitas_bf739a),
                color = LoteriasCaixaDS.colors.graphite,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                modifier = Modifier.uiId("pay.0053")
            )
            AcceptedBrandMarks(method.id, (Modifier.padding(top = 4.dp)).uiId("pay.0054"))
        }
    }
}

private const val ACCEPTED_BRAND_COLUMN_COUNT = 3
