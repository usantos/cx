package br.gov.caixa.loterias.feature.account.presentation.components

import br.gov.caixa.loterias.feature.account.R as LocalizedR

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.account.presentation.screen.PersonalDataActions
import br.gov.caixa.loterias.feature.account.presentation.screen.PersonalDataScreen
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataUiState

@Preview(name = "Dados pessoais", showBackground = true)
@Composable
private fun PersonalDataFormPreview() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSTheme(darkTheme = false) {
        PersonalDataScreen(
            state = PersonalDataUiState(
                loading = false,
                name = uiStrings.getString(LocalizedR.string.ui_nome_de_exemplo_268008),
                dailyLimit = "R$ 100,00",
                minimumDailyLimit = BigDecimal("30.00"),
                authorizedDailyLimit = BigDecimal("500.00")
            ),
            imeVisible = false,
            onBack = {},
            actions = PersonalDataActions(
                submit = {},
                changeGender = {},
                changeZipCode = {},
                changeDailyLimit = {},
                recalculateLimit = {},
                requestZipCodeRedirect = {},
                toggleNotifications = {}
            )
        )
    }
}
