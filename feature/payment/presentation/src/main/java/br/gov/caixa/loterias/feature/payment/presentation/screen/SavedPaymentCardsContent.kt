package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
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
        Box(modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.White), contentAlignment = Alignment.Center) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_voce_ainda_nao_possui_um_cartao_de_c_c57539),
                (Modifier.fillMaxWidth().padding(30.dp)).uiId("pay.0123"),
                color = LoteriasCaixaDSColorTokens.Blue700,
                fontSize = 16.2.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = savedCardTextStyle
            )
        }
        return
    }
    LazyColumn((modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.White)).uiId("pay.0124")) {
        item {
            Text(
                uiStrings.getString(LocalizedR.string.ui_cartoes_cadastrados_b7e87f),
                (Modifier.padding(start = 16.dp, top = 40.dp, end = 16.dp, bottom = 16.dp)).uiId("pay.0125"),
                color = LoteriasCaixaDSColorTokens.Blue700,
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
                    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSavedCardSection(
                        provider = if (isRecarga) {
                            br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentProvider.RECARGA_PAY
                        } else {
                            br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPaymentProvider.MERCADO_PAGO
                        },
                        intro = uiStrings.getString(
                            if (isRecarga) {
                                LocalizedR.string.ui_confira_aqui_sua_lista_de_cartoes_sa_736bcc
                            } else {
                                LocalizedR.string.ui_confira_aqui_sua_lista_de_cartoes_sa_62877f
                            }
                        ),
                        logo = { LegacyCardProviderLogo(isRecarga, it) },
                        cards = { listModifier ->
                            LazyRow(listModifier.uiId("pay.0128")) {
                                items(cards, key = { card -> savedCardListKey(method.id, card.id) }) { card ->
                                    SavedPaymentCardItem(card, { onOpenCard(card) }, { onDeleteCard(card) })
                                }
                            }
                        }
                    )
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
    fontFamily = LoteriasCaixaDSFontFamily,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

@Composable
private fun SavedPaymentCardItem(card: SavedPaymentCard, onOpen: () -> Unit, onDelete: () -> Unit) {
    br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSavedCreditCard(
        number = stringResource(LocalizedR.string.ui_1_s_dca085, card.lastDigits),
        expiration = stringResource(
            LocalizedR.string.ui_venc_1_s_2_s_ab2a97,
            card.expirationMonth.toString(),
            card.expirationYear.toString()
        ),
        brand = savedCardLegacyBrand(card.brand),
        brandDescription = card.brand,
        deleteIcon = painterResource(R.drawable.legacy_saved_card_delete),
        deleteDescription = stringResource(LocalizedR.string.ui_remover_cartao_310d97),
        onOpen = onOpen,
        onDelete = onDelete
    )
}

@Composable
private fun savedCardLegacyBrand(brand: String): androidx.compose.ui.graphics.painter.Painter {
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
    return painterResource(resource)
}
