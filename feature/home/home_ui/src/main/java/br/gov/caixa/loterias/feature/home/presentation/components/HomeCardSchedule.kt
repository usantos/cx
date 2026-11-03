package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.mapper.formatHomeDrawDate
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR

@Composable
internal fun HomeCardDrawDate(lottery: HomeLottery, drawDate: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (drawDate.isNotBlank()) {
        Row(
            Modifier.fillMaxWidth().padding(start = 19.dp, end = 19.dp, top = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(DeliveryR.drawable.installed_home_calendario),
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                colorFilter = ColorFilter.tint(lottery.listText)
            )
            Column(Modifier.padding(start = 10.dp)) {
                Text(
                    if (lottery.resultCode in listOf("loteca", "lotogol")) {
                        uiStrings.getString(DeliveryR.string.ui_encerramento_f0dcba)
                    } else {
                        uiStrings.getString(LocalizedR.string.ui_sorteio_a49bf4)
                    },
                    color = LoteriasCaixaDSColorTokens.NotificationText,
                    style = homeLegacyTextStyle(12.sp),
                    modifier = Modifier.uiId("home.0006")
                )
                Text(
                    formatHomeDrawDate(drawDate),
                    color = LoteriasCaixaDSColorTokens.NotificationText,
                    style = homeLegacyTextStyle(12.sp, FontWeight.SemiBold),
                    modifier = Modifier.uiId("home.0007")
                )
            }
        }
    }
}

@Composable
internal fun HomeCardClosingTime(lottery: HomeLottery, remainingTime: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val salesClosed = remainingTime == "Encerradas"
    Row(
        Modifier.fillMaxWidth().padding(start = 19.dp, end = 19.dp, top = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(lottery.clockRes),
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            colorFilter = ColorFilter.tint(lottery.listText)
        )
        Column(Modifier.padding(start = 10.dp)) {
            Text(
                if (salesClosed) {
                    uiStrings.getString(DeliveryR.string.ui_apostas_e73ece)
                } else {
                    uiStrings.getString(DeliveryR.string.ui_apostas_se_encerram_em_d9ca00)
                },
                color = LoteriasCaixaDSColorTokens.NotificationText,
                style = homeLegacyTextStyle(12.sp),
                modifier = Modifier.uiId("home.0008")
            )
            Text(
                remainingTime,
                color = LoteriasCaixaDSColorTokens.NotificationText,
                style = homeLegacyTextStyle(12.sp, FontWeight.SemiBold),
                modifier = Modifier.uiId("home.0009")
            )
        }
    }
}
