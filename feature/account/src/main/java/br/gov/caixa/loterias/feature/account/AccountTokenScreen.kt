package br.gov.caixa.loterias.feature.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSAuthenticationLoadingDialog

/** activity_token's empty blue surface, shared by the OAuth callback and the screen catalog. */
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
