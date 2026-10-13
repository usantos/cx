package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object CaixaDSCartActionTokens {
    val Width = 240.dp
    val ContinueHeight = 56.dp
    val PaymentHeight = 70.dp
    val BetweenActions = 14.dp
    val BeforeSummary = 15.dp
    val OutlineRadius = 4.dp
    val PaymentRadius = 5.dp
    val Blue = Color(0xFF005EA8)
    val OutlineText = Color(0xFF005CA9)
}

/** Actions of activity_carrinho.xml, independent of cart validation or navigation. */
@Composable
fun CaixaDSCartActions(
    onContinueBetting: () -> Unit,
    onPayment: () -> Unit,
    modifier: Modifier = Modifier,
    paymentEnabled: Boolean = true,
) {
    val tokens = CaixaDSCartActionTokens
    val typography = TextStyle(fontFamily = CaixaDSFontFamily, fontSize = 16.sp, textAlign = TextAlign.Center,
        platformStyle = PlatformTextStyle(includeFontPadding = true))
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = onContinueBetting, modifier = Modifier.width(tokens.Width).height(tokens.ContinueHeight),
            shape = RoundedCornerShape(tokens.OutlineRadius), border = BorderStroke(1.dp, tokens.Blue),
            contentPadding = PaddingValues(horizontal = 8.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = tokens.OutlineText)) {
            Text("Continuar apostando", style = typography, fontWeight = FontWeight.Bold, maxLines = 1)
        }
        Spacer(Modifier.height(tokens.BetweenActions))
        Button(onClick = onPayment, enabled = paymentEnabled,
            modifier = Modifier.width(tokens.Width).height(tokens.PaymentHeight),
            shape = RoundedCornerShape(tokens.PaymentRadius), contentPadding = PaddingValues(horizontal = 8.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = tokens.Blue, contentColor = Color.White)) {
            Text(buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Avançar") }
                append("\npara a forma de pagamento")
            }, style = typography, maxLines = 2)
        }
        // 51 dp to the bottom in the original = this gap + the 36 dp summary slot.
        Spacer(Modifier.height(tokens.BeforeSummary))
    }
}
