package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog

/** Superfície azul vazia de activity_token, compartilhada pelo callback OAuth e pelo catálogo de telas. */
@Composable
fun AccountTokenScreen(modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxSize().background(CaixaDS.colors.surface)
            .statusBarsPadding().navigationBarsPadding()
            .background(CaixaDS.colors.authenticationBackground),
    ) {
        CaixaDSAuthenticationLoadingDialog()
    }
}
