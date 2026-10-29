package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.mapper.prizeAwardedRows
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun PrizeAwardTable(details: br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails, contentColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val money = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    Column(
        Modifier.fillMaxWidth().padding(16.dp).heightIn(min = 45.dp, max = 180.dp)
            .uiId("retail.0066").verticalScroll(rememberScrollState())
    ) {
        prizeAwardedRows(details).groupBy { it.contest }.forEach { (contest, awards) ->
            if (details.recurringDraws > 0) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_concurso_1_s_39491d, contest.toString()),
                    (Modifier.padding(horizontal = 32.dp).padding(top = 8.dp)).uiId("retail.0067"),
                    color = contentColor,
                    fontFamily = CaixaDSFuturaFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            awards.forEach { award ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 32.dp).padding(top = 8.dp, bottom = 10.dp)) {
                    Text(
                        award.description,
                        (Modifier.weight(1f)).uiId("retail.0068"),
                        color = contentColor,
                        fontFamily = CaixaDSFuturaFontFamily,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        award.amount?.let(money::format).orEmpty(),
                        color = contentColor,
                        fontFamily = CaixaDSFuturaFontFamily,
                        fontSize = 14.sp,
                        modifier = Modifier.uiId("retail.0069")
                    )
                }
            }
            Spacer(Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(1.dp).background(contentColor))
        }
    }
}
