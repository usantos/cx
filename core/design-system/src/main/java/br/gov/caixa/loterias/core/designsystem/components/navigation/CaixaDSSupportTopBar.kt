package br.gov.caixa.loterias.core.designsystem.components.navigation

import androidx.compose.runtime.Composable

/** Telas de suporte usam o cabeçalho Caixa, com seta, tipografia e espaçamento compartilhados. */
@Composable
fun CaixaDSSupportTopBar(title: String, onBack: () -> Unit) {
    CaixaDSTopBar(title = title, onBack = onBack)
    CaixaDSStatusBarColor(
        br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens.White,
        darkIcons = true
    )
}
