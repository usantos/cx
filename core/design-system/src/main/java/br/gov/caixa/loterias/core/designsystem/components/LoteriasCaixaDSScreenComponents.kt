package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSTopBar
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
fun LoteriasCaixaDSScaffold(
    title: String,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    gradientTopBar: Boolean = false,
    topBarStartColor: Color? = null,
    topBarEndColor: Color? = null,
    topBarContentColor: Color = LoteriasCaixaDSColorTokens.White,
    topBarAction: (@Composable () -> Unit)? = null,
    topBarActionWidth: Dp = 48.dp,
    topBarNavigationWidth: Dp = 72.dp,
    topBarNavigationContent: (@Composable () -> Unit)? = null,
    topBarTitleSize: androidx.compose.ui.unit.TextUnit = 16.sp,
    topBarTitleWeight: FontWeight = FontWeight.Bold,
    topBarAnnotatedTitle: AnnotatedString? = null,
    topBarTitleContent: (@Composable () -> Unit)? = null,
    topBarSubtitle: String? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    customTopBar: (@Composable () -> Unit)? = null,
    contentUsesTruncatedToolbarGuideline: Boolean = false,
    containerColor: Color = LoteriasCaixaDS.colors.canvas,
    content: @Composable (PaddingValues) -> Unit,
) {
    // O cabeçalho azul da Caixa mantém a mesma tipografia e geometria de navegação em todas as telas.
    val caixaHeader = usesCaixaHeader(gradientTopBar, topBarStartColor, topBarEndColor)
    val density = androidx.compose.ui.platform.LocalDensity.current
    // O ConstraintLayout instalado lê guideBegin com getDimensionPixelOffset,
    // enquanto a altura da barra de ferramentas usa getDimensionPixelSize. Preserva essa sobreposição.
    val guidelineOverlap = with(density) {
        val toolbarPixels = LoteriasCaixaDS.sizes.topBar.toPx()
        (LoteriasCaixaDS.sizes.topBar.roundToPx() - toolbarPixels.toInt()).toDp()
    }
    LoteriasCaixaDSScreenChrome(
        modifier = modifier,
        containerColor = containerColor,
        customTopBar = customTopBar,
        topBar = {
            LoteriasCaixaDSTopBar(
                title = title,
                onBack = onBack,
                action = topBarAction,
                gradient = gradientTopBar,
                startColor = topBarStartColor,
                endColor = topBarEndColor,
                contentColor = topBarContentColor,
                actionWidth = topBarActionWidth,
                navigationWidth = if (caixaHeader) 72.dp else topBarNavigationWidth,
                navigationContent = if (caixaHeader) null else topBarNavigationContent,
                titleSize = if (caixaHeader) 16.sp else topBarTitleSize,
                titleWeight = if (caixaHeader) FontWeight.Bold else topBarTitleWeight,
                annotatedTitle = if (caixaHeader) null else topBarAnnotatedTitle,
                titleContent = topBarTitleContent,
                subtitle = topBarSubtitle
            )
        },
        bottomBar = bottomBar,
        content = { padding ->
            content(padding.forToolbarGuideline(contentUsesTruncatedToolbarGuideline, guidelineOverlap))
        }
    )
}
