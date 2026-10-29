package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSFooterButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSOutlinedButton
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.payment.R as LocalizedR

@Composable
internal fun PaymentFailureScreen(spec: WorkflowSpec, onNavigate: (String) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(title = spec.title, onBack = { onNavigate("home") }, modifier = Modifier.uiId("pay.0093")) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                Modifier.size(76.dp).border(3.dp, CaixaDS.colors.error, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    CaixaDSIcons.Close,
                    contentDescription = null,
                    tint = CaixaDS.colors.error,
                    modifier = (Modifier.size(38.dp)).uiId("pay.0094")
                )
            }
            Text(
                spec.description,
                (Modifier.padding(top = 24.dp)).uiId("pay.0095"),
                color = CaixaDS.colors.cartDark,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_revise_os_dados_do_pagamento_ou_esco_b71aea),
                (Modifier.padding(top = 12.dp)).uiId("pay.0096"),
                color = CaixaDS.colors.graphite,
                textAlign = TextAlign.Center
            )
            CaixaDSFooterButton(
                spec.primaryAction,
                { onNavigate("payment/method") },
                (Modifier.width(220.dp).padding(top = 32.dp)).uiId("pay.0097")
            )
            CaixaDSOutlinedButton(
                spec.secondaryAction.orEmpty(),
                { onNavigate("home") },
                (Modifier.width(220.dp).padding(top = 12.dp)).uiId("pay.0098")
            )
        }
    }
}
