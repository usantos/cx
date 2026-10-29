package br.gov.caixa.loterias.feature.payment.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPaymentBrandMark
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCard as Card
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCardDefaults as CardDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.payment.R
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.mapper.legacyPaymentTextStyle
import br.gov.caixa.loterias.feature.payment.presentation.screen.LegacyCardProviderLogo

@Composable
internal fun ProviderHeader(providerName: String = "Mercado Pago", onChange: (() -> Unit)? = null) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().padding(top = 8.dp).height(57.dp)
            .background(CaixaDS.colors.paymentCard),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(5.dp).fillMaxSize().background(CaixaDS.colors.savedCardAccent))
        Icon(
            CaixaDSIcons.Back,
            uiStrings.getString(LocalizedR.string.ui_voltar_59dc92),
            tint = CaixaDS.colors.cartDark,
            modifier = (
                Modifier.padding(start = 16.dp)
                    .then(if (onChange != null) Modifier.uiId("pay.0001").clickable(onClick = onChange) else Modifier)
                ).uiId("pay.0002")
        )
        Box(Modifier.padding(start = 16.dp).weight(1f)) {
            LegacyCardProviderLogo(providerName.contains("recarga", ignoreCase = true), modifier = Modifier.uiId("pay.0003"))
        }
        Text(
            uiStrings.getString(LocalizedR.string.ui_alterar_ded212),
            (
                Modifier.padding(end = 28.dp)
                    .then(if (onChange != null) Modifier.uiId("pay.0004").clickable(onClick = onChange) else Modifier)
                ).uiId("pay.0005"),
            color = CaixaDS.colors.cartDark
        )
    }
}

@Composable
internal fun LegacyPaymentInput(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 14.dp)) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = (Modifier.fillMaxWidth().height(40.dp)).uiId("pay.0006"),
            singleLine = true,
            textStyle = CaixaDS.typography.bodyLarge.copy(color = CaixaDS.colors.text),
            decorationBox = { inner ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                    if (value.isBlank()) {
                        Text(label, color = CaixaDS.colors.primaryButton, fontSize = 16.sp, modifier = Modifier.uiId("pay.0007"))
                    }
                    inner()
                }
            }
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDS.colors.line))
    }
}

@Composable
internal fun CvvEntry(value: String, length: Int = 3, onValueChange: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = (Modifier.padding(top = 8.dp)).uiId("pay.0008"),
        textStyle = CaixaDS.typography.bodyLarge.copy(color = CaixaDSColorTokens.Transparent),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                repeat(length) { index ->
                    Box(
                        Modifier.width(40.dp).height(60.dp)
                            .border(1.dp, CaixaDS.colors.teal),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            value.getOrNull(index)?.toString().orEmpty(),
                            color = CaixaDS.colors.cartDark,
                            fontSize = 40.sp,
                            style = legacyPaymentTextStyle,
                            modifier = Modifier.uiId("pay.0009")
                        )
                    }
                }
            }
        }
    )
}

@Composable
internal fun CreditCardFront(
    brand: String = "VISA",
    number: String = "•••• •••• •••• 4589",
    holderName: String = stringResource(
        LocalizedR.string.format_card_holder_placeholder
    ),
    expiration: String = "12/2029",
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Card(
        // fragment_cartao_credito.xml: corpo de 200 dp mais margens externas de 32 dp.
        Modifier.fillMaxWidth().height(264.dp).padding(32.dp),
        shape = RoundedCornerShape(25.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = CaixaDS.colors.creditCard)
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier.fillMaxSize().padding(start = 190.dp)
                    .background(CaixaDSColorTokens.White, RoundedCornerShape(topStart = 100.dp, bottomStart = 100.dp))
            )
            CaixaDSPaymentBrandMark(
                brand,
                (Modifier.align(Alignment.TopStart).padding(start = 20.dp).offset(y = (-5).dp).size(80.dp))
                    .uiId("pay.0010")
            )
            Column(Modifier.align(Alignment.TopStart).padding(start = 20.dp, top = 81.dp)) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_numero_67e494),
                    color = CaixaDSColorTokens.Black,
                    fontSize = 12.sp,
                    style = legacyPaymentTextStyle,
                    modifier = Modifier.uiId("pay.0011")
                )
                Text(
                    number,
                    (Modifier.padding(top = 6.dp)).uiId("pay.0012"),
                    color = CaixaDSColorTokens.Black,
                    fontSize = 12.sp,
                    style = legacyPaymentTextStyle
                )
            }
            Column(Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 10.dp)) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_nome_508690),
                    (Modifier.padding(bottom = 6.dp)).uiId("pay.0013"),
                    color = CaixaDSColorTokens.Black,
                    fontSize = 12.sp,
                    style = legacyPaymentTextStyle
                )
                Text(
                    holderName,
                    color = CaixaDSColorTokens.Black,
                    fontSize = 12.sp,
                    style = legacyPaymentTextStyle,
                    modifier = Modifier
                        .uiId("pay.0014")
                )
            }
            Column(Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 10.dp), horizontalAlignment = Alignment.End) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_validade_5f7aec),
                    (Modifier.padding(end = 4.dp, bottom = 12.dp)).uiId("pay.0015"),
                    color = CaixaDSColorTokens.Black,
                    fontSize = 12.sp,
                    style = legacyPaymentTextStyle
                )
                Text(
                    expiration,
                    color = CaixaDSColorTokens.Black,
                    fontSize = 12.sp,
                    style = legacyPaymentTextStyle,
                    modifier = Modifier
                        .uiId("pay.0016")
                )
            }
        }
    }
}

@Composable
internal fun CreditCardBack(showMagnifier: Boolean, brand: String = "VISA") {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.fillMaxWidth().height(264.dp)) {
        Card(
            Modifier.fillMaxSize().padding(32.dp),
            shape = RoundedCornerShape(25.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = CaixaDS.colors.creditCard)
        ) {
            Box(Modifier.fillMaxSize()) {
                Box(
                    Modifier.fillMaxSize().padding(start = 190.dp)
                        .background(CaixaDSColorTokens.White, RoundedCornerShape(topStart = 100.dp, bottomStart = 100.dp))
                )
                Box(
                    Modifier.fillMaxWidth().padding(top = 24.dp).height(40.dp)
                        .background(CaixaDS.colors.magneticStripe)
                )
                Box(
                    Modifier.fillMaxWidth().padding(top = 95.dp).height(48.dp)
                        .background(CaixaDS.colors.signatureStripe)
                )
                CaixaDSPaymentBrandMark(
                    brand,
                    (Modifier.align(Alignment.BottomStart).padding(start = 20.dp).width(80.dp).height(65.dp)).uiId("pay.0017")
                )
            }
        }
        if (showMagnifier) {
            Box(
                Modifier.align(Alignment.BottomEnd).padding(end = 18.dp, bottom = 56.dp).size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(R.drawable.legacy_card_magnifier), null, Modifier.fillMaxSize())
                Text(
                    uiStrings.getString(LocalizedR.string.ui_xxx_9b38b8),
                    (Modifier.offset(x = (-4).dp, y = (-5).dp)).uiId("pay.0018"),
                    color = CaixaDSColorTokens.Black,
                    fontSize = 20.sp,
                    style = legacyPaymentTextStyle
                )
            }
        }
    }
}

@Composable
internal fun LegacySmallHeading(text: String) {
    Text(
        text,
        (Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 16.dp)).uiId("pay.0019"),
        color = CaixaDS.colors.cartDark,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        style = legacyPaymentTextStyle
    )
}
