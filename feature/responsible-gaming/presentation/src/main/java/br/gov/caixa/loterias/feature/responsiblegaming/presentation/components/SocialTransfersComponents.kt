package br.gov.caixa.loterias.feature.responsiblegaming.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.responsiblegaming.R as LocalizedR
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.SocialAmount

@Composable
internal fun SocialBlock(
    first: String,
    value: String,
    description: String,
    background: Color,
    foreground: Color,
    valueColor: Color,
    backgroundResource: Int,
    modifier: Modifier,
) {
    Box(modifier.fillMaxHeight()) {
        Image(
            painter = painterResource(backgroundResource),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(background)
        )
        Column(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (first.isNotEmpty()) {
                Text(first, (Modifier.padding(top = 18.dp)).uiId("rg.0008"), color = foreground, fontSize = 16.sp)
                Text(
                    value,
                    (Modifier.padding(top = 4.dp)).uiId("rg.0009"),
                    color = valueColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    value,
                    (Modifier.padding(top = 18.dp)).uiId("rg.0010"),
                    color = valueColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                description,
                (Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp)).uiId("rg.0011"),
                color = foreground,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

internal data class SocialCategoryData(val title: String, val amount: SocialAmount?, val color: Color)

@Composable
internal fun SocialCategoryPair(left: SocialCategoryData, right: SocialCategoryData, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().height(120.dp).background(CaixaDSColorTokens.SocialCanvas)) {
        Row(Modifier.fillMaxWidth().height(76.dp)) {
            SocialCategoryAmount(left, (Modifier.weight(1f)).uiId("rg.0012"))
            SocialCategoryAmount(right, (Modifier.weight(1f)).uiId("rg.0013"))
        }
        Box(Modifier.fillMaxWidth().height(44.dp)) {
            Image(
                painter = painterResource(R.drawable.icgrouprepasses),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
                colorFilter = ColorFilter.tint(CaixaDSColorTokens.SocialGraphite)
            )
            Row(Modifier.fillMaxSize()) {
                SocialCategoryLabel(left, (Modifier.weight(1f)).uiId("rg.0014"))
                SocialCategoryLabel(right, (Modifier.weight(1f)).uiId("rg.0015"))
            }
        }
    }
}

@Composable
private fun SocialCategoryAmount(data: SocialCategoryData, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_r_1_s_4e3de0, data.amount?.value ?: "0,0"),
            (Modifier.padding(top = 24.dp)).uiId("rg.0016"),
            color = data.color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            data.amount?.unit ?: uiStrings.getString(LocalizedR.string.ui_reais_95ff39),
            color = CaixaDSColorTokens.SocialGraphite,
            fontSize = 12.sp,
            modifier = Modifier.uiId("rg.0017")
        )
    }
}

@Composable
private fun SocialCategoryLabel(data: SocialCategoryData, modifier: Modifier) {
    Column(modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            data.title,
            (Modifier.padding(top = 14.dp)).uiId("rg.0018"),
            color = CaixaDSColorTokens.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.weight(1f))
        Box(Modifier.fillMaxWidth().height(5.dp).background(data.color))
    }
}
