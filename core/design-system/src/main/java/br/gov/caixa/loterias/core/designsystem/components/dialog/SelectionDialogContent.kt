package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

internal val nativeDialogTextStyle = TextStyle(
    fontFamily = LoteriasCaixaDSFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

@Composable
internal fun SelectionDialogTitle(title: String) {
    if (title.isNotBlank()) {
        Text(
            title,
            modifier = Modifier.semantics { heading() },
            color = LoteriasCaixaDS.colors.brand,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            style = nativeDialogTextStyle
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(top = 17.dp),
            thickness = 1.dp,
            color = LoteriasCaixaDSColorTokens.TutorialDivider
        )
    }
}

@Composable
internal fun SelectionDialogAction(
    label: String,
    onClick: () -> Unit,
    emphasized: Boolean,
    modifier: Modifier = Modifier,
    visitorPrompt: Boolean = false,
) {
    // Activities privadas AppCompat usam 54dp/64dp; Theme.SimulaMaterial usa 48dp/88dp.
    Box(
        modifier.widthIn(min = if (visitorPrompt) 64.dp else 88.dp)
            .heightIn(min = if (visitorPrompt) 54.dp else 48.dp).uiId("ds.0094").clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = if (emphasized) LoteriasCaixaDS.colors.brand else LoteriasCaixaDSColorTokens.SocialGraphite,
            style = nativeDialogTextStyle.copy(fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal),
            textAlign = TextAlign.Center
        )
    }
}
