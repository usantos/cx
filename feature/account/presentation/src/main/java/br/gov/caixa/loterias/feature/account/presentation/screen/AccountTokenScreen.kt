package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.account.presentation.delivery.AccountUiEntry

@Composable
fun AccountTokenScreen(modifier: Modifier = Modifier) {
    FeatureModuleContent(
        module = AppModule.ACCOUNT,
        entryType = AccountUiEntry::class.java,
        onBack = {},
        access = FeatureEntryAccess.REQUIRED,
        preserveActive = true
    ) { entry ->
        entry.AccountTokenScreen(modifier)
    }
}
