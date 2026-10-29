package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSComboDetailClover
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSComboSpecialStrip
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSMarketplaceFilterClover
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.BetComboEntry
import br.gov.caixa.loterias.feature.betting.presentation.mapper.ComboTextSize
import br.gov.caixa.loterias.feature.betting.presentation.mapper.catalogName
import br.gov.caixa.loterias.feature.betting.presentation.mapper.comboEntryName
import br.gov.caixa.loterias.feature.betting.presentation.mapper.comboText
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.presentation.mapper.specialEntry

@Composable
internal fun LegacyComboCatalogCard(combo: BetCombo, onClick: () -> Unit) {
    val special = combo.specialEntry()?.modality
    val strip = special?.let { caixaDSComboSpecialStrip(it.id, it.code) } ?: R.drawable.tarja_trevo_padrao_lista_combos
    val clover = special?.let {
        caixaDSCampaignDrawable(it.id, it.code, CaixaDSLotteryArtwork.CLOVER_ON_DARK)
            ?: caixaDSMarketplaceFilterClover(it.id, it.code, selected = true)
    } ?: R.drawable.trevo_lista_combo
    val nameColor = special?.let { caixaDSLotteryStyle(it.id, it.code).listText } ?: CaixaDSColorTokens.ComboCardName
    Box(
        Modifier.padding(
            top = 8.dp
        ).width(
            147.dp
        ).height(102.dp).legacyAutomationId("combo-catalog-${combo.id}")
            .uiId("bet.0014").clickable(role = androidx.compose.ui.semantics.Role.Button, onClick = onClick)
    ) {
        Box(
            Modifier.align(Alignment.BottomCenter).width(147.dp).height(96.dp)
                .background(CaixaDSColorTokens.White, RoundedCornerShape(4.dp))
                .border(1.dp, CaixaDSColorTokens.ComboCardBorder, RoundedCornerShape(4.dp))
        ) {
            Image(
                painterResource(R.drawable.trevo_background_lista_combo),
                null,
                Modifier.offset(x = 1.dp, y = (-2.5).dp).width(59.dp).height(61.dp)
            )
        }
        Box(Modifier.align(Alignment.TopCenter).width(39.dp).height(33.dp)) {
            Image(caixaDSDrawablePainter(strip), null, Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
            Image(
                caixaDSDrawablePainter(clover),
                null,
                Modifier.align(Alignment.CenterStart).padding(start = 6.dp).width(22.dp).height(21.dp),
                contentScale = ContentScale.FillBounds
            )
        }
        Box(Modifier.fillMaxSize().padding(top = 33.dp, bottom = 16.dp), contentAlignment = Alignment.Center) {
            Column(Modifier.fillMaxWidth().wrapContentHeight(unbounded = true), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    combo.catalogName(),
                    (Modifier.padding(start = 5.dp, end = 5.dp, top = 13.dp)).uiId("bet.0015"),
                    style = comboText(ComboTextSize.LABEL, FontWeight.SemiBold),
                    color = nameColor,
                    textAlign = TextAlign.Center
                )
                Text(
                    formatBetAmount(combo.amount),
                    style = comboText(ComboTextSize.LABEL, FontWeight.Bold, CaixaDSFuturaFontFamily),
                    color = CaixaDSColorTokens.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.uiId("bet.0016")
                )
            }
        }
    }
}

@Composable
internal fun ComboDetailEntryCard(entry: BetComboEntry) {
    val resources = LocalResources.current
    val color = caixaDSLotteryStyle(entry.modality.id, entry.modality.code).listText
    val contest = if (entry.recurringDraws > 0) {
        resources.getString(
            R.string.combo_contest_range,
            entry.contestNumber.toString(),
            (entry.contestNumber + entry.recurringDraws - 1).toString()
        )
    } else {
        resources.getString(R.string.ui_concurso_1_s_36bb8b, entry.contestNumber.toString())
    }
    val key = "${entry.modality.code}-${entry.contestType}-${entry.contestNumber}"
    Box(
        Modifier.fillMaxWidth().padding(start = 2.dp, top = 3.dp).height(70.dp)
            .legacyAutomationId("combo-entry-$key").background(CaixaDSColorTokens.White, RoundedCornerShape(3.5.dp))
            .border(1.dp, CaixaDSColorTokens.ComboDark, RoundedCornerShape(3.5.dp))
    ) {
        Image(
            caixaDSDrawablePainter(caixaDSComboDetailClover(entry.modality.id, entry.modality.code)),
            null,
            Modifier.padding(start = 1.dp, top = 1.dp).width(40.dp).height(37.dp).legacyAutomationId("combo-clover-$key")
        )
        Column(
            Modifier.fillMaxSize().padding(top = 6.dp, bottom = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                if (entry.betQuantity == 1) {
                    resources.getString(R.string.combo_one_game)
                } else {
                    resources.getString(R.string.ui_1_s_jogos_090154, entry.betQuantity.toString())
                },
                style = comboText(ComboTextSize.CAPTION),
                color = color,
                modifier = Modifier.uiId("bet.0017")
            )
            Text(
                comboEntryName(entry),
                (Modifier.padding(start = 30.dp, end = 15.dp)).uiId("bet.0018"),
                style = comboText(ComboTextSize.LABEL, FontWeight.SemiBold),
                color = color,
                textAlign = TextAlign.Center
            )
            Text(contest, style = comboText(ComboTextSize.CAPTION), color = color, modifier = Modifier.uiId("bet.0019"))
        }
    }
}
