package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.account.presentation.delivery.AccountUiEntry

@Composable
fun SuccessScreen(registration: Boolean) {
    FeatureModuleContent(
        module = AppModule.ACCOUNT,
        entryType = AccountUiEntry::class.java,
        onBack = {},
        access = FeatureEntryAccess.CONTINUATION,
        preserveActive = true
    ) { entry ->
        entry.SuccessScreen(registration)
    }
}
