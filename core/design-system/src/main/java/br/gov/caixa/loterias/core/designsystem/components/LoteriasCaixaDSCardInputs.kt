package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSBasicTextField
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

/** Botão verde do legado: mínimo de50dp, texto centralizado e altura natural para acessibilidade. */
@Composable
fun LoteriasCaixaDSCardActionButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick,
        modifier.heightIn(min = 50.dp).uiId("pay.0027"),
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LoteriasCaixaDSColorTokens.TealAction,
            contentColor = LoteriasCaixaDSColorTokens.White
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
    ) {
        Text(
            label,
            Modifier.fillMaxWidth().uiId("pay.0028"),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = LoteriasCaixaDSCardTextStyle
        )
    }
}

/** Mantém células de40×60dp em fonte normal e adapta as três/quatro posições ao espaço disponível. */
@Composable
fun LoteriasCaixaDSCardSecurityCode(value: String, length: Int, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val fontScale = LocalDensity.current.fontScale.coerceAtLeast(1f)
    BoxWithConstraints(modifier.fillMaxWidth().padding(top = 8.dp)) {
        val cellWidth = minOf(40.dp * fontScale, (maxWidth - 15.dp * (length - 1)) / length)
        LoteriasCaixaDSBasicTextField(
            value,
            onValueChange,
            Modifier.fillMaxWidth().uiId("pay.0008"),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = LoteriasCaixaDS.typography.bodyLarge.copy(color = LoteriasCaixaDSColorTokens.Transparent),
            cursorBrush = SolidColor(LoteriasCaixaDSColorTokens.Transparent),
            decorationBox = { inner ->
                Box(Modifier.fillMaxWidth()) {
                    Box(Modifier.matchParentSize()) { inner() }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally)) {
                        repeat(length) { index ->
                            Box(
                                Modifier.width(cellWidth).height(60.dp * fontScale).border(1.dp, LoteriasCaixaDS.colors.teal),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    value.getOrNull(index)?.toString().orEmpty(),
                                    Modifier.fillMaxWidth().uiId("pay.0009"),
                                    color = LoteriasCaixaDS.colors.cartDark,
                                    fontSize = 40.sp,
                                    lineHeight = 48.sp,
                                    textAlign = TextAlign.Center,
                                    style = LoteriasCaixaDSCardTextStyle
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}
