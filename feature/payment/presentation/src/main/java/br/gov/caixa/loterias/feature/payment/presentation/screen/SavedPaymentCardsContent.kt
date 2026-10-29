package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.payment.R
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.mapper.savedCardListKey
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState

@Composable
internal fun SavedCardsContent(
    state: PaymentUiState,
    modifier: Modifier,
    onDeleteCard: (SavedPaymentCard) -> Unit,
    onOpenCard: (SavedPaymentCard) -> Unit,
    privateDataEnabled: Boolean = true,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val total = state.cards.values.sumOf { it.size }
    if (privateDataEnabled && total == 0) {
        Box(modifier.fillMaxSize().background(CaixaDSColorTokens.White), contentAlignment = Alignment.Center) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_voce_ainda_nao_possui_um_cartao_de_c_c57539),
                (Modifier.fillMaxWidth().padding(30.dp)).uiId("pay.0123"),
                color = CaixaDSColorTokens.Blue700,
                fontSize = 16.2.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = savedCardTextStyle
            )
        }
        return
    }
    LazyColumn((modifier.fillMaxSize().background(CaixaDSColorTokens.White)).uiId("pay.0124")) {
        item {
            Text(
                uiStrings.getString(LocalizedR.string.ui_cartoes_cadastrados_b7e87f),
                (Modifier.padding(start = 16.dp, top = 40.dp, end = 16.dp, bottom = 16.dp)).uiId("pay.0125"),
                color = CaixaDSColorTokens.Blue700,
                fontSize = 14.2.sp,
                fontWeight = FontWeight.Bold,
                style = savedCardTextStyle
            )
        }
        state.methods.filter(PaymentMethod::isSupportedCard).forEach { method ->
            val cards = state.cards[method.id].orEmpty()
            if (cards.isNotEmpty()) {
                item {
                    val isRecarga = method.id == PaymentMethod.RECARGA_PAY_ID
                    val accent = if (isRecarga) CaixaDSColorTokens.RecargaAccent else CaixaDSColorTokens.MercadoPagoAccent
                    Row(
                        Modifier.fillMaxWidth().height(57.dp).border(1.dp, CaixaDSColorTokens.CardOutline),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.width(8.dp).fillMaxHeight().background(accent))
                        LegacyCardProviderLogo(isRecarga, (Modifier.padding(start = 42.dp)).uiId("pay.0126"))
                    }
                    Box(Modifier.fillMaxWidth().height(270.dp).background(accent.copy(alpha = 20f / 255f))) {
                        Box(Modifier.width(8.dp).fillMaxHeight().background(accent))
                        Column(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                            Text(
                                if (isRecarga) {
                                    uiStrings.getString(
                                        LocalizedR.string.ui_confira_aqui_sua_lista_de_cartoes_sa_736bcc
                                    )
                                } else {
                                    uiStrings.getString(LocalizedR.string.ui_confira_aqui_sua_lista_de_cartoes_sa_62877f)
                                },
                                modifier = (Modifier.fillMaxWidth().offset(x = 24.dp)).uiId("pay.0127"),
                                color = CaixaDSColorTokens.NotificationIntro,
                                style = savedCardTextStyle,
                                fontSize = 14.sp
                            )
                            LazyRow(
                                (
                                    Modifier.fillMaxWidth().padding(
                                        start = 16.dp,
                                        top = 16.dp
                                    ).wrapContentHeight(Alignment.Top, unbounded = true)
                                    ).uiId("pay.0128")
                            ) {
                                items(cards, key = { card -> savedCardListKey(method.id, card.id) }) { card ->
                                    SavedPaymentCardItem(card, { onOpenCard(card) }, { onDeleteCard(card) })
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
internal fun LegacyCardProviderLogo(recarga: Boolean, modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Image(
        painterResource(if (recarga) R.drawable.legacy_card_provider_recarga else R.drawable.legacy_card_provider_mp),
        if (recarga) {
            uiStrings.getString(LocalizedR.string.ui_recargapay_354540)
        } else {
            uiStrings.getString(
                LocalizedR.string.ui_mercado_pago_fe6402
            )
        },
        modifier.then(if (recarga) Modifier.width(129.dp).height(43.dp) else Modifier.width(107.dp).height(26.dp))
    )
}

private val savedCardTextStyle = TextStyle(
    fontFamily = CaixaDSFontFamily,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

@Composable
private fun SavedPaymentCardItem(card: SavedPaymentCard, onOpen: () -> Unit, onDelete: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.padding(30.dp).width(300.dp).height(165.dp)
            .background(
                Brush.verticalGradient(listOf(CaixaDSColorTokens.SavedCardGradientStart, CaixaDSColorTokens.SavedCardGradientEnd)),
                RoundedCornerShape(10.dp)
            )
            .uiId("pay.0129").clickable(onClick = onOpen)
    ) {
        Box(
            Modifier.align(Alignment.TopEnd).size(40.dp)
                .uiId("pay.0130").clickable(onClick = onDelete),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.legacy_saved_card_delete),
                uiStrings.getString(LocalizedR.string.ui_remover_cartao_310d97),
                (Modifier.width(23.dp).height(22.dp)).uiId("pay.0131"),
                tint = CaixaDSColorTokens.White
            )
        }
        SavedCardLegacyBrandMark(
            card.brand,
            (
                Modifier.align(Alignment.BottomEnd).padding(end = 17.dp, bottom = 17.dp)
                    .width(70.dp).height(56.dp)
                ).uiId("pay.0132")
        )
        Column(Modifier.align(Alignment.TopStart).padding(start = 17.dp, top = 92.dp, end = 87.dp)) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_1_s_dca085, card.lastDigits),
                color = CaixaDSColorTokens.White,
                fontSize = 16.sp,
                style = savedCardTextStyle,
                modifier = Modifier.uiId("pay.0133")
            )
            Text(
                uiStrings.getString(
                    LocalizedR.string.ui_venc_1_s_2_s_ab2a97,
                    card.expirationMonth.toString(),
                    card.expirationYear.toString()
                ),
                color = CaixaDSColorTokens.White,
                fontSize = 13.sp,
                style = savedCardTextStyle,
                modifier = Modifier.uiId("pay.0134")
            )
        }
    }
}

@Composable
private fun SavedCardLegacyBrandMark(brand: String, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val resource = when (brand.lowercase()) {
        "visa" -> R.drawable.legacy_saved_ic_visa
        "master", "mastercard" -> R.drawable.legacy_saved_ic_master_card
        "hipercard" -> R.drawable.legacy_saved_ic_hipercard
        "diners" -> R.drawable.legacy_saved_ic_diners
        "elo" -> R.drawable.legacy_saved_elo_fundo_escuro
        "amex", uiStrings.getString(LocalizedR.string.ui_american_express_066b5b) -> R.drawable.legacy_saved_ic_american
        else -> R.drawable.legacy_saved_card_cvc
    }
    Image(painterResource(resource), brand, modifier)
}
