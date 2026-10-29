package br.gov.caixa.loterias.feature.account.navigation

import androidx.compose.runtime.Composable

interface LoginConnectionPresentation {
    @Composable fun Offline(onRetry: () -> Unit, loading: Boolean)
}
