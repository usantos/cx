package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.account.domain.model.AccountNotification
import br.gov.caixa.loterias.feature.home.presentation.delivery.HomeUiEntry

@Composable
fun HomeNotificationPopup(notification: AccountNotification, onClose: () -> Unit, onOpenLink: () -> Unit) {
    FeatureModuleContent(
        module = AppModule.HOME,
        entryType = HomeUiEntry::class.java,
        onBack = onClose,
        access = FeatureEntryAccess.OPTIONAL,
        preserveActive = false
    ) { entry ->
        entry.HomeNotificationPopup(notification, onClose, onOpenLink)
    }
}
