package br.gov.caixa.loterias.feature.onboarding.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSScaffold as Scaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSBrandTopBar
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.onboarding.R as LocalizedR

@Composable
internal fun AgeConfirmationDialog(onConfirmAdult: () -> Unit, onRejectAdult: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSDialog(
        title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
        message = uiStrings.getString(LocalizedR.string.ui_voce_tem_mais_de_18_anos_215df2),
        positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
        onPositive = onConfirmAdult,
        negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
        onNegative = onRejectAdult
    )
}

@Composable
internal fun AgeScreen(onBack: () -> Unit, onResponsibleGaming: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Scaffold(
        containerColor = CaixaDS.colors.surface,
        topBar = { CaixaDSBrandTopBar() },
        modifier = Modifier.uiId("start.0006")
    ) { padding ->
        BoxWithConstraints(Modifier.fillMaxSize().padding(padding)) {
            // O XML nativo ancora a base do selo na diretriz de 50%.
            val guideline = maxHeight / 2
            ResponsibleGamingIcon(
                Modifier.align(Alignment.TopCenter).offset {
                    // A base do selo e a coluna compartilham a mesma diretriz em pixels inteiros.
                    androidx.compose.ui.unit.IntOffset(0, guideline.roundToPx() - 100.dp.roundToPx())
                }.size(100.dp)
            )
            Column(
                Modifier.align(Alignment.TopCenter).offset(y = guideline),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))
                Box(Modifier.width(125.dp).height(1.dp).background(CaixaDS.colors.brand))
                Spacer(Modifier.height(24.dp))
                Text(
                    buildAnnotatedString {
                        append(uiStrings.getString(LocalizedR.string.ui_o_uso_do_aplicativo_e_restrito_a_mai_6418db))
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("18 anos.") }
                    },
                    modifier = (Modifier.width(347.dp).height(54.dp)).uiId("start.0007"),
                    color = CaixaDS.colors.brand,
                    fontSize = 18.sp,
                    lineHeight = 23.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(28.dp))
                Text(
                    uiStrings.getString(LocalizedR.string.ui_saiba_mais_sobre_o_jogo_responsavel_696ab2),
                    modifier = (Modifier.width(348.dp).uiId("start.0008").clickable(onClick = onResponsibleGaming)).uiId("start.0009"),
                    color = CaixaDS.colors.brand,
                    fontSize = 18.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                )
            }
            LegacyFlatAction(
                text = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
                modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 16.dp, vertical = 16.dp)
                    .fillMaxWidth().height(56.dp),
                color = CaixaDS.colors.brand,
                onClick = onBack
            )
        }
    }
}
