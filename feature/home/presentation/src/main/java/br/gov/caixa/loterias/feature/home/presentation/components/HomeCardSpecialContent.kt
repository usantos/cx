package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind

@Composable
internal fun SpecialLotteryContent(kind: HomeLotteryKind) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.fillMaxSize().background(CaixaDSColorTokens.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (kind == HomeLotteryKind.INSTANT) {
            // card_modalidade mantém os dois TextViews de prêmio INVISIBLE, preservando a altura de suas fontes.
            Spacer(Modifier.height(25.dp))
            Text("", style = homeLegacyTextStyle(25.sp, FontWeight.Bold), modifier = Modifier.uiId("home.0010"))
            Text("", style = homeLegacyTextStyle(20.sp, FontWeight.Bold), modifier = Modifier.uiId("home.0011"))
            Text(
                uiStrings.getString(LocalizedR.string.ui_diversao_e_premios_na_hora_d9b789),
                (Modifier.fillMaxWidth().padding(top = 3.dp)).uiId("home.0012"),
                color = CaixaDSColorTokens.NotificationText,
                style = homeLegacyTextStyle(13.8.sp),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Image(
                painter = painterResource(R.drawable.imagem_instantanea),
                contentDescription = uiStrings.getString(LocalizedR.string.ui_bilhetes_da_instantanea_a14b7f),
                modifier = Modifier.wrapContentSize(Alignment.TopCenter, unbounded = true).requiredSize(246.dp, 186.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                buildAnnotatedString {
                    append(uiStrings.getString(LocalizedR.string.ui_acesse_e_jogue_no_portal_da_511a08))
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(uiStrings.getString(LocalizedR.string.final_instant_title))
                    }
                },
                (Modifier.fillMaxWidth().padding(top = 20.dp)).uiId("home.0013"),
                color = CaixaDSColorTokens.NotificationText,
                style = homeLegacyTextStyle(13.8.sp),
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                uiStrings.getString(LocalizedR.string.ui_mais_que_apostas_combos_criados_para_9c4a06),
                (Modifier.fillMaxWidth().padding(top = 40.dp)).uiId("home.0014"),
                color = CaixaDSColorTokens.NotificationText,
                style = homeLegacyTextStyle(13.8.sp),
                textAlign = TextAlign.Center
            )
            Image(
                painter = painterResource(R.drawable.imagem_combos),
                contentDescription = uiStrings.getString(LocalizedR.string.ui_combos_de_apostas_5dd87d),
                modifier = Modifier.wrapContentSize(Alignment.TopCenter, unbounded = true).requiredSize(233.dp, 327.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}
