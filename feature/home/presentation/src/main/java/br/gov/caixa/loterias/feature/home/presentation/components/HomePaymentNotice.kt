package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.home.presentation.delivery.HomeUiEntry

@Composable
fun HomePaymentNotice(onOpenPurchases: () -> Unit) {
    FeatureModuleContent(
        module = AppModule.HOME,
        entryType = HomeUiEntry::class.java,
        onBack = {},
        access = FeatureEntryAccess.OPTIONAL,
        preserveActive = false
    ) { entry ->
        entry.HomePaymentNotice(onOpenPurchases)
    }
}
