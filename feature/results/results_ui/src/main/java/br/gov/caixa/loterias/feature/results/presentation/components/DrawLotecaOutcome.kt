package br.gov.caixa.loterias.feature.results.presentation.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.results.R
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam
import br.gov.caixa.loterias.feature.results.ui.R as DeliveryR

@Composable
internal fun LotecaOutcome(team: DrawSportsTeam?, won: Boolean, legend: String, color: Color, expandedText: Boolean = false) {
    val textColor = if (won) LoteriasCaixaDSColorTokens.Black else color
    val outcomeModifier = (if (expandedText) Modifier.fillMaxWidth() else Modifier.width(80.dp).heightIn(min = 115.dp))
        .background(if (won) LoteriasCaixaDSColorTokens.RapidExcluded else LoteriasCaixaDSColorTokens.Transparent)
        .semantics { selected = won }
    if (expandedText) {
        Row(
            outcomeModifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LotecaEmblem(team, (Modifier.size(40.dp)).uiId("results.0003"))
            LotecaOutcomeText(team, legend, textColor, TextAlign.Start, (Modifier.weight(1f)).uiId("results.0004"))
        }
    } else {
        Column(outcomeModifier, horizontalAlignment = Alignment.CenterHorizontally) {
            LotecaEmblem(team, (Modifier.padding(top = 16.dp, bottom = 10.dp).size(40.dp)).uiId("results.0005"))
            LotecaOutcomeText(team, legend, textColor, TextAlign.Center, (Modifier.fillMaxWidth()).uiId("results.0006"))
        }
    }
}

@Composable
private fun LotecaEmblem(team: DrawSportsTeam?, modifier: Modifier) {
    val bitmap = remember(team?.shieldBase64) {
        team?.shieldBase64?.let { encoded ->
            runCatching {
                val bytes = Base64.decode(encoded.substringAfter("base64,", encoded), Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            }.getOrNull()
        }
    }
    if (bitmap != null) {
        Image(bitmap, null, modifier)
    } else {
        Image(
            painterResource(
                if (team == null) DeliveryR.drawable.legacy_result_time_versus_white else DeliveryR.drawable.legacy_result_time_padrao
            ),
            null,
            modifier
        )
    }
}

@Composable
private fun LotecaOutcomeText(team: DrawSportsTeam?, legend: String, color: Color, textAlign: TextAlign, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(modifier) {
        Text(
            team?.name ?: uiStrings.getString(DeliveryR.string.ui_empate_0b5021),
            (Modifier.fillMaxWidth()).uiId("results.0007"),
            color = color,
            fontSize = 13.sp,
            fontFamily = LoteriasCaixaDSRegularFontFamily,
            fontWeight = FontWeight.Bold,
            textAlign = textAlign
        )
        val region = team?.let {
            if (it.countryCode.isNotBlank() && (!it.countryCode.equals("BRA", true) || it.name.equals("BRASIL", true))) {
                "/${it.countryCode}"
            } else {
                it.state.takeIf(String::isNotBlank)?.let { state -> "/$state" }.orEmpty()
            }
        }.orEmpty()
        Text(
            listOf(region, legend).filter(String::isNotBlank).joinToString(" "),
            (Modifier.fillMaxWidth()).uiId("results.0008"),
            color = color,
            fontSize = 13.sp,
            fontFamily = LoteriasCaixaDSRegularFontFamily,
            textAlign = textAlign
        )
    }
}
