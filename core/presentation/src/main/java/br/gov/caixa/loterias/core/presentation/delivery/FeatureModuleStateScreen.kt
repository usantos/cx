package br.gov.caixa.loterias.core.presentation.delivery

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSEmptyState
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState
import br.gov.caixa.loterias.core.presentation.R

@Composable
fun FeatureModuleStateScreen(
    module: AppModule,
    state: ModuleDeliveryState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    embedded: Boolean = false,
) {
    val title = stringResource(moduleTitles.getValue(module))
    if (embedded) {
        FeatureModuleStateContent(title, state, onBack, onRetry, modifier)
    } else {
        LoteriasCaixaDSScaffold(title = title, onBack = onBack, modifier = modifier) { padding ->
            FeatureModuleStateContent(title, state, onBack, onRetry, Modifier.padding(padding))
        }
    }
}

@Composable
private fun FeatureModuleStateContent(
    title: String,
    state: ModuleDeliveryState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier,
) {
    val failed = state == ModuleDeliveryState.Failed
    LoteriasCaixaDSEmptyState(
        title = title,
        message = stringResource(
            when (state) {
                ModuleDeliveryState.Disabled -> R.string.module_delivery_disabled
                ModuleDeliveryState.Failed -> R.string.module_delivery_failed
                else -> R.string.module_delivery_preparing
            }
        ),
        modifier = modifier.uiId("modules.0001"),
        actionLabel = stringResource(if (failed) R.string.module_delivery_retry else R.string.module_delivery_back),
        onAction = if (failed) onRetry else onBack
    )
}

private val moduleTitles = mapOf(
    AppModule.ACCOUNT to R.string.module_title_account,
    AppModule.HOME to R.string.module_title_home,
    AppModule.ONBOARDING to R.string.module_title_onboarding,
    AppModule.BETTING to R.string.module_title_betting,
    AppModule.MARKETPLACE to R.string.module_title_marketplace,
    AppModule.MARKETPLACE_FILTER to R.string.module_title_marketplace_filter,
    AppModule.CART to R.string.module_title_cart,
    AppModule.PAYMENT to R.string.module_title_payment,
    AppModule.TICKETS to R.string.module_title_tickets,
    AppModule.RESULTS to R.string.module_title_results,
    AppModule.NOTIFICATIONS to R.string.module_title_notifications,
    AppModule.RESPONSIBLE_GAMING to R.string.module_title_responsible_gaming,
    AppModule.RETAIL to R.string.module_title_retail,
    AppModule.SUPPORT_HELP to R.string.module_title_support_help,
    AppModule.SUPPORT_FAQ to R.string.module_title_support_faq
)
