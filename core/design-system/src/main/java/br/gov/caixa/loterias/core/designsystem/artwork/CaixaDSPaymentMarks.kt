package br.gov.caixa.loterias.core.designsystem.artwork

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
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS

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
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    when {
        provider == CaixaDSPaymentProvider.MERCADO_PAGO -> Image(
            imageVector = CaixaDSPaymentVectors.MercadoPago,
            contentDescription = uiStrings.getString(LocalizedR.string.ui_mercado_pago_fe6402),
            modifier = modifier,
            contentScale = ContentScale.Fit
        )

        recargaPayResourceId != 0 -> Image(
            painter = painterResource(recargaPayResourceId),
            contentDescription = uiStrings.getString(LocalizedR.string.ui_recarga_pay_ba884a),
            modifier = modifier,
            contentScale = ContentScale.Fit
        )

        else -> Box(modifier, contentAlignment = Alignment.Center) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_recargapay_5092d1),
                color = Color(0xFF5B6670),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/** Bandeira oficial normalizada a partir do nome devolvido pelos processadores. */
@Composable
fun CaixaDSPaymentBrandMark(brand: String, modifier: Modifier = Modifier) {
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
            contentScale = ContentScale.Fit
        )
    } else {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text(brand.uppercase(), color = CaixaDS.colors.cartDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/** Marca completa usada pelo card Pix de forma de pagamento. */
@Composable
fun CaixaDSPixBrand(modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Image(
        imageVector = CaixaDSPaymentVectors.Pix,
        contentDescription = uiStrings.getString(LocalizedR.string.ui_pix_fa7d3d),
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

/** Temporizador original do fluxo Pix copia e cola. */
@Composable
fun CaixaDSPixTimerIcon(modifier: Modifier = Modifier, color: Color = CaixaDS.colors.cartDark) {
    Icon(
        imageVector = CaixaDSPaymentVectors.Timer,
        contentDescription = null,
        modifier = modifier,
        tint = color
    )
}

@Composable
fun CaixaDSPixCopyIcon(copied: Boolean, modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Image(
        imageVector = if (copied) CaixaDSPaymentVectors.PixCopied else CaixaDSPaymentVectors.Copy,
        contentDescription = if (copied) {
            uiStrings.getString(LocalizedR.string.ui_pix_copiado_dffc13)
        } else {
            uiStrings.getString(
                LocalizedR.string.ui_copiar_codigo_pix_55c205
            )
        },
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
