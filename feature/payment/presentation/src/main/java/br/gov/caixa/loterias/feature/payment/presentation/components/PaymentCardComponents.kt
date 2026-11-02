package br.gov.caixa.loterias.feature.payment.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentBrandMark
import br.gov.caixa.loterias.feature.payment.R
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.screen.LegacyCardProviderLogo

@Composable
internal fun ProviderHeader(providerName: String = "Mercado Pago", onChange: (() -> Unit)? = null) {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardProviderHeader(
        stringResource(LocalizedR.string.ui_voltar_59dc92),
        stringResource(LocalizedR.string.ui_alterar_ded212),
        logo = { LegacyCardProviderLogo(providerName.contains("recarga", ignoreCase = true), it) },
        onChange = onChange
    )
}

@Composable
internal fun LegacyPaymentInput(label: String, value: String, onValueChange: (String) -> Unit) {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardTextInput(label, value, onValueChange)
}

@Composable
internal fun CvvEntry(value: String, length: Int = 3, onValueChange: (String) -> Unit) {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardSecurityCode(value, length, onValueChange)
}

@Composable
internal fun CreditCardFront(
    brand: String = "VISA",
    number: String = "•••• •••• •••• 4589",
    holderName: String = stringResource(LocalizedR.string.format_card_holder_placeholder),
    expiration: String = "12/2029",
) {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCreditCardFront(
        br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCreditCardContent(
            number,
            holderName,
            expiration,
            stringResource(LocalizedR.string.ui_numero_67e494),
            stringResource(LocalizedR.string.ui_nome_508690),
            stringResource(LocalizedR.string.ui_validade_5f7aec)
        ),
        brand = { LegacyCreditCardBrand(brand, it) }
    )
}

@Composable
internal fun CreditCardBack(showMagnifier: Boolean, brand: String = "VISA") {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCreditCardBack(
        showMagnifier,
        brand = { LegacyCreditCardBrand(brand, it) },
        magnifier = painterResource(R.drawable.legacy_card_magnifier),
        magnifierText = stringResource(LocalizedR.string.ui_xxx_9b38b8)
    )
}

@Composable
private fun LegacyCreditCardBrand(brand: String, modifier: Modifier) {
    LoteriasCaixaDSPaymentBrandMark(
        brand,
        modifier,
        fallback = painterResource(
            if (brand.equals("diners", ignoreCase = true)) R.drawable.legacy_saved_ic_diners else R.drawable.legacy_saved_card_cvc
        )
    )
}

@Composable
internal fun LegacySmallHeading(text: String) {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCardHeading(text)
}
