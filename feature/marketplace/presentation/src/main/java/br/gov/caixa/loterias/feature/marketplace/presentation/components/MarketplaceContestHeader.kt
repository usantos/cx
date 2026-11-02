package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryMark
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSMarketplaceFilterClover
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSMarketplaceTrapeze
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceStyle
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.layout.loteriasCaixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceDrawDate
import java.util.Locale

private const val CONTEST_HEADING_TEXT_SIZE = 16
private const val CONTEST_METADATA_TEXT_SIZE = 14
private const val CONTEST_TRAPEZOID_BOTTOM_RATIO = .698f

@Composable
internal fun MarketplaceContestHeader(game: LotteryGameParameters, specialName: String? = null) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val colors = marketplaceColors(game.modality.code)
    Column(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().height(55.dp).background(colors.light)) {
            MarketplaceContestArtwork(game, colors)
            Row(Modifier.fillMaxSize().padding(start = 93.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    marketplaceContestTitle(game, specialName),
                    (Modifier.weight(1f).padding(end = 24.dp)).uiId("pool.0001"),
                    color = colors.content,
                    style = marketplaceText(CONTEST_HEADING_TEXT_SIZE, FontWeight.Bold).copy(fontFamily = LoteriasCaixaDSFuturaFontFamily),
                    maxLines = 2
                )
                Column(Modifier.padding(end = 8.dp).offset(y = LoteriasCaixaDSMarketplaceStyle.contestMetadataOffset)) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_concurso_1997f5),
                        style = marketplaceText(CONTEST_METADATA_TEXT_SIZE),
                        color = colors.content,
                        modifier = Modifier.uiId("pool.0002")
                    )
                    Text(
                        game.contestNumber.toString(),
                        (Modifier.padding(top = 2.dp)).uiId("pool.0003"),
                        style = marketplaceText(CONTEST_METADATA_TEXT_SIZE, FontWeight.SemiBold),
                        color = colors.content
                    )
                }
                Column(Modifier.offset(y = LoteriasCaixaDSMarketplaceStyle.contestMetadataOffset)) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_sorteio_a49bf4),
                        style = marketplaceText(CONTEST_METADATA_TEXT_SIZE),
                        color = colors.content,
                        modifier = Modifier.uiId("pool.0004")
                    )
                    Text(
                        marketplaceDrawDate(game.drawDate),
                        (Modifier.padding(top = 2.dp)).uiId("pool.0005"),
                        style = marketplaceText(CONTEST_METADATA_TEXT_SIZE, FontWeight.SemiBold),
                        color = colors.content
                    )
                }
            }
        }
        MarketplaceContestPrize(game, colors)
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.MarketplaceContestArtwork(game: LotteryGameParameters, colors: MarketplaceColors) {
    val trapezoid =
        loteriasCaixaDSCampaignDrawable(
            game.modality.id,
            game.modality.name,
            if (game.contestType.equals("ESPECIAL", true) ||
                game.contestType.equals("E", true)
            ) {
                LoteriasCaixaDSLotteryArtwork.SPECIAL_TRAPEZE
            } else {
                LoteriasCaixaDSLotteryArtwork.TRAPEZE
            }
        )
            ?: if (game.contestType.equals("ESPECIAL", true) || game.contestType.equals("E", true)) {
                null
            } else {
                loteriasCaixaDSMarketplaceTrapeze(game.modality.id, game.modality.name)
            }
    if (trapezoid != null) {
        androidx.compose.foundation.Image(loteriasCaixaDSDrawablePainter(trapezoid), null, Modifier.width(85.dp).fillMaxHeight())
    } else {
        Canvas(Modifier.width(85.dp).fillMaxHeight()) {
            drawPath(
                Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width * CONTEST_TRAPEZOID_BOTTOM_RATIO, size.height)
                    lineTo(0f, size.height)
                    close()
                },
                colors.dark
            )
        }
    }
    val clover =
        loteriasCaixaDSCampaignDrawable(game.modality.id, game.modality.name, LoteriasCaixaDSLotteryArtwork.CLOVER_ON_DARK)
            ?: loteriasCaixaDSMarketplaceFilterClover(game.modality.id, game.modality.name, selected = true)
    if (clover != null) {
        androidx.compose.foundation.Image(
            loteriasCaixaDSDrawablePainter(clover),
            null,
            Modifier.align(Alignment.CenterStart).padding(start = 20.dp).size(27.dp, 28.dp)
        )
    } else {
        LoteriasCaixaDSLotteryMark(
            (Modifier.align(Alignment.CenterStart).padding(start = 20.dp).size(27.dp, 28.dp)).uiId("pool.0019"),
            lightColor = colors.content,
            accentColor = colors.content.copy(alpha = .5f)
        )
    }
}

private fun marketplaceContestTitle(game: LotteryGameParameters, specialName: String?): String {
    val special = game.contestType.equals("ESPECIAL", true) || game.contestType.equals("E", true)
    return (
        specialName ?: if (special) {
            game.modality.specialName.ifBlank {
                game.modality.name
            }
        } else {
            game.modality.name
        }
        ).lowercase(Locale.forLanguageTag("pt-BR"))
}
