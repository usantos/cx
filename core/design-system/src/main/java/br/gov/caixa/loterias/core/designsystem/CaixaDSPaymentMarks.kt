package br.gov.caixa.loterias.core.designsystem

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

enum class CaixaDSPaymentProvider { MERCADO_PAGO, RECARGA_PAY }

/**
 * Logos dos processadores usados pelo fluxo legado.
 *
 * Mercado Pago é vetorial e pertence ao CaixaDS. O logo histórico do RecargaPay é um PNG do
 * produto e, por isso, seu resource id é injetado pelo módulo app em vez de criar dependência
 * reversa entre o Design System e a aplicação.
 */
@Composable
fun CaixaDSPaymentProviderMark(
    provider: CaixaDSPaymentProvider,
    modifier: Modifier = Modifier,
    @DrawableRes recargaPayResourceId: Int = 0,
) {
    when {
        provider == CaixaDSPaymentProvider.MERCADO_PAGO -> Image(
            imageVector = CaixaDSPaymentVectors.MercadoPago,
            contentDescription = "Mercado Pago",
            modifier = modifier,
            contentScale = ContentScale.Fit,
        )
        recargaPayResourceId != 0 -> Image(
            painter = painterResource(recargaPayResourceId),
            contentDescription = "Recarga Pay",
            modifier = modifier,
            contentScale = ContentScale.Fit,
        )
        else -> Box(modifier, contentAlignment = Alignment.Center) {
            Text("recargapay", color = Color(0xFF5B6670), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/** Bandeira oficial normalizada a partir do nome devolvido pelos processadores. */
@Composable
fun CaixaDSPaymentBrandMark(
    brand: String,
    modifier: Modifier = Modifier,
) {
    val normalized = brand.filter(Char::isLetterOrDigit).uppercase()
    val vector: ImageVector? = when (normalized) {
        "AMEX", "AMERICANEXPRESS" -> CaixaDSPaymentVectors.AmericanExpress
        "ELO" -> CaixaDSPaymentVectors.Elo
        "HIPER", "HIPERCARD" -> CaixaDSPaymentVectors.Hipercard
        "MASTER", "MASTERCARD" -> CaixaDSPaymentVectors.Mastercard
        "VISA" -> CaixaDSPaymentVectors.Visa
        else -> null
    }
    if (vector != null) {
        Image(
            imageVector = vector,
            contentDescription = brand,
            modifier = modifier,
            contentScale = ContentScale.Fit,
        )
    } else {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text(brand.uppercase(), color = CaixaDS.colors.legacyCartDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/** Marca completa usada pelo card Pix de forma de pagamento. */
@Composable
fun CaixaDSPixBrand(modifier: Modifier = Modifier) {
    Image(
        imageVector = CaixaDSPaymentVectors.Pix,
        contentDescription = "Pix",
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

/** Cronômetro original do Pix Copia e Cola. */
@Composable
fun CaixaDSPixTimerIcon(
    modifier: Modifier = Modifier,
    color: Color = CaixaDS.colors.legacyCartDark,
) {
    Icon(
        imageVector = CaixaDSPaymentVectors.Timer,
        contentDescription = null,
        modifier = modifier,
        tint = color,
    )
}

@Composable
fun CaixaDSPixCopyIcon(
    copied: Boolean,
    modifier: Modifier = Modifier,
) {
    Image(
        imageVector = if (copied) CaixaDSPaymentVectors.PixCopied else CaixaDSPaymentVectors.Copy,
        contentDescription = if (copied) "Pix copiado" else "Copiar código Pix",
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}
