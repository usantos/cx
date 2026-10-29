package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSMarketplaceFilterClover
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSCheckbox
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.presentation.mapper.ComboTextSize
import br.gov.caixa.loterias.feature.betting.presentation.mapper.comboText
import br.gov.caixa.loterias.feature.betting.presentation.mapper.detailName
import br.gov.caixa.loterias.feature.betting.presentation.mapper.specialEntry

@Composable
internal fun ComboDetailHeader(combo: BetCombo) {
    val resources = LocalResources.current
    val special = combo.specialEntry()?.modality
    val clover = special?.let {
        caixaDSCampaignDrawable(it.id, it.code, CaixaDSLotteryArtwork.CLOVER_ON_LIGHT)
            ?: caixaDSMarketplaceFilterClover(it.id, it.code, selected = false)
    } ?: R.drawable.trevo_fundo_claro_detalhe_combo
    val color = special?.let { caixaDSLotteryStyle(it.id, it.code).listText } ?: CaixaDSColorTokens.ComboDetailHeading
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 20.dp, top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(caixaDSDrawablePainter(clover), null, Modifier.size(20.dp).legacyAutomationId("combo-header-clover"))
            Text(
                combo.detailName(),
                (Modifier.padding(start = 8.dp)).uiId("bet.0072"),
                style = comboText(ComboTextSize.HEADING, FontWeight.SemiBold),
                color = color
            )
        }
        Text(
            combo.description.takeIf(String::isNotBlank) ?: resources.getString(R.string.ui_com_esse_pacote_voce_garante_apostas_e05958) +
                resources.getString(R.string.ui_incluindo_teimosinhas_para_a_semana_dbc3bd),
            (Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp)).uiId("bet.0073"),
            style = comboText(ComboTextSize.BODY),
            color = CaixaDSColorTokens.ComboDescription
        )
        Spacer(Modifier.height(3.dp))
    }
}

@Composable
internal fun ComboNumberPreviewOption(surprise: Boolean, onToggle: () -> Unit) {
    val resources = LocalResources.current
    Column(Modifier.fillMaxWidth().padding(start = 17.dp, end = 17.dp, top = 3.dp, bottom = 36.dp)) {
        Text(
            resources.getString(R.string.ui_os_numeros_dos_jogos_dos_combos_sao_57f5d3) +
                resources.getString(R.string.ui_mas_antes_de_efetuar_o_pagamento_e_p_b8fb7a),
            style = comboText(ComboTextSize.CAPTION),
            color = CaixaDSColorTokens.Black,
            modifier = Modifier.uiId("bet.0074")
        )
        Row(
            Modifier.fillMaxWidth().height(48.dp)
                .uiId("bet.0075").toggleable(value = !surprise, role = Role.Checkbox) { onToggle() }.padding(start = 1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Box(Modifier.clearAndSetSemantics { }) {
                CaixaDSCheckbox(!surprise, null, (Modifier.size(32.dp)).uiId("bet.0076"), color = CaixaDSColorTokens.SocialGraphite)
            }
            Text(
                resources.getString(R.string.ui_quero_ver_os_numeros_escolhidos_ante_376901),
                style = comboText(ComboTextSize.LABEL, family = CaixaDSFuturaFontFamily),
                color = CaixaDSColorTokens.Black,
                modifier = Modifier.uiId("bet.0077")
            )
        }
        Spacer(Modifier.height(360.dp))
    }
}
