package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.cart.presentation.components.LegacyHomeButton
import br.gov.caixa.loterias.feature.cart.presentation.components.PoolCartFooter
import br.gov.caixa.loterias.feature.cart.presentation.components.PoolHeader
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartHelpAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAdditionUiState
import br.gov.caixa.loterias.feature.cart.ui.R as DeliveryR

@Composable
fun LegacyBetAdditionDelivered(
    state: CartAdditionUiState,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onOpenCart: () -> Unit,
    onHelp: ((Int, String) -> Unit)?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val style = loteriasCaixaDSLotteryStyle(0, state.modality)
    Column(Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.White).statusBarsPadding()) {
        PoolHeader(
            state.modality,
            state.contest,
            state.drawDate,
            style.light,
            style.content,
            onBack,
            cartHelpAction(state.modality, onHelp)
        )
        Box(Modifier.weight(1f).fillMaxWidth().background(style.light).zIndex(1f)) {
            Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 11.dp)) {
                Text(
                    if (state.modality.equals("COMBO", true)) {
                        uiStrings.getString(
                            DeliveryR.string.ui_combo_inserido_no_carrinho_com_suces_1198f8
                        )
                    } else {
                        uiStrings.getString(DeliveryR.string.ui_aposta_inserida_no_carrinho_com_suce_bcb0ad)
                    },
                    (Modifier.fillMaxWidth()).uiId("cart.0091"),
                    color = style.content,
                    fontFamily = LoteriasCaixaDSFontFamily,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    fontSize = 14.2.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Box(Modifier.fillMaxWidth().height(318.dp)) {
                    Image(
                        painterResource(br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_cart_confirmation_arrow),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(start = 59.dp, end = 42.dp, top = 9.dp),
                        contentScale = ContentScale.Inside,
                        colorFilter = ColorFilter.tint(style.content)
                    )
                    if (state.minimumCartAmount.signum() > 0) {
                        Text(
                            uiStrings.getString(
                                DeliveryR.string.ui_lembre_se_o_valor_minimo_do_total_de_61222c,
                                formatBrazilianCurrency(state.minimumCartAmount)
                            ),
                            (Modifier.padding(start = 98.dp, end = 97.dp, top = 29.dp)).uiId("cart.0092"),
                            color = style.content,
                            fontSize = 14.sp,
                            fontFamily = LoteriasCaixaDSFuturaFontFamily,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            LegacyHomeButton(onClick = onHome, modifier = (Modifier.align(Alignment.BottomCenter).offset(y = 43.dp)).uiId("cart.0093"))
        }
        Column(Modifier.background(LoteriasCaixaDSColorTokens.White).navigationBarsPadding()) {
            PoolCartFooter(state.amount, onOpenCart)
        }
    }
}
