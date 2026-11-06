package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentProvider
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

/** Seção visual do processador; a feature fornece conteúdo, ordem e operações dos cartões. */
@Composable
fun LoteriasCaixaDSSavedCardSection(
    provider: LoteriasCaixaDSPaymentProvider,
    intro: String,
    logo: @Composable (Modifier) -> Unit,
    cards: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    val expanded = LocalDensity.current.fontScale > 1f
    val accent = if (provider ==
        LoteriasCaixaDSPaymentProvider.RECARGA_PAY
    ) {
        LoteriasCaixaDSColorTokens.RecargaAccent
    } else {
        LoteriasCaixaDSColorTokens.MercadoPagoAccent
    }
    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().height(57.dp).border(1.dp, LoteriasCaixaDSColorTokens.CardOutline),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.width(8.dp).fillMaxHeight().background(accent))
            logo(Modifier.padding(start = 42.dp).uiId("pay.0126"))
        }
        Box(
            Modifier.fillMaxWidth().then(
                if (expanded) Modifier.heightIn(min = 270.dp) else Modifier.height(270.dp)
            ).background(accent.copy(alpha = SAVED_SECTION_ALPHA))
        ) {
            Box(Modifier.matchParentSize()) { Box(Modifier.width(8.dp).fillMaxHeight().background(accent)) }
            Column(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text(
                    intro,
                    Modifier.fillMaxWidth().padding(start = 24.dp, end = 16.dp).uiId("pay.0127"),
                    color = LoteriasCaixaDSColorTokens.NotificationIntro,
                    fontSize = 14.sp,
                    style = LoteriasCaixaDSCardTextStyle
                )
                cards(Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp).wrapContentHeight(Alignment.Top, unbounded = true))
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

/** Cartão300×165dp do legado; fonte ampliada pode aumentar a altura sem cortar os dados. */
@Composable
fun LoteriasCaixaDSSavedCreditCard(
    number: String,
    expiration: String,
    brand: Painter,
    brandDescription: String,
    deleteIcon: Painter,
    deleteDescription: String,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val expanded = LocalDensity.current.fontScale > 1f
    Box(
        modifier.padding(30.dp).width(300.dp).then(if (expanded) Modifier.heightIn(min = 165.dp) else Modifier.height(165.dp))
            .background(
                Brush.verticalGradient(
                    listOf(LoteriasCaixaDSColorTokens.SavedCardGradientStart, LoteriasCaixaDSColorTokens.SavedCardGradientEnd)
                ),
                RoundedCornerShape(10.dp)
            )
            .uiId("pay.0129").clickable(onClick = onOpen)
    ) {
        Box(
            Modifier.align(Alignment.TopEnd).size(40.dp).uiId("pay.0130").clickable(onClick = onDelete),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                deleteIcon,
                deleteDescription,
                Modifier.width(23.dp).height(22.dp).uiId("pay.0131"),
                tint = LoteriasCaixaDSColorTokens.White
            )
        }
        Image(
            brand,
            brandDescription,
            Modifier.align(Alignment.BottomEnd).padding(end = 17.dp, bottom = 17.dp)
                .width(70.dp).height(56.dp).uiId("pay.0132")
        )
        Column(Modifier.fillMaxWidth().padding(start = 17.dp, top = 92.dp, end = 87.dp, bottom = if (expanded) 17.dp else 0.dp)) {
            Text(
                number,
                Modifier.uiId("pay.0133"),
                color = LoteriasCaixaDSColorTokens.White,
                fontSize = 16.sp,
                style = LoteriasCaixaDSCardTextStyle
            )
            Text(
                expiration,
                Modifier.uiId("pay.0134"),
                color = LoteriasCaixaDSColorTokens.White,
                fontSize = 13.sp,
                style = LoteriasCaixaDSCardTextStyle
            )
        }
    }
}

private const val SAVED_SECTION_ALPHA = 20f / 255f
