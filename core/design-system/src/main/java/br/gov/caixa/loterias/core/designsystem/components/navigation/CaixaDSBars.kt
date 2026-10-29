package br.gov.caixa.loterias.core.designsystem.components.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

@Composable
fun CaixaDSTopBar(
    title: String,
    onBack: (() -> Unit)?,
    action: (@Composable () -> Unit)? = null,
    gradient: Boolean = false,
    startColor: Color? = null,
    endColor: Color? = null,
    contentColor: Color = CaixaDSColorTokens.White,
    actionWidth: Dp = 48.dp,
    navigationWidth: Dp = 72.dp,
    navigationContent: (@Composable () -> Unit)? = null,
    titleSize: androidx.compose.ui.unit.TextUnit = 16.sp,
    titleWeight: FontWeight = FontWeight.Bold,
    annotatedTitle: AnnotatedString? = null,
    titleContent: (@Composable () -> Unit)? = null,
    subtitle: String? = null,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    // Solicitações legadas de degradê resultam em uma cor sólida para cada barra de ferramentas e subtítulo.
    val headerColor = if (gradient) {
        endColor ?: CaixaDSColorTokens.Blue700
    } else {
        endColor ?: startColor ?: CaixaDSColorTokens.Blue700
    }
    CaixaDSStatusBarColor(CaixaDSColorTokens.Blue700, darkIcons = false)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CaixaDSColorTokens.Blue700)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(CaixaDS.sizes.topBar)
                .background(headerColor)
                .padding(horizontal = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.width(if (onBack == null) 16.dp else navigationWidth).height(48.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (navigationContent != null) {
                    navigationContent()
                } else {
                    onBack?.let { callback ->
                        IconButton(
                            onClick = callback,
                            modifier = (Modifier.padding(start = 4.dp).focusProperties { canFocus = false }).uiId("ds.0107")
                        ) {
                            Icon(
                                CaixaDSIcons.Back,
                                contentDescription = uiStrings.getString(LocalizedR.string.ui_voltar_59dc92),
                                tint = contentColor
                            )
                        }
                    }
                }
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (titleContent != null) {
                    titleContent()
                } else {
                    Text(
                        text = annotatedTitle ?: AnnotatedString(title),
                        color = contentColor,
                        style = TextStyle(
                            fontFamily = CaixaDSFontFamily,
                            fontSize = titleSize,
                            fontWeight = titleWeight,
                            platformStyle = PlatformTextStyle(includeFontPadding = true)
                        ),
                        maxLines = 1
                    )
                }
            }
            Box(Modifier.width(actionWidth).height(48.dp), contentAlignment = Alignment.Center) { action?.invoke() }
        }
        subtitle?.let { HeaderSubtitle(it, headerColor) }
    }
}

@Composable
fun CaixaDSBrandTopBar(title: String = "Loterias CAIXA") {
    Row(
        modifier = Modifier.fillMaxWidth().background(CaixaDS.colors.brand).statusBarsPadding()
            .height(CaixaDS.sizes.topBar).padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.legacy_brand_trevo),
            contentDescription = null,
            modifier = Modifier.size(width = 23.dp, height = 22.dp)
        )
        Box(Modifier.padding(horizontal = 16.dp).width(.75.dp).height(24.dp).background(Color.White))
        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun HeaderSubtitle(text: String, color: Color) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(73.dp)
            .background(color)
    ) {
        Box(
            Modifier.align(Alignment.TopEnd).width(130.dp).height(1.dp)
                .background(CaixaDSColorTokens.ExtendedToolbarLine)
        )
        Text(
            text,
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            color = Color.White,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            maxLines = 2
        )
    }
}
