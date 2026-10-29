package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

/** Cores e métricas nativas de TextView de activity_filtra_marketplace.xml e item_dezena.xml. */
object CaixaDSMarketplaceFilterStyle {
    val canvas = Color.White
    val label = Color.Black
    val hint = CaixaDSColorTokens.AccountTermsText
    val betText = CaixaDSColorTokens.SocialGraphite
    val accent = Color(0xFF005EA8)
    val heading = CaixaDSColorTokens.CaixaBlue
    val line = Color(0xFFD9D9D9)
    val dialogHeading get() = caixaDSMarketplaceFilterText(14).copy(lineHeight = 18.9.sp)
    val bottomInset = 5.dp
    val outlinedButtonCorner = 4.dp
    val filledButtonCorner = 5.dp
}

fun caixaDSMarketplaceFilterText(size: Int = 14, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSFontFamily,
    fontSize = size.sp,
    fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

@Composable
fun CaixaDSMarketplaceFilterLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CaixaDSMarketplaceFilterStyle.label,
    fontSize: Int = 12,
) {
    Text(text, modifier, color = color, style = caixaDSMarketplaceFilterText(fontSize, FontWeight.Bold))
}

@Composable
fun CaixaDSMarketplaceFilterInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    currency: Boolean = false,
    fontSize: Int = 14,
    accessibilityLabel: String = placeholder,
    errorMessage: String? = null,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var editValue by remember { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    val displayedValue = editValue.copy(
        text = value,
        selection = if (currency) TextRange(value.length) else editValue.selection
    )
    BasicTextField(
        value = displayedValue,
        onValueChange = { edited ->
            val input = edited.text
            val limited = if (keyboardType == KeyboardType.Decimal || keyboardType == KeyboardType.Number) input.take(14) else input
            val formatted = if (currency && limited.isNotEmpty()) {
                java.text.NumberFormat.getNumberInstance(java.util.Locale.forLanguageTag("pt-BR")).apply {
                    minimumFractionDigits = 2
                    maximumFractionDigits = 2
                }.format(br.gov.caixa.loterias.core.designsystem.components.input.caixaDSMoneyInputAmount(limited))
            } else {
                limited
            }
            editValue = edited.copy(
                text = formatted,
                selection = if (currency) TextRange(formatted.length) else edited.selection
            )
            // Mudanças de seleção/composição do teclado não alteram o texto nem devem reiniciar a busca.
            if (formatted != value) onValueChange(formatted)
        },
        modifier = (
            modifier.fillMaxWidth().height(48.dp)
                .border(1.dp, CaixaDSMarketplaceFilterStyle.label, RoundedCornerShape(5.dp))
                .semantics {
                    contentDescription = accessibilityLabel
                    if (!errorMessage.isNullOrBlank()) error(errorMessage)
                }
            ).uiId("ds.0029"),
        textStyle = caixaDSMarketplaceFilterText(fontSize).copy(color = CaixaDSMarketplaceFilterStyle.hint),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        decorationBox = { inner ->
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                if (currency) {
                    Icon(
                        CaixaDSMarketplaceFilterCurrencyIcon,
                        uiStrings.getString(LocalizedR.string.ui_r_0d69df),
                        Modifier.width(44.dp).height(49.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(Modifier.width(5.dp))
                }
                Box(Modifier.weight(1f).padding(start = if (currency) 0.dp else 8.dp, end = 8.dp)) {
                    if (value.isEmpty()) {
                        Text(
                            placeholder,
                            color = CaixaDSMarketplaceFilterStyle.hint,
                            style = caixaDSMarketplaceFilterText(fontSize)
                        )
                    }
                    inner()
                }
            }
        }
    )
}

/** O GridLayoutManager nativo distribui seis colunas, mas mantém cada filho de 49dp no início da sua coluna. */
@Composable
fun CaixaDSMarketplaceFilterBetGrid(options: List<Int>, selected: Set<Int>, modifier: Modifier = Modifier, onClick: (Int) -> Unit) {
    val fontScale = LocalDensity.current.fontScale
    if (fontScale > 1f) {
        val cellWidth = 45.dp * fontScale + 4.dp
        FlowRow(modifier.fillMaxWidth()) {
            options.forEach { value ->
                Box(Modifier.width(cellWidth).padding(2.dp)) {
                    CaixaDSMarketplaceFilterNumberCell(value, value in selected, 45.dp, 45.dp) { onClick(value) }
                }
            }
        }
        return
    }
    val rows = ((options.size + 5) / 6).coerceAtLeast(1)
    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        modifier = modifier.fillMaxWidth().height((rows * 53).dp),
        userScrollEnabled = false
    ) {
        items(options) { value ->
            Box(Modifier.fillMaxWidth().height(53.dp), contentAlignment = Alignment.TopStart) {
                Box(Modifier.width(49.dp).height(53.dp).padding(horizontal = 2.dp, vertical = 4.dp)) {
                    CaixaDSMarketplaceFilterNumberCell(value, value in selected, 45.dp, 45.dp) { onClick(value) }
                }
            }
        }
    }
}

