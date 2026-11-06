package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
internal fun HtmlDialogMessage(title: String, body: AnnotatedString, focusRequester: FocusRequester) {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth()
            .focusRequester(focusRequester)
            .focusable()
            .semantics { heading() },
        color = LoteriasCaixaDSColorTokens.ComboDark,
        style = LoteriasCaixaDS.typography.bodyLarge.copy(
            fontFamily = LoteriasCaixaDSRegularFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = TextStyle.Default.lineHeight,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    )
    Box(
        Modifier.fillMaxWidth().padding(top = 17.dp).height(1.dp)
            .background(LoteriasCaixaDSColorTokens.TutorialDivider)
    )
    Text(
        text = body,
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        color = LoteriasCaixaDSColorTokens.SocialGraphite,
        style = LoteriasCaixaDS.typography.bodyLarge.copy(
            fontFamily = LoteriasCaixaDSRegularFontFamily,
            fontSize = 16.sp,
            lineHeight = TextStyle.Default.lineHeight,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    )
}

@Composable
internal fun HtmlDialogActions(positiveLabel: String, onPositive: () -> Unit, negativeLabel: String?, onNegative: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        negativeLabel?.let { label ->
            TextButton(
                onClick = onNegative,
                modifier = (Modifier.padding(end = 8.dp)).uiId("ds.0077")
            ) {
                Text(
                    label,
                    color = LoteriasCaixaDSColorTokens.SocialGraphite,
                    style = LoteriasCaixaDS.typography.labelLarge.copy(
                        fontFamily = LoteriasCaixaDSRegularFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
        TextButton(onClick = onPositive, modifier = Modifier.uiId("ds.0078")) {
            Text(
                positiveLabel,
                color = LoteriasCaixaDSColorTokens.ComboDark,
                style = LoteriasCaixaDS.typography.labelLarge.copy(
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
