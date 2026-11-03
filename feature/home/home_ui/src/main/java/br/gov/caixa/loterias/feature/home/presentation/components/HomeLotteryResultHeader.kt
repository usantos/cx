package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR
import kotlin.math.roundToInt

@Composable
internal fun LotteryResultHeader(
    lottery: HomeLottery,
    resultExpanded: Boolean,
    expansion: androidx.compose.runtime.State<Float>,
    onToggleResult: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.layout { measurable, constraints ->
            val width = (240.dp.toPx() + 40.dp.toPx() * expansion.value).roundToInt()
                .coerceAtMost(constraints.maxWidth)
            val header = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
            layout(width, header.height) { header.placeRelative(0, 0) }
        }.height(32.dp).background(lottery.darkColor)
            .uiId("home.0035").clickable(onClick = onToggleResult),
        contentAlignment = Alignment.Center
    ) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_ultimo_resultado_d527ea),
            color = lottery.darkContent,
            style = homeLegacyTextStyle(14.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.uiId("home.0036")
        )
        Icon(
            painterResource(if (resultExpanded) DeliveryR.drawable.legacy_home_ic_x_bold else DeliveryR.drawable.legacy_home_ic_seta_baixo),
            contentDescription = if (resultExpanded) {
                uiStrings.getString(
                    DeliveryR.string.ui_fechar_resultado_3062bc
                )
            } else {
                uiStrings.getString(
                    DeliveryR.string.ui_abrir_ultimo_resultado_4bb93c
                )
            },
            modifier = (Modifier.align(Alignment.CenterEnd).padding(end = 12.dp).size(9.dp)).uiId("home.0037"),
            tint = lottery.darkContent
        )
    }
}
