package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily

/** Dados somente de apresentação; nenhum identificador de compra ou regra do processador pertence ao DS. */
data class LoteriasCaixaDSCreditCardContent(
    val number: String,
    val holderName: String,
    val expiration: String,
    val numberLabel: String,
    val holderLabel: String,
    val expirationLabel: String,
)

val LoteriasCaixaDSCardTextStyle =
    TextStyle(fontFamily = LoteriasCaixaDSFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true))

/** Mantém a frente de 200dp do legado e reorganiza os campos quando a fonte é ampliada. */
@Composable
fun LoteriasCaixaDSCreditCardFront(
    content: LoteriasCaixaDSCreditCardContent,
    brand: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    val expanded = LocalDensity.current.fontScale > 1f
    Card(
        modifier.fillMaxWidth().padding(32.dp).then(if (expanded) Modifier.heightIn(min = 200.dp) else Modifier.height(200.dp)),
        shape = RoundedCornerShape(25.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = LoteriasCaixaDS.colors.creditCard)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Box(
                Modifier.matchParentSize().padding(start = 190.dp)
                    .background(Color.White, RoundedCornerShape(topStart = 100.dp, bottomStart = 100.dp))
            )
            if (expanded) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CardFrontBrand(brand, Modifier.size(80.dp))
                    CardNumber(content)
                    CardHolder(content)
                    CardExpiration(content)
                }
            } else {
                Box(Modifier.fillMaxWidth().height(200.dp)) {
                    CardFrontBrand(brand, Modifier.align(Alignment.TopStart).padding(start = 20.dp).offset(y = (-5).dp).size(80.dp))
                    CardNumber(content, Modifier.align(Alignment.TopStart).padding(start = 20.dp, top = 81.dp))
                    CardHolder(content, Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 10.dp))
                    CardExpiration(content, Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 10.dp))
                }
            }
        }
    }
}

@Composable
private fun CardNumber(content: LoteriasCaixaDSCreditCardContent, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(content.numberLabel, Modifier.uiId("pay.0011"), Color.Black, fontSize = 12.sp, style = LoteriasCaixaDSCardTextStyle)
        Text(
            content.number,
            Modifier.padding(top = 6.dp).uiId("pay.0012"),
            Color.Black,
            fontSize = 12.sp,
            style = LoteriasCaixaDSCardTextStyle
        )
    }
}

@Composable
private fun CardHolder(content: LoteriasCaixaDSCreditCardContent, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            content.holderLabel,
            Modifier.padding(bottom = 6.dp).uiId("pay.0013"),
            Color.Black,
            fontSize = 12.sp,
            style = LoteriasCaixaDSCardTextStyle
        )
        Text(content.holderName, Modifier.uiId("pay.0014"), Color.Black, fontSize = 12.sp, style = LoteriasCaixaDSCardTextStyle)
    }
}

@Composable
private fun CardExpiration(content: LoteriasCaixaDSCreditCardContent, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.End) {
        Text(
            content.expirationLabel,
            Modifier.padding(end = 4.dp, bottom = 12.dp).uiId("pay.0015"),
            Color.Black,
            fontSize = 12.sp,
            style = LoteriasCaixaDSCardTextStyle
        )
        Text(content.expiration, Modifier.uiId("pay.0016"), Color.Black, fontSize = 12.sp, style = LoteriasCaixaDSCardTextStyle)
    }
}

/** O verso preserva a arte e as faixas do cartão; a lupa é fornecida como arte, sem lógica de compra. */
@Composable
fun LoteriasCaixaDSCreditCardBack(
    showMagnifier: Boolean,
    brand: @Composable (Modifier) -> Unit,
    magnifier: Painter,
    magnifierText: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth().height(264.dp)) {
        Card(
            Modifier.fillMaxSize().padding(32.dp),
            shape = RoundedCornerShape(25.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = LoteriasCaixaDS.colors.creditCard)
        ) {
            Box(Modifier.fillMaxSize()) {
                Box(
                    Modifier.fillMaxSize().padding(start = 190.dp)
                        .background(Color.White, RoundedCornerShape(topStart = 100.dp, bottomStart = 100.dp))
                )
                Box(Modifier.fillMaxWidth().padding(top = 24.dp).height(40.dp).background(LoteriasCaixaDS.colors.magneticStripe))
                Box(Modifier.fillMaxWidth().padding(top = 95.dp).height(48.dp).background(LoteriasCaixaDS.colors.signatureStripe))
                brand(Modifier.align(Alignment.BottomStart).padding(start = 20.dp).width(80.dp).height(65.dp).uiId("pay.0017"))
            }
        }
        if (showMagnifier) {
            Box(
                Modifier.align(Alignment.BottomEnd).padding(end = 18.dp, bottom = 56.dp).size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(magnifier, null, Modifier.fillMaxSize())
                Text(
                    magnifierText,
                    Modifier.offset(x = (-4).dp, y = (-5).dp).uiId("pay.0018"),
                    Color.Black,
                    fontSize = 20.sp,
                    style = LoteriasCaixaDSCardTextStyle
                )
            }
        }
    }
}

@Composable
private fun CardFrontBrand(brand: @Composable (Modifier) -> Unit, modifier: Modifier) = brand(modifier.uiId("pay.0010"))
