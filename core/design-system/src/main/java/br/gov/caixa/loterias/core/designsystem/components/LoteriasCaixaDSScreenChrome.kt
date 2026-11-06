package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
internal fun LoteriasCaixaDSScreenChrome(
    modifier: Modifier,
    containerColor: Color,
    customTopBar: (@Composable () -> Unit)?,
    topBar: @Composable () -> Unit,
    bottomBar: (@Composable () -> Unit)?,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = containerColor,
        topBar = { if (customTopBar != null) customTopBar() else topBar() },
        bottomBar = { LoteriasCaixaDSScreenBottomBar(bottomBar) },
        content = content
    )
}

@Composable
private fun LoteriasCaixaDSScreenBottomBar(bottomBar: (@Composable () -> Unit)?) {
    bottomBar?.let {
        Column(Modifier.background(LoteriasCaixaDS.colors.surface).navigationBarsPadding()) { it() }
    }
}

internal fun PaddingValues.forToolbarGuideline(enabled: Boolean, overlap: Dp): PaddingValues = if (enabled) {
    object : PaddingValues by this {
        override fun calculateTopPadding(): Dp = this@forToolbarGuideline.calculateTopPadding() - overlap
    }
} else {
    this
}

internal fun usesCaixaHeader(gradient: Boolean, startColor: Color?, endColor: Color?): Boolean = !gradient &&
    (startColor == null || startColor == LoteriasCaixaDSColorTokens.Blue700) &&
    (endColor == null || endColor == LoteriasCaixaDSColorTokens.Blue700)
