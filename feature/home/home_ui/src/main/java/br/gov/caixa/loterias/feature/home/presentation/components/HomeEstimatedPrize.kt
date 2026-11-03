package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.mapper.formatCompactPrize
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR

@Composable
internal fun HomeEstimatedPrize(lottery: HomeLottery, enlarged: Boolean = false) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (lottery.estimatedPrize.signum() > 0) {
        Text(
            formatBrazilianCurrency(lottery.estimatedPrize),
            (Modifier.then(if (enlarged) Modifier.fillMaxWidth().padding(horizontal = 16.dp) else Modifier).padding(top = 25.dp))
                .uiId("home.0001"),
            color = lottery.listText,
            textAlign = TextAlign.Center,
            style = homeLegacyTextStyle(20.sp, FontWeight.Bold, xmlTextSize = false)
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_1_s_4c75be, formatCompactPrize(lottery.estimatedPrize)),
            color = lottery.listText,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = homeLegacyTextStyle(16.sp, FontWeight.Bold, xmlTextSize = false),
            modifier = Modifier.uiId("home.0002")
        )
        Text(
            buildAnnotatedString {
                append(uiStrings.getString(DeliveryR.string.ui_premio_estimado_do_concurso_4dddfc))
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(lottery.contestNumber.toString()) }
            },
            (Modifier.padding(top = 3.dp)).uiId("home.0003"),
            color = LoteriasCaixaDSColorTokens.NotificationText,
            style = homeLegacyTextStyle(13.8.sp)
        )
    } else {
        Text(
            buildAnnotatedString {
                append(uiStrings.getString(DeliveryR.string.ui_proximo_concurso_749d77))
                withStyle(SpanStyle(color = LoteriasCaixaDSColorTokens.NotificationText, fontSize = 18.sp)) {
                    append(lottery.contestNumber.toString())
                }
                append(uiStrings.getString(DeliveryR.string.ui_aguardando_estimativa_de_premio_efd8b1))
            },
            (Modifier.padding(top = 25.dp)).uiId("home.0004"),
            color = lottery.listText,
            style = homeLegacyTextStyle(14.sp, FontWeight.Bold, xmlTextSize = false),
            textAlign = TextAlign.Center
        )
        // A legenda legada fica INVISIBLE, preservando sua altura medida e margem.
        Text(
            uiStrings.getString(DeliveryR.string.ui_premio_estimado_do_concurso_b1224f),
            (Modifier.padding(top = 3.dp).alpha(0f).clearAndSetSemantics { }).uiId("home.0005"),
            style = homeLegacyTextStyle(13.8.sp)
        )
    }
}
