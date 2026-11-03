package br.gov.caixa.loterias.feature.payment.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCard as Card
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCardDefaults as CardDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentVisualAssets
import br.gov.caixa.loterias.feature.payment.ui.R as DeliveryR

@Composable
fun PaymentProviderCard(
    method: PaymentMethod,
    cards: List<SavedPaymentCard>,
    visibleCards: List<SavedPaymentCard>,
    expanded: Boolean,
    visualAssets: PaymentVisualAssets,
    onOpenCard: (SavedPaymentCard) -> Unit,
    onRequestAddCard: (Long) -> Unit,
    onRequestDelete: (SavedPaymentCard) -> Unit,
    onToggleExpanded: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Card(
        Modifier.fillMaxWidth().padding(start = 35.dp, top = 16.dp, end = 35.dp, bottom = 8.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = LoteriasCaixaDS.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            if (LocalDensity.current.fontScale > 1f) {
                LargeFontProviderIdentity(method, visualAssets)
            } else {
                LegacyProviderIdentity(method, visualAssets)
            }
            if (cards.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .background(LoteriasCaixaDS.colors.paymentCard)
                ) {
                    Text(
                        if (method.id == PaymentMethod.MERCADO_PAGO_ID) {
                            uiStrings.getString(DeliveryR.string.ui_voce_ainda_nao_possui_cartao_salvo_n_d906dc)
                        } else {
                            uiStrings.getString(DeliveryR.string.ui_voce_ainda_nao_possui_cartao_salvo_n_9cd97e)
                        },
                        (
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterStart)
                                .padding(start = 16.dp, end = 32.dp)
                            ).uiId("pay.0032"),
                        color = LoteriasCaixaDS.colors.graphite,
                        fontSize = 16.sp,
                        lineHeight = 20.sp
                    )
                    Text(
                        uiStrings.getString(DeliveryR.string.ui_novo_cartao_de_credito_b4cf66),
                        (
                            Modifier
                                .align(Alignment.BottomStart)
                                .uiId("pay.0033").clickable { onRequestAddCard(method.id) }
                                .padding(start = 16.dp, bottom = 18.dp)
                            ).uiId("pay.0034"),
                        color = LoteriasCaixaDS.colors.brand,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
            } else {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(LoteriasCaixaDS.colors.paymentCard)
                        .padding(top = 16.dp, bottom = 10.dp)
                ) {
                    visibleCards.forEach { card ->
                        InlinePaymentCard(
                            card = card,
                            onOpen = { onOpenCard(card) },
                            onDelete = { onRequestDelete(card) }
                        )
                    }
                    ProviderCardActions(method.id, cards.size > 2, expanded, onRequestAddCard, onToggleExpanded)
                }
            }
        }
    }
}
