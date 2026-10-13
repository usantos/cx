package br.gov.caixa.loterias.feature.cart

import androidx.activity.compose.BackHandler

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.Lifecycle
import br.gov.caixa.loterias.core.designsystem.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartAdditionRoute(
    viewModel: CartAdditionViewModel,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onOpenCart: () -> Unit,
    onHelp: ((Int, String) -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refreshReservation() }
    if (state.kind == CartAdditionKind.POOL_SHARE) {
        BackHandler(onBack = onBack)
        LegacyPoolAddition(state, onBack, onHome, onOpenCart, onHelp)
    } else {
        BackHandler(onBack = onBack)
        LegacyBetAddition(state, onBack, onHome, onOpenCart, onHelp)
    }
}

@Composable
private fun LegacyPoolAddition(
    state: CartAdditionUiState,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onOpenCart: () -> Unit,
    onHelp: ((Int, String) -> Unit)?,
) {
    val style = caixaDSLotteryStyle(0, state.modality)
    Column(Modifier.fillMaxSize().background(CaixaDSColorTokens.White).statusBarsPadding()) {
        PoolHeader(
            modality = state.modality,
            contest = state.contest,
            drawDate = state.drawDate,
            color = style.light,
            contentColor = style.content,
            onBack = onBack,
            onHelp = cartHelpAction(state.modality, onHelp),
        )
        Box(Modifier.weight(1f).fillMaxWidth().background(style.light).zIndex(1f)) {
            Box(
                Modifier.align(Alignment.BottomCenter).width(292.dp).height(374.dp).padding(bottom = 56.dp),
            ) {
                PoolCartArrow(
                    color = style.content.copy(alpha = 0.75f),
                    modifier = Modifier.fillMaxSize().padding(start = 17.dp, end = 2.dp, top = 35.dp),
                )
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Bolão inserido no\ncarrinho com sucesso.",
                        color = style.content,
                        fontFamily = CaixaDSLegacyFuturaFontFamily,
                        fontSize = 14.2.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(28.dp))
                    Row(
                        Modifier
                            .background(style.dark, RoundedCornerShape(6.dp))
                            .border(1.dp, style.dark, RoundedCornerShape(6.dp))
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(CaixaDSIcons.Time, contentDescription = null, modifier = Modifier.size(24.dp), tint = style.content)
                        Text(
                            formatRemainingTime(state.remainingSeconds),
                            Modifier.padding(start = 8.dp),
                            color = style.content,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Text(
                        "Conclua o pagamento de suas\napostas dentro do prazo",
                        Modifier.padding(top = 16.dp),
                        color = style.content,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Box(
                        Modifier.padding(top = 36.dp).width(250.dp)
                            .background(style.dark, RoundedCornerShape(6.dp))
                            .border(1.dp, style.dark, RoundedCornerShape(6.dp))
                            .padding(horizontal = 4.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "Devido a regras de arredondamento,\no valor cobrado pela Cota deste bolão\npoderá ser menor que o exibido.",
                            color = style.content,
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            LegacyHomeButton(onClick = onHome, modifier = Modifier.align(Alignment.BottomCenter).offset(y = 43.dp))
        }
        Column(Modifier.background(CaixaDSColorTokens.White).navigationBarsPadding()) {
            PoolCartFooter(state.amount, onOpenCart)
        }
    }
}

@Composable
private fun PoolHeader(
    modality: String,
    contest: Int,
    drawDate: String,
    color: Color,
    contentColor: Color,
    onBack: () -> Unit,
    onHelp: (() -> Unit)?,
) {
    Row(Modifier.fillMaxWidth().height(48.dp).background(color), verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.width(24.dp))
        IconButton(
            onClick = onBack,
            modifier = Modifier.width(30.dp).height(48.dp).focusProperties { canFocus = false },
        ) {
            Icon(CaixaDSIcons.Back, contentDescription = "Voltar", modifier = Modifier.size(18.dp), tint = contentColor)
        }
        Text(
            modality.toDisplayModality().ifBlank { "bolão" },
            Modifier.weight(1f).padding(start = 24.dp, end = 24.dp),
            color = contentColor,
            fontFamily = CaixaDSLegacyFuturaFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        if (contest > 0) {
            HeaderValue("Concurso", contest.toString(), contentColor)
            HeaderValue("Sorteio", drawDateLabel(drawDate), contentColor, Modifier.padding(end = 8.dp))
        }
        if (onHelp != null) {
            IconButton(onClick = onHelp, modifier = Modifier.padding(end = 4.dp).size(32.dp)) {
                Icon(painterResource(br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_icon_duvida2), "Dúvidas", Modifier.size(32.dp), tint = contentColor)
            }
        }
    }
}

@Composable
private fun HeaderValue(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier.padding(start = 8.dp), horizontalAlignment = Alignment.Start) {
        Text(label, color = color, fontSize = 14.sp, lineHeight = 17.sp)
        Text(value, color = color, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PoolCartArrow(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val y = 18.dp.toPx()
        val right = size.width
        val bottom = size.height - 4.dp.toPx()
        drawPath(
            Path().apply {
                moveTo(0f, y)
                lineTo(right, y)
                lineTo(right, bottom - 8.dp.toPx())
            },
            color = color,
            style = Stroke(width = 0.65.dp.toPx(), cap = StrokeCap.Square),
        )
        drawPath(
            Path().apply {
                moveTo(right - 4.dp.toPx(), bottom - 8.dp.toPx())
                lineTo(right + 4.dp.toPx(), bottom - 8.dp.toPx())
                lineTo(right, bottom)
                close()
            },
            color = color,
        )
    }
}

@Composable
private fun PoolCartFooter(amount: BigDecimal, onOpenCart: () -> Unit) {
    val labelStyle = TextStyle(fontFamily = CaixaDSFontFamily, fontSize = 12.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true), textAlign = TextAlign.Center)
    Row(Modifier.fillMaxWidth().height(94.dp).background(CaixaDSColorTokens.LegacyBetFooter)) {
        Column(Modifier.weight(1f).fillMaxSize().padding(bottom = 13.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
            Text(formatCurrency(amount), Modifier.padding(bottom = 12.dp), color = CaixaDSColorTokens.LegacySecondaryText,
                style = labelStyle.copy(fontSize = if (amount > BigDecimal("999.99")) 9.8.sp else 14.sp, fontWeight = FontWeight.Bold))
            Text("Valor dessa\naposta", color = CaixaDSColorTokens.LegacyBetLink, style = labelStyle)
        }
        Spacer(Modifier.width(184.dp))
        Column(Modifier.weight(1f).fillMaxSize().clickable(onClick = onOpenCart).padding(top = 8.dp, bottom = 13.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            Image(painterResource(br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_icon_carrinho),
                contentDescription = null, modifier = Modifier.width(34.dp).height(40.dp))
            Text("Carrinho\nde apostas", color = CaixaDSColorTokens.LegacyBetLink, style = labelStyle)
        }
    }
}

@Composable
private fun LegacyHomeButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.width(184.dp).height(84.dp).background(CaixaDSColorTokens.LegacyBetAction).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text("Voltar ao início", fontFamily = CaixaDSLegacyFuturaFontFamily, color = CaixaDSColorTokens.White, fontSize = 17.7.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun LegacyBetAddition(
    state: CartAdditionUiState,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onOpenCart: () -> Unit,
    onHelp: ((Int, String) -> Unit)?,
) {
    val style = caixaDSLotteryStyle(0, state.modality)
    Column(Modifier.fillMaxSize().background(CaixaDSColorTokens.White).statusBarsPadding()) {
        PoolHeader(state.modality, state.contest, state.drawDate, style.light, style.content, onBack, cartHelpAction(state.modality, onHelp))
        Box(Modifier.weight(1f).fillMaxWidth().background(style.light).zIndex(1f)) {
            Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 11.dp)) {
                Text(
                    if (state.modality.equals("COMBO", true)) "Combo inserido no\ncarrinho com sucesso." else "Aposta inserida no\ncarrinho com sucesso.",
                    Modifier.fillMaxWidth(), color = style.content,
                    fontFamily = CaixaDSFontFamily,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    fontSize = 14.2.sp,
                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                )
                Box(Modifier.fillMaxWidth().height(318.dp)) {
                    Image(
                        painterResource(br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_cart_confirmation_arrow),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(start = 59.dp, end = 42.dp, top = 9.dp),
                        contentScale = ContentScale.Inside,
                        colorFilter = ColorFilter.tint(style.content),
                    )
                    if (state.minimumCartAmount.signum() > 0) {
                        Text(
                            "Lembre-se: o valor mínimo do total de compras nesse aplicativo é ${formatCurrency(state.minimumCartAmount)}",
                            Modifier.padding(start = 98.dp, end = 97.dp, top = 29.dp),
                            color = style.content, fontSize = 14.sp,
                            fontFamily = CaixaDSLegacyFuturaFontFamily, textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            LegacyHomeButton(onClick = onHome, modifier = Modifier.align(Alignment.BottomCenter).offset(y = 43.dp))
        }
        Column(Modifier.background(CaixaDSColorTokens.White).navigationBarsPadding()) {
            PoolCartFooter(state.amount, onOpenCart)
        }
    }
}

private fun formatCurrency(value: BigDecimal): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(value)

private fun String.toDisplayModality(): String = trim().lowercase()
    .split('_', '-', ' ')
    .filter(String::isNotBlank)
    .joinToString("-")

private fun cartHelpAction(modality: String, onHelp: ((Int, String) -> Unit)?): (() -> Unit)? {
    if (onHelp == null) return null
    val normalized = java.text.Normalizer.normalize(modality, java.text.Normalizer.Form.NFD)
        .replace(Regex("\\p{M}"), "").uppercase().replace('-', '_').replace(' ', '_')
    val id = when (normalized) {
        "MEGA_SENA", "MEGA_DA_VIRADA" -> 2
        "QUINA", "QUINA_DE_SAO_JOAO" -> 3
        "SUPER_SETE", "SUPER_7" -> 7
        "LOTOFACIL", "LOTOFACIL_DA_INDEPENDENCIA" -> 8
        "+MILIONARIA", "MAIS_MILIONARIA" -> 9
        "DIA_DE_SORTE" -> 11
        "LOTOGOL" -> 14
        "LOTOMANIA" -> 16
        "DUPLA_SENA", "DUPLA_DE_PASCOA" -> 18
        "LOTECA" -> 19
        "TIMEMANIA" -> 20
        else -> return null
    }
    return { onHelp(id, modality) }
}
