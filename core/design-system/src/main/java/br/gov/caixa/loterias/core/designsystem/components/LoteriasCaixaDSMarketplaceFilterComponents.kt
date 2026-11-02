package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId

/** O rótulo fica 10dp abaixo do divisor; o sinal de mais nativo de 22dp ocupa sua própria área à direita. */
@Composable
fun LoteriasCaixaDSMarketplaceFilterAccordionHeader(
    title: String,
    testTag: String,
    onOpen: () -> Unit,
    kind: LoteriasCaixaDSMarketplaceFilterAccordionKind = LoteriasCaixaDSMarketplaceFilterAccordionKind.QUANTITY,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val largeFont = LocalDensity.current.fontScale > 1f
    Box(
        Modifier.fillMaxWidth().then(if (largeFont) Modifier.heightIn(min = 48.dp) else Modifier.height(27.dp))
            .legacyAutomationId(testTag).uiId("ds.0031").clickable(role = Role.Button, onClick = onOpen)
    ) {
        LoteriasCaixaDSMarketplaceFilterLabel(
            title,
            (
                Modifier.then(if (largeFont) Modifier.fillMaxWidth().padding(bottom = 10.dp) else Modifier)
                    .padding(start = 10.dp, top = 10.dp, end = 48.dp)
                ).uiId("ds.0032")
        )
        Image(
            painterResource(R.drawable.legacy_marketplace_filter_add),
            contentDescription = uiStrings.getString(LocalizedR.string.ui_adicionar_1_s_183174, title),
            modifier = Modifier.align(Alignment.TopEnd).offset(
                x = (-20).dp,
                y = if (kind ==
                    LoteriasCaixaDSMarketplaceFilterAccordionKind.QUANTITY
                ) {
                    10.dp
                } else {
                    8.dp
                }
            ).size(22.dp),
            contentScale = ContentScale.FillBounds
        )
    }
}

enum class LoteriasCaixaDSMarketplaceFilterAccordionKind { QUANTITY, NUMBERS }

/** btn_round_outlined / btn_rounded_blue têm cantos de 4dp / 5dp, inclusive nas ações de diálogos. */
@Composable
fun LoteriasCaixaDSMarketplaceFilterButton(
    text: String,
    outlined: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 40.dp,
    textSize: Int = 14,
    fontWeight: FontWeight = FontWeight.Bold,
    contentColor: Color = if (outlined) LoteriasCaixaDSMarketplaceFilterStyle.accent else Color.White,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    val largeFont = LocalDensity.current.fontScale > 1f
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = (modifier.then(if (largeFont) Modifier.heightIn(min = height) else Modifier.height(height))).uiId("ds.0033"),
        shape = RoundedCornerShape(
            if (outlined) {
                LoteriasCaixaDSMarketplaceFilterStyle.outlinedButtonCorner
            } else {
                LoteriasCaixaDSMarketplaceFilterStyle.filledButtonCorner
            }
        ),
        border = if (outlined) BorderStroke(1.dp, LoteriasCaixaDSMarketplaceFilterStyle.accent) else null,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (outlined) LoteriasCaixaDSMarketplaceFilterStyle.canvas else LoteriasCaixaDSMarketplaceFilterStyle.accent,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text,
            modifier = if (largeFont) Modifier.fillMaxWidth() else Modifier,
            style = loteriasCaixaDSMarketplaceFilterText(
                textSize,
                fontWeight
            ).copy(lineHeight = lineHeight, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        )
    }
}
