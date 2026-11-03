package br.gov.caixa.loterias.feature.payment.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentBrandMark
import br.gov.caixa.loterias.feature.payment.presentation.screen.LegacyCardProviderLogoDelivered
import br.gov.caixa.loterias.feature.payment.ui.R as DeliveryR

@Composable
fun ProviderHeader(providerName: String = "Mercado Pago", onChange: (() -> Unit)? = null) {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardProviderHeader(
        stringResource(DeliveryR.string.ui_voltar_59dc92),
        stringResource(DeliveryR.string.ui_alterar_ded212),
        logo = { LegacyCardProviderLogoDelivered(providerName.contains("recarga", ignoreCase = true), it) },
        onChange = onChange
    )
}

@Composable
fun LegacyPaymentInput(label: String, value: String, onValueChange: (String) -> Unit) {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardTextInput(label, value, onValueChange)
}

@Composable
fun CvvEntry(value: String, length: Int = 3, onValueChange: (String) -> Unit) {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardSecurityCode(value, length, onValueChange)
}

@Composable
fun CreditCardFront(
    brand: String = "VISA",
    number: String = "•••• •••• •••• 4589",
    holderName: String = stringResource(DeliveryR.string.format_card_holder_placeholder),
    expiration: String = "12/2029",
) {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCreditCardFront(
        br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCreditCardContent(
            number,
            holderName,
            expiration,
            stringResource(DeliveryR.string.ui_numero_67e494),
            stringResource(DeliveryR.string.ui_nome_508690),
            stringResource(DeliveryR.string.ui_validade_5f7aec)
        ),
        brand = { LegacyCreditCardBrand(brand, it) }
    )
}

@Composable
fun CreditCardBack(showMagnifier: Boolean, brand: String = "VISA") {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCreditCardBack(
        showMagnifier,
        brand = { LegacyCreditCardBrand(brand, it) },
        magnifier = painterResource(DeliveryR.drawable.legacy_card_magnifier),
        magnifierText = stringResource(DeliveryR.string.ui_xxx_9b38b8)
    )
}

@Composable
private fun LegacyCreditCardBrand(brand: String, modifier: Modifier) {
    LoteriasCaixaDSPaymentBrandMark(
        brand,
        modifier,
        fallback = painterResource(
            if (brand.equals(
                    "diners",
                    ignoreCase = true
                )
            ) {
                DeliveryR.drawable.legacy_saved_ic_diners
            } else {
                DeliveryR.drawable.legacy_saved_card_cvc
            }
        )
    )
}

@Composable
fun LegacySmallHeading(text: String) {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardHeading(text)
}
