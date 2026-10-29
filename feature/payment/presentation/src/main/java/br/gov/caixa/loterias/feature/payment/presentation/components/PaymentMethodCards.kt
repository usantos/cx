package br.gov.caixa.loterias.feature.payment.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPaymentBrandMark
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPaymentProvider
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPaymentProviderMark
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixBrand
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCard as Card
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCardDefaults as CardDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSDeleteIcon
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.mapper.acceptedBrands
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentVisualAssets

@Composable
internal fun PixPaymentMethodCard(onPix: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 35.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = CaixaDS.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CaixaDSPixBrand((Modifier.padding(vertical = 16.dp).width(200.dp).height(90.dp)).uiId("pay.0029"))
            Button(
                onClick = onPix,
                modifier = (Modifier.fillMaxWidth().height(52.dp)).uiId("pay.0030"),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CaixaDS.colors.pix)
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_gerar_codigo_pix_copia_e_cola_1ee337),
                    color = CaixaDSColorTokens.White,
                    modifier = Modifier.uiId("pay.0031")
                )
            }
        }
    }
}

@Composable
internal fun PaymentProviderCard(
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
        colors = CardDefaults.cardColors(containerColor = CaixaDS.colors.surface),
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
                        .background(CaixaDS.colors.paymentCard)
                ) {
                    Text(
                        if (method.id == PaymentMethod.MERCADO_PAGO_ID) {
                            uiStrings.getString(LocalizedR.string.ui_voce_ainda_nao_possui_cartao_salvo_n_d906dc)
                        } else {
                            uiStrings.getString(LocalizedR.string.ui_voce_ainda_nao_possui_cartao_salvo_n_9cd97e)
                        },
                        (
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterStart)
                                .padding(start = 16.dp, end = 32.dp)
                            ).uiId("pay.0032"),
                        color = CaixaDS.colors.graphite,
                        fontSize = 16.sp,
                        lineHeight = 20.sp
                    )
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_novo_cartao_de_credito_b4cf66),
                        (
                            Modifier
                                .align(Alignment.BottomStart)
                                .uiId("pay.0033").clickable { onRequestAddCard(method.id) }
                                .padding(start = 16.dp, bottom = 18.dp)
                            ).uiId("pay.0034"),
                        color = CaixaDS.colors.brand,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
            } else {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(CaixaDS.colors.paymentCard)
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

@Composable
private fun InlinePaymentCard(card: SavedPaymentCard, onOpen: () -> Unit, onDelete: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().height(58.dp), verticalAlignment = Alignment.CenterVertically) {
        Card(
            Modifier.weight(1f).padding(start = 16.dp, end = 8.dp, bottom = 8.dp).height(50.dp)
                .uiId("pay.0035").clickable(onClick = onOpen),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = CaixaDS.colors.surface)
        ) {
            Row(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                CaixaDSPaymentBrandMark(card.brand, (Modifier.size(50.dp)).uiId("pay.0036"))
                Spacer(Modifier.weight(1f))
                Text(
                    uiStrings.getString(LocalizedR.string.ui_1_s_2fc810, card.lastDigits),
                    color = CaixaDS.colors.text,
                    modifier = Modifier.uiId("pay.0037")
                )
            }
        }
        Icon(
            CaixaDSDeleteIcon,
            contentDescription = uiStrings.getString(LocalizedR.string.ui_excluir_cartao_final_1_s_228aca, card.lastDigits),
            modifier = (Modifier.size(42.dp).uiId("pay.0038").clickable(onClick = onDelete).padding(9.dp)).uiId("pay.0039"),
            tint = CaixaDS.colors.brand
        )
    }
}

@Composable
private fun AcceptedBrandMarks(paymentMethodId: Long, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.End) {
        acceptedBrands(paymentMethodId).chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { brand -> CaixaDSPaymentBrandMark(brand, (Modifier.size(30.dp)).uiId("pay.0040")) }
            }
        }
    }
}

private fun PaymentMethod.toCaixaDSProvider(): CaixaDSPaymentProvider =
    if (id == PaymentMethod.RECARGA_PAY_ID) CaixaDSPaymentProvider.RECARGA_PAY else CaixaDSPaymentProvider.MERCADO_PAGO