/** O mesmo vetor de 41 unidades é escalado pelo ToggleButton nativo para apostas de 45dp e diálogos de 29dp. */
@Composable
fun CaixaDSMarketplaceFilterNumberCell(value: Int, selected: Boolean, diameter: Dp = 29.dp, rowHeight: Dp = 45.dp, onClick: () -> Unit) {
    val fontScale = LocalDensity.current.fontScale
    val effectiveDiameter = if (fontScale > 1f) diameter * fontScale else diameter
    val effectiveRowHeight = if (fontScale > 1f) maxOf(rowHeight, effectiveDiameter + 8.dp) else rowHeight
    Box(Modifier.fillMaxWidth().height(effectiveRowHeight), contentAlignment = Alignment.Center) {
        Box(
            Modifier.size(effectiveDiameter).uiId("ds.0030").toggleable(value = selected, role = Role.Checkbox, onValueChange = {
                onClick()
            }),
            contentAlignment = Alignment.Center
        ) {
            MarketplaceFilterNumberBall(value, selected)
        }
    }
}

/** O rótulo fica 10dp abaixo do divisor; o sinal de mais nativo de 22dp ocupa sua própria área à direita. */
@Composable
fun CaixaDSMarketplaceFilterAccordionHeader(
    title: String,
    testTag: String,
    onOpen: () -> Unit,
    kind: CaixaDSMarketplaceFilterAccordionKind = CaixaDSMarketplaceFilterAccordionKind.QUANTITY,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val largeFont = LocalDensity.current.fontScale > 1f
    Box(
        Modifier.fillMaxWidth().then(if (largeFont) Modifier.heightIn(min = 48.dp) else Modifier.height(27.dp))
            .legacyAutomationId(testTag).uiId("ds.0031").clickable(role = Role.Button, onClick = onOpen)
    ) {
        CaixaDSMarketplaceFilterLabel(
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
                    CaixaDSMarketplaceFilterAccordionKind.QUANTITY
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

enum class CaixaDSMarketplaceFilterAccordionKind { QUANTITY, NUMBERS }

/** btn_round_outlined / btn_rounded_blue têm cantos de 4dp / 5dp, inclusive nas ações de diálogos. */
@Composable
fun CaixaDSMarketplaceFilterButton(
    text: String,
    outlined: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 40.dp,
    textSize: Int = 14,
    fontWeight: FontWeight = FontWeight.Bold,
    contentColor: Color = if (outlined) CaixaDSMarketplaceFilterStyle.accent else Color.White,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    val largeFont = LocalDensity.current.fontScale > 1f
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = (modifier.then(if (largeFont) Modifier.heightIn(min = height) else Modifier.height(height))).uiId("ds.0033"),
        shape = RoundedCornerShape(
            if (outlined) CaixaDSMarketplaceFilterStyle.outlinedButtonCorner else CaixaDSMarketplaceFilterStyle.filledButtonCorner
        ),
        border = if (outlined) BorderStroke(1.dp, CaixaDSMarketplaceFilterStyle.accent) else null,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (outlined) CaixaDSMarketplaceFilterStyle.canvas else CaixaDSMarketplaceFilterStyle.accent,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text,
            modifier = if (largeFont) Modifier.fillMaxWidth() else Modifier,
            style = caixaDSMarketplaceFilterText(
                textSize,
                fontWeight
            ).copy(lineHeight = lineHeight, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        )
    }
}

/** item_dezena_quero_nao_quero e seu contêiner nativo de seleção com recuo de 20dp. */
@Composable
fun CaixaDSMarketplaceFilterSelection(values: List<Int>, clearLabel: String, onRemove: (Int) -> Unit, onClear: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val rows = ((values.size + 5) / 6).coerceAtLeast(1)
    Column(Modifier.fillMaxWidth().padding(top = 10.dp, end = 20.dp)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.fillMaxWidth().height((rows * 45).dp),
            userScrollEnabled = false
        ) {
            items(values) { value ->
                Box(Modifier.fillMaxWidth().height(45.dp), contentAlignment = Alignment.Center) {
                    Box(
                        Modifier.size(29.dp).uiId("ds.0034").clickable(role = Role.Button) { onRemove(value) }.semantics {
                            contentDescription =
                                uiStrings.getString(LocalizedR.string.ui_remover_1_s_f2ff9f, value.toString().padStart(2, '0'))
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        MarketplaceFilterNumberBall(value, selected = true)
                        Image(
                            painterResource(R.drawable.legacy_marketplace_filter_remove),
                            null,
                            Modifier.align(Alignment.TopEnd).offset(x = 7.dp, y = (-4).dp).size(16.dp)
                        )
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
            CaixaDSMarketplaceFilterButton(
                text = clearLabel.uppercase(java.util.Locale.forLanguageTag("pt-BR")),
                outlined = true,
                onClick = onClear,
                modifier = (Modifier.width(127.dp)).uiId("ds.0035"),
                height = 34.dp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/** Desenha apenas o visual compartilhado; cada chamador mantém seu tamanho, tratamento de cliques e sobreposições. */
@Composable
private fun MarketplaceFilterNumberBall(value: Int, selected: Boolean) {
    Image(
        painterResource(if (selected) R.drawable.legacy_marketplace_filter_bet_selected else R.drawable.legacy_marketplace_filter_bet),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
    Text(
        value.toString().padStart(2, '0'),
        modifier = if (LocalDensity.current.fontScale > 1f) Modifier.fillMaxWidth() else Modifier,
        textAlign = if (LocalDensity.current.fontScale > 1f) TextAlign.Center else null,
        color = if (selected) Color.White else CaixaDSMarketplaceFilterStyle.betText,
        style = caixaDSMarketplaceFilterText(14, FontWeight.Bold)
    )
}
