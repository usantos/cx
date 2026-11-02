package br.gov.caixa.loterias.core.designsystem.components.navigation

import androidx.compose.runtime.Composable

/** Telas de suporte usam o cabeçalho Caixa, com seta, tipografia e espaçamento compartilhados. */
@Composable
fun LoteriasCaixaDSSupportTopBar(title: String, onBack: () -> Unit) {
    LoteriasCaixaDSTopBar(title = title, onBack = onBack)
    LoteriasCaixaDSStatusBarColor(
        br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens.White,
        darkIcons = true
    )
}
