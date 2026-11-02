package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
internal fun OptionDialogHeader(
    title: String,
    description: String?,
    subtitle: String?,
    nativeTitleHeight: Int,
    focusRequester: FocusRequester,
) {
    val density = LocalDensity.current
    val boldFamily = remember { FontFamily(Font(R.font.caixa_std_bold)) }
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth()
            .heightIn(min = with(density) { nativeTitleHeight.toDp() })
            .focusRequester(focusRequester)
            .focusable()
            .semantics { heading() },
        color = LoteriasCaixaDSColorTokens.ComboDark,
        style = TextStyle(
            fontFamily = boldFamily,
            fontSize = 16.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    )
    Box(
        Modifier.fillMaxWidth().padding(top = 17.dp).height(1.dp)
            .background(LoteriasCaixaDSColorTokens.TutorialDivider)
    )
    description?.let {
        Text(
            text = it,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            color = LoteriasCaixaDSColorTokens.SocialGraphite,
            style = TextStyle(
                fontFamily = LoteriasCaixaDSRegularFontFamily,
                fontSize = 16.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            )
        )
    }
    subtitle?.let {
        Text(
            text = it,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            color = LoteriasCaixaDSColorTokens.SocialGraphite,
            style = TextStyle(
                fontFamily = LoteriasCaixaDSRegularFontFamily,
                fontSize = 14.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            )
        )
    }
    if (description == null && subtitle == null) {
        Spacer(Modifier.height(16.dp))
    }
}