@Composable
internal fun PaymentDataHeading(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 32.dp)) {
        Text(
            title,
            color = CaixaDS.colors.cartDark,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 20.sp,
            modifier = Modifier.uiId("pay.0041")
        )
        Text(
            subtitle,
            (Modifier.padding(start = 16.dp, top = 8.dp)).uiId("pay.0042"),
            color = CaixaDS.colors.graphite,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun ProviderCardActions(methodId: Long, expandable: Boolean, expanded: Boolean, add: (Long) -> Unit, toggle: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val largeFont = LocalDensity.current.fontScale > 1f
    val actionModifier = if (largeFont) Modifier.fillMaxWidth() else Modifier
    val actions: @Composable () -> Unit = {
        ProviderActionLabel(
            uiStrings.getString(LocalizedR.string.ui_adicionar_cartao_dd9fbe),
            (actionModifier)
                .uiId("pay.0043")
        ) { add(methodId) }
        if (expandable) {
            val label = if (expanded) LocalizedR.string.ui_listar_menos_cff0a0 else LocalizedR.string.ui_listar_mais_99b0d5
            ProviderActionLabel(uiStrings.getString(label), (actionModifier).uiId("pay.0044"), toggle)
        }
    }
    if (largeFont) {
        Column(Modifier.fillMaxWidth()) { actions() }
    } else {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { actions() }
    }
}

@Composable
private fun ProviderActionLabel(label: String, modifier: Modifier, onClick: () -> Unit) {
    Text(
        label,
        (modifier.uiId("pay.0045").clickable(role = Role.Button, onClick = onClick).padding(horizontal = 16.dp, vertical = 8.dp))
            .uiId("pay.0046"),
        color = CaixaDS.colors.brand,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        textDecoration = TextDecoration.Underline
    )
}

@Composable
private fun LargeFontProviderIdentity(method: PaymentMethod, visualAssets: PaymentVisualAssets) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_pagar_via_191e93),
            (Modifier.fillMaxWidth()).uiId("pay.0047"),
            color = CaixaDS.colors.graphite
        )
        CaixaDSPaymentProviderMark(
            provider = method.toCaixaDSProvider(),
            recargaPayResourceId = visualAssets.recargaPayLogoResourceId,
            modifier = (
                if (method.id == PaymentMethod.MERCADO_PAGO_ID) {
                    Modifier.width(107.dp).height(26.dp)
                } else {
                    Modifier.width(129.dp).height(43.dp)
                }
                ).uiId("pay.0048")
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_bandeiras_aceitas_bf739a),
            (Modifier.fillMaxWidth()).uiId("pay.0049"),
            color = CaixaDS.colors.graphite
        )
        AcceptedBrandMarks(method.id, modifier = Modifier.uiId("pay.0050"))
    }
}

@Composable
private fun LegacyProviderIdentity(method: PaymentMethod, visualAssets: PaymentVisualAssets) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier
            .fillMaxWidth()
            .height(92.dp)
            .padding(start = 16.dp, top = 8.dp, end = 16.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_pagar_via_191e93),
                color = CaixaDS.colors.graphite,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                modifier = Modifier.uiId("pay.0051")
            )
            CaixaDSPaymentProviderMark(
                provider = method.toCaixaDSProvider(),
                recargaPayResourceId = visualAssets.recargaPayLogoResourceId,
                modifier = (
                    if (method.id == PaymentMethod.MERCADO_PAGO_ID) {
                        Modifier.padding(top = 16.dp).width(107.dp).height(26.dp)
                    } else {
                        Modifier.padding(top = 16.dp).width(129.dp).height(43.dp)
                    }
                    ).uiId("pay.0052")
            )
        }
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_bandeiras_aceitas_bf739a),
                color = CaixaDS.colors.graphite,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                modifier = Modifier.uiId("pay.0053")
            )
            AcceptedBrandMarks(method.id, (Modifier.padding(top = 4.dp)).uiId("pay.0054"))
        }
    }
}
