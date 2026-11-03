package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSMarketplaceFilterClover
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSCheckbox
import br.gov.caixa.loterias.core.designsystem.layout.loteriasCaixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.presentation.mapper.ComboTextSize
import br.gov.caixa.loterias.feature.betting.presentation.mapper.comboText
import br.gov.caixa.loterias.feature.betting.presentation.mapper.detailName
import br.gov.caixa.loterias.feature.betting.presentation.mapper.specialEntry
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
fun ComboDetailHeader(combo: BetCombo) {
    val resources = LocalResources.current
    val special = combo.specialEntry()?.modality
    val clover = special?.let {
        loteriasCaixaDSCampaignDrawable(it.id, it.code, LoteriasCaixaDSLotteryArtwork.CLOVER_ON_LIGHT)
            ?: loteriasCaixaDSMarketplaceFilterClover(it.id, it.code, selected = false)
    } ?: DeliveryR.drawable.trevo_fundo_claro_detalhe_combo
    val color = special?.let { loteriasCaixaDSLotteryStyle(it.id, it.code).listText } ?: LoteriasCaixaDSColorTokens.ComboDetailHeading
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 20.dp, top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(loteriasCaixaDSDrawablePainter(clover), null, Modifier.size(20.dp).legacyAutomationId("combo-header-clover"))
            Text(
                combo.detailName(),
                (Modifier.padding(start = 8.dp)).uiId("bet.0072"),
                style = comboText(ComboTextSize.HEADING, FontWeight.SemiBold),
                color = color
            )
        }
        Text(
            combo.description.takeIf(String::isNotBlank)
                ?: resources.getString(DeliveryR.string.ui_com_esse_pacote_voce_garante_apostas_e05958) +
                resources.getString(DeliveryR.string.ui_incluindo_teimosinhas_para_a_semana_dbc3bd),
            (Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp)).uiId("bet.0073"),
            style = comboText(ComboTextSize.BODY),
            color = LoteriasCaixaDSColorTokens.ComboDescription
        )
        Spacer(Modifier.height(3.dp))
    }
}

@Composable
fun ComboNumberPreviewOption(surprise: Boolean, onToggle: () -> Unit) {
    val resources = LocalResources.current
    Column(Modifier.fillMaxWidth().padding(start = 17.dp, end = 17.dp, top = 3.dp, bottom = 36.dp)) {
        Text(
            resources.getString(DeliveryR.string.ui_os_numeros_dos_jogos_dos_combos_sao_57f5d3) +
                resources.getString(DeliveryR.string.ui_mas_antes_de_efetuar_o_pagamento_e_p_b8fb7a),
            style = comboText(ComboTextSize.CAPTION),
            color = LoteriasCaixaDSColorTokens.Black,
            modifier = Modifier.uiId("bet.0074")
        )
        Row(
            Modifier.fillMaxWidth().height(48.dp)
                .uiId("bet.0075").toggleable(value = !surprise, role = Role.Checkbox) { onToggle() }.padding(start = 1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Box(Modifier.clearAndSetSemantics { }) {
                LoteriasCaixaDSCheckbox(
                    !surprise,
                    null,
                    (Modifier.size(32.dp)).uiId("bet.0076"),
                    color = LoteriasCaixaDSColorTokens.SocialGraphite
                )
            }
            Text(
                resources.getString(DeliveryR.string.ui_quero_ver_os_numeros_escolhidos_ante_376901),
                style = comboText(ComboTextSize.LABEL, family = LoteriasCaixaDSFuturaFontFamily),
                color = LoteriasCaixaDSColorTokens.Black,
                modifier = Modifier.uiId("bet.0077")
            )
        }
        Spacer(Modifier.height(360.dp))
    }
}
