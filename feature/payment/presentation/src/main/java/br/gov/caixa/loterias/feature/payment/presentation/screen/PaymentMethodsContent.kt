package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.components.PaymentDataHeading
import br.gov.caixa.loterias.feature.payment.presentation.components.PaymentProviderCard
import br.gov.caixa.loterias.feature.payment.presentation.components.PixPaymentMethodCard
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PAYMENT_CARD_PROCESSOR_GUIDANCE
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentVisualAssets
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState

@Composable
internal fun PaymentMethodsContent(
    state: PaymentUiState,
    modifier: Modifier,
    visualAssets: PaymentVisualAssets,
    onPix: () -> Unit,
    onOpenCard: (SavedPaymentCard) -> Unit,
    onRequestAddCard: (Long) -> Unit,
    onRequestDelete: (SavedPaymentCard) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var expandedProviders by remember { mutableStateOf(emptySet<Long>()) }
    LazyColumn((modifier.fillMaxSize().background(CaixaDS.colors.paymentCanvas)).uiId("pay.0099")) {
        val pix = state.methods.firstOrNull(PaymentMethod::isPix)
        if (pix != null) {
            item {
                PaymentDataHeading("Pix", uiStrings.getString(LocalizedR.string.ui_gere_um_codigo_pix_copia_e_cola_para_0f0e33))
                PixPaymentMethodCard(onPix)
            }
        }
        val cardMethods = state.methods.filter(PaymentMethod::isSupportedCard)
        if (cardMethods.isNotEmpty()) {
            item { PaymentDataHeading(uiStrings.getString(LocalizedR.string.ui_cartao_de_credito_60d5db), PAYMENT_CARD_PROCESSOR_GUIDANCE) }
            items(cardMethods, key = PaymentMethod::id) { method ->
                val cards = state.cards[method.id].orEmpty()
                val expanded = method.id in expandedProviders
                val visibleCards = if (expanded) cards else cards.take(2)
                PaymentProviderCard(
                    method, cards, visibleCards, expanded, visualAssets,
                    onOpenCard, onRequestAddCard, onRequestDelete,
                    onToggleExpanded = {
                        expandedProviders = if (expanded) {
                            expandedProviders - method.id
                        } else {
                            expandedProviders + method.id
                        }
                    }
                )
            }
        }
    }
}
