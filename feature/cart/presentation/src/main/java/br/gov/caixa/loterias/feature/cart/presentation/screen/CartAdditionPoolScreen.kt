package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.presentation.components.LegacyHomeButton
import br.gov.caixa.loterias.feature.cart.presentation.components.PoolCartArrow
import br.gov.caixa.loterias.feature.cart.presentation.components.PoolCartFooter
import br.gov.caixa.loterias.feature.cart.presentation.components.PoolHeader
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartHelpAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAdditionUiState
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.formatRemainingTime

@Composable
internal fun LegacyPoolAddition(
    state: CartAdditionUiState,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onOpenCart: () -> Unit,
    onHelp: ((Int, String) -> Unit)?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val style = caixaDSLotteryStyle(0, state.modality)
    Column(Modifier.fillMaxSize().background(CaixaDSColorTokens.White).statusBarsPadding()) {
        PoolHeader(
            modality = state.modality,
            contest = state.contest,
            drawDate = state.drawDate,
            color = style.light,
            contentColor = style.content,
            onBack = onBack,
            onHelp = cartHelpAction(state.modality, onHelp)
        )
        Box(Modifier.weight(1f).fillMaxWidth().background(style.light).zIndex(1f)) {
            Box(
                Modifier.align(Alignment.BottomCenter).width(292.dp).height(374.dp).padding(bottom = 56.dp)
            ) {
                PoolCartArrow(
                    color = style.content.copy(alpha = 0.75f),
                    modifier = (Modifier.fillMaxSize().padding(start = 17.dp, end = 2.dp, top = 35.dp)).uiId("cart.0094")
                )
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_bolao_inserido_no_carrinho_com_suces_0423e6),
                        color = style.content,
                        fontFamily = CaixaDSFuturaFontFamily,
                        fontSize = 14.2.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.uiId("cart.0095")
                    )
                    Spacer(Modifier.height(28.dp))
                    Row(
                        Modifier
                            .background(style.dark, RoundedCornerShape(6.dp))
                            .border(1.dp, style.dark, RoundedCornerShape(6.dp))
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            CaixaDSIcons.Time,
                            contentDescription = null,
                            modifier = (Modifier.size(24.dp)).uiId("cart.0096"),
                            tint = style.darkContent
                        )
                        Text(
                            formatRemainingTime(state.remainingSeconds),
                            (Modifier.padding(start = 8.dp)).uiId("cart.0097"),
                            color = style.darkContent,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_conclua_o_pagamento_de_suas_apostas_c293f6),
                        (Modifier.padding(top = 16.dp)).uiId("cart.0098"),
                        color = style.content,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Box(
                        Modifier.padding(top = 36.dp).width(250.dp)
                            .background(style.dark, RoundedCornerShape(6.dp))
                            .border(1.dp, style.dark, RoundedCornerShape(6.dp))
                            .padding(horizontal = 4.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_devido_a_regras_de_arredondamento_o_37ef7b),
                            color = style.darkContent,
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.uiId("cart.0099")
                        )
                    }
                }
            }
            LegacyHomeButton(onClick = onHome, modifier = (Modifier.align(Alignment.BottomCenter).offset(y = 43.dp)).uiId("cart.0100"))
        }
        Column(Modifier.background(CaixaDSColorTokens.White).navigationBarsPadding()) {
            PoolCartFooter(state.amount, onOpenCart)
        }
    }
}
