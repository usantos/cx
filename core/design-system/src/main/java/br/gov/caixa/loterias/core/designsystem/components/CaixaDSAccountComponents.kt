package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

@Composable
fun CaixaDSAccountField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    showValidation: Boolean = false,
    validationEmphasized: Boolean = false,
    emptyMask: String = "",
    error: Boolean = false,
    currencyInput: Boolean = false,
) {
    CaixaDSTextField(
        value = value, onValueChange = onValueChange, label = label, modifier = modifier.uiId("ds.0001"),
        enabled = enabled, keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = error, placeholder = emptyMask, showValidation = showValidation,
        currencyInput = currencyInput, variant = CaixaDSTextFieldVariant.LEGACY
    )
}

/** O txt_valida_cep instalado fica diretamente abaixo do campo: 14sp, aviso de 20dp e intervalo de 8dp. */
@Composable
fun CaixaDSAccountInlineWarning(message: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(R.drawable.legacy_account_warning), null, Modifier.size(20.dp), tint = CaixaDSColorTokens.AccountError)
        Spacer(Modifier.width(8.dp))
        Text(message, fontSize = 14.sp, color = CaixaDSColorTokens.AccountError, style = CaixaDSAccountTextStyle)
    }
}

@Composable
fun <T> CaixaDSAccountChoice(selected: T, options: List<Pair<String, T>>, onSelected: (T) -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(
            58.dp * LocalDensity.current.fontScale.coerceAtLeast(1f)
        ).selectableGroup().background(CaixaDSColorTokens.White, RoundedCornerShape(18.dp))
            .border(1.dp, CaixaDSColorTokens.CardOutline, RoundedCornerShape(18.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { index, (label, gender) ->
            if (index == 1) Box(Modifier.height(42.dp).size(width = 1.dp, height = 42.dp).background(CaixaDSColorTokens.GenderDivider))
            Row(
                Modifier.weight(1f).fillMaxSize()
                    .uiId("ds.0002").selectable(selected = selected == gender, role = Role.RadioButton) { onSelected(gender) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                // O LinearLayout interno nativo envolve filhos de 20dp mais a margem de 16dp.
                // Seu primeiro filho fica centralizado com topMargin9; o segundo usa topMargin16.
                Row(
                    Modifier.height(20.dp * LocalDensity.current.fontScale.coerceAtLeast(1f)).offset(
                        y = if (index ==
                            0
                        ) {
                            (-2).dp
                        } else {
                            (-3).dp
                        }
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AccountGenderLabel(label)
                    Icon(
                        painterResource(
                            if (selected ==
                                gender
                            ) {
                                R.drawable.legacy_account_ic_check_circle
                            } else {
                                R.drawable.legacy_account_ic_circle_outline
                            }
                        ),
                        null,
                        Modifier.padding(start = 8.dp).size(20.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}

/** SegmentedButton centraliza os limites desenhados do texto, em vez de sua largura de avanço. */
@Composable
private fun AccountGenderLabel(label: String) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val textSizePx = with(density) { 14.sp.toPx() }
    val boundsWidth = remember(label, context, textSizePx) {
        val paint = android.graphics.Paint().apply {
            typeface = ResourcesCompat.getFont(context, br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_bold)
            textSize = textSizePx
        }
        val bounds = android.graphics.Rect()
        paint.getTextBounds(label, 0, label.length, bounds)
        bounds.width()
    }
    Box(Modifier.width(with(density) { boundsWidth.toDp() })) {
        Text(
            label,
            Modifier.wrapContentWidth(Alignment.Start, unbounded = true),
            color = CaixaDSColorTokens.BetAction,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            style = CaixaDSAccountTextStyle.copy(platformStyle = PlatformTextStyle(includeFontPadding = false))
        )
    }
}

val CaixaDSAccountTextStyle = TextStyle(fontFamily = CaixaDSFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true))
val CaixaDSAccountBackground = Brush.verticalGradient(
    listOf(CaixaDSColorTokens.White, CaixaDSColorTokens.AccountGradientMiddle, CaixaDSColorTokens.AccountGradientEnd)
)

@Composable
fun CaixaDSAccountSaveFooter(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean,
    enabledColor: Color = CaixaDSColorTokens.ComboDark,
    disabledColor: Color = CaixaDSColorTokens.ComboDark,
) {
    Box(Modifier.fillMaxWidth().height(90.dp).background(CaixaDSColorTokens.White)) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = (Modifier.fillMaxWidth().padding(start = 32.dp, top = 16.dp, end = 32.dp).height(48.dp)).uiId("ds.0003"),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = enabledColor,
                contentColor = CaixaDSColorTokens.White,
                disabledContainerColor = disabledColor,
                disabledContentColor = CaixaDSColorTokens.White
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
        ) {
            Text(label, fontSize = 16.sp, fontWeight = FontWeight.Bold, style = CaixaDSAccountTextStyle)
        }
    }
}
