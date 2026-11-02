package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.loteriasCaixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.layout.loteriasCaixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.R
import br.gov.caixa.loterias.feature.results.presentation.components.drawResultTitle
import br.gov.caixa.loterias.feature.results.presentation.mapper.modalityName
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState

@Composable
internal fun ResultLotteryHeader(state: ResultsUiState, lotteryStyle: LoteriasCaixaDSLotteryStyle) {
    Box(Modifier.fillMaxWidth().height(71.dp).background(lotteryStyle.light)) {
        val specialHeader = specialResultHeader(state)
        specialHeader?.let { resource ->
            Image(
                painterResource(resource),
                state.result?.let { drawResultTitle(it, modalityName(state.modality)) },
                Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
        Row(
            Modifier.fillMaxWidth().height(71.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.width(90.dp).height(71.dp), contentAlignment = Alignment.CenterStart) {
                val trapezoid = loteriasCaixaDSCampaignDrawable(0, modalityName(state.modality), LoteriasCaixaDSLotteryArtwork.TRAPEZE)
                if (trapezoid != null) {
                    Image(loteriasCaixaDSDrawablePainter(trapezoid), null, Modifier.fillMaxSize())
                } else {
                    Canvas(Modifier.fillMaxSize()) {
                        drawPath(
                            Path().apply {
                                moveTo(0f, 0f)
                                lineTo(size.width, 0f)
                                lineTo(size.width * TRAPEZOID_BOTTOM_WIDTH_FRACTION, size.height)
                                lineTo(0f, size.height)
                                close()
                            },
                            lotteryStyle.dark
                        )
                    }
                }
                Image(loteriasCaixaDSLotteryClover(state.modality ?: "megasena"), null, Modifier.padding(start = 24.dp).size(23.dp))
            }
            Text(
                state.result?.let { drawResultTitle(it, modalityName(state.modality)) }?.lowercase()
                    ?: modalityName(state.modality).lowercase(),
                modifier = (Modifier.padding(start = 8.dp)).uiId("results.0091"),
                color = lotteryStyle.content,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = LoteriasCaixaDSFuturaFontFamily,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                )
            )
        }
    }
}

private fun specialResultHeader(state: ResultsUiState): Int? = state.result?.takeIf { it.specialContest }?.let { result ->
    when (state.modality) {
        "megasena" -> R.drawable.legacy_result_mega_virada_dupla

        "quina" -> R.drawable.legacy_result_quina_sao_joao_dupla

        "lotofacil" -> R.drawable.legacy_result_lotofacil_indepedencia_dupla

        "duplasena" -> R.drawable.legacy_result_dupla_pascoa_dupla

        "loteca" -> if (result.specialName.contains("país", true) || result.specialName.contains("pais", true)) {
            R.drawable.legacy_result_loteca_pais_dupla
        } else {
            R.drawable.legacy_result_loteca_copa_dupla
        }

        else -> null
    }
}

private const val TRAPEZOID_BOTTOM_WIDTH_FRACTION = .74f
