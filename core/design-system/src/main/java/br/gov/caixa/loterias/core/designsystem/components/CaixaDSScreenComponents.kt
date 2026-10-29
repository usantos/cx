package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSTopBar
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS

@Composable
fun CaixaDSScaffold(
    title: String,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    gradientTopBar: Boolean = false,
    topBarStartColor: Color? = null,
    topBarEndColor: Color? = null,
    topBarContentColor: Color = CaixaDSColorTokens.White,
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
    containerColor: Color = CaixaDS.colors.canvas,
    content: @Composable (PaddingValues) -> Unit,
) {
    // O cabeçalho azul da Caixa mantém a mesma tipografia e geometria de navegação em todas as telas.
    val caixaHeader = !gradientTopBar &&
        (topBarStartColor == null || topBarStartColor == CaixaDSColorTokens.Blue700) &&
        (topBarEndColor == null || topBarEndColor == CaixaDSColorTokens.Blue700)
    val density = androidx.compose.ui.platform.LocalDensity.current
    // O ConstraintLayout instalado lê guideBegin com getDimensionPixelOffset,
    // enquanto a altura da barra de ferramentas usa getDimensionPixelSize. Preserva essa sobreposição.
    val guidelineOverlap = with(density) {
        val toolbarPixels = CaixaDS.sizes.topBar.toPx()
        (CaixaDS.sizes.topBar.roundToPx() - toolbarPixels.toInt()).toDp()
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = containerColor,
        topBar = {
            if (customTopBar != null) customTopBar() else
            CaixaDSTopBar(
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
                subtitle = topBarSubtitle,
            )
        },
        bottomBar = {
            bottomBar?.let {
                Column(Modifier.background(CaixaDS.colors.surface).navigationBarsPadding()) { it() }
            }
        },
        content = { padding ->
            content(if (contentUsesTruncatedToolbarGuideline) object : PaddingValues by padding {
                override fun calculateTopPadding(): Dp = padding.calculateTopPadding() - guidelineOverlap
            } else padding)
        },
    )
}
