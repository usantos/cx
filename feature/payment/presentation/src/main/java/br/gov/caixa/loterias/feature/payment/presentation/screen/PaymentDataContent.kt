package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.mapper.paymentTopBarTitle
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentVisualAssets
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentMode
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState

@Composable
internal fun PaymentDataContent(
    mode: PaymentMode,
    state: PaymentUiState,
    visualAssets: PaymentVisualAssets,
    handleBack: () -> Unit,
    onHelp: () -> Unit,
    onPix: () -> Unit,
    onOpenCard: (SavedPaymentCard) -> Unit,
    onPurchases: () -> Unit,
    privateDataEnabled: Boolean,
    actions: PaymentDataActions,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = if (mode == PaymentMode.CARDS) {
            uiStrings.getString(LocalizedR.string.ui_meus_cartoes_8f78b1)
        } else {
            uiStrings.getString(
                LocalizedR.string.ui_forma_de_pagamento_faf708
            )
        },

        onBack = handleBack,
        topBarAction = if (mode != PaymentMode.CARDS) {
            (
                {
                    Icon(
                        CaixaDSIcons.Help,
                        contentDescription = uiStrings.getString(LocalizedR.string.ui_ajuda_fe0add),
                        tint = CaixaDSColorTokens.White,
                        modifier = (Modifier.fillMaxSize().uiId("pay.0087").clickable(onClick = onHelp).padding(12.dp)).uiId("pay.0088")
                    )
                }
                )
        } else {
            null
        },
        modifier = Modifier.uiId("pay.0089")
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.brand)
            }

            state.pixRecoveryRequired || state.pixSessionMismatch -> Column(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_recuperar_o_codigo_2d2b23),
                    color = CaixaDS.colors.graphite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.uiId("pay.0090")
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onPurchases,
                    modifier = Modifier
                        .uiId("pay.0091")
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_minhas_compras_c1e245),
                        modifier = Modifier
                            .uiId("pay.0092")
                    )
                }
            }

            mode == PaymentMode.METHODS -> PaymentMethodsContent(
                state,
                Modifier.padding(padding),
                visualAssets,
                onPix,
                onOpenCard,
                actions.requestAddCard,
                actions.requestDelete
            )

            mode == PaymentMode.CARDS -> SavedCardsContent(
                state,
                Modifier.padding(padding),
                actions.requestDelete,
                onOpenCard,
                privateDataEnabled
            )

            else -> PixContent(state.pix, state.checkingPix, Modifier.padding(padding), actions.checkPixStatus, onPurchases)
        }
    }
}

internal data class PaymentDataActions(
    val requestAddCard: (Long) -> Unit,
    val requestDelete: (SavedPaymentCard) -> Unit,
    val checkPixStatus: () -> Unit,
)
