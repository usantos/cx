package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryKey
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplaceDetailModalityLabel

@Composable
internal fun DetailHeader(detail: MarketplacePoolDetail?, colors: MarketplaceColors, onBack: () -> Unit, specialName: String? = null) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val special = detail?.contestType == 2
    val key = caixaDSLotteryKey(0, detail?.modality.orEmpty())
    val artwork = detailHeaderArtwork(special, key, specialName)
    val label = if (special) {
        when (key) {
            "MEGASENA" -> uiStrings.getString(LocalizedR.string.ui_mega_da_virada_4113fa)
            "QUINA" -> uiStrings.getString(LocalizedR.string.ui_quina_de_sao_joao_a3337a)
            "LOTOFACIL" -> uiStrings.getString(LocalizedR.string.ui_lotofacil_da_independencia_5a2372)
            "DUPLASENA" -> uiStrings.getString(LocalizedR.string.ui_dupla_de_pascoa_68c9b0)
            "LOTECA" -> uiStrings.getString(LocalizedR.string.ui_loteca_especial_29c766)
            else -> detail?.modality?.let(::marketplaceDetailModalityLabel)
        }
    } else {
        detail?.modality?.let(::marketplaceDetailModalityLabel)
    }
    Box(Modifier.fillMaxWidth().height(52.dp).background(colors.light)) {
        artwork?.let { Image(painterResource(it), null, Modifier.matchParentSize(), contentScale = ContentScale.FillBounds) }
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.width(44.dp).height(48.dp).uiId("pool.0053").clickable(role = Role.Button, onClick = onBack),
                Alignment.CenterEnd
            ) {
                Icon(
                    CaixaDSIcons.Back,
                    uiStrings.getString(LocalizedR.string.ui_voltar_59dc92),
                    (Modifier.padding(end = 8.dp).size(12.dp, 18.dp)).uiId("pool.0054"),
                    tint = colors.content
                )
            }
            val specialLabel = specialName?.takeIf(String::isNotBlank)?.let {
                if (key == "LOTECA") it.replace('\n', ' ') else it
            }
            val titleStyle = detailText(18, FontWeight.Bold).copy(fontFamily = CaixaDSFuturaFontFamily)
            Text(
                if (special) {
                    specialLabel ?: label ?: uiStrings.getString(
                        LocalizedR.string.ui_bolao_99927e
                    )
                } else {
                    label ?: uiStrings.getString(LocalizedR.string.ui_bolao_99927e)
                },
                (Modifier.weight(1f).padding(start = 3.dp)).uiId("pool.0055"),
                style = if (special && key == "LOTOFACIL") titleStyle.copy(fontSize = titleStyle.fontSize * .8f) else titleStyle,
                color = colors.content
            )
            if (detail != null) {
                HeaderDatum(
                    stringResource(LocalizedR.string.ui_concurso_1997f5),
                    detail.contest.toString(),
                    colors.content,
                    (Modifier.align(Alignment.Top)).uiId("pool.0056")
                )
                HeaderDatum(
                    if (detail.modality.contains("LOTECA", true)) "Resultado" else "Sorteio",
                    detail.drawDate.ifBlank { detail.drawDateAndTime }.take(5),
                    colors.content,
                    (Modifier.align(Alignment.Top))
                        .uiId("pool.0057")
                )
            }
        }
    }
}

@Composable
private fun HeaderDatum(label: String, value: String, color: Color, modifier: Modifier) {
    Column(modifier.padding(end = 8.dp, top = 4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = detailText(14), color = color, modifier = Modifier.uiId("pool.0058"))
        Text(value, style = detailText(12, FontWeight.Bold), color = color, modifier = Modifier.uiId("pool.0059"))
    }
}

private fun detailHeaderArtwork(special: Boolean, key: String?, specialName: String?): Int? = if (special) {
    when (key) {
        "MEGASENA" -> R.drawable.legacy_campaign_mega_virada_simples

        "QUINA" -> R.drawable.legacy_marketplace_quina_sao_joao_simples

        "LOTOFACIL" -> R.drawable.legacy_marketplace_lotofacil_indepedencia_simples

        "DUPLASENA" -> R.drawable.legacy_marketplace_dupla_pascoa_simples

        "LOTECA" -> if (specialName?.contains("pais", true) == true || specialName?.contains("país", true) == true) {
            R.drawable.legacy_marketplace_cabecalho_loteca_pais_detalhe_simples
        } else {
            R.drawable.legacy_marketplace_loteca_copa_simples
        }

        else -> null
    }
} else {
    null
}
