package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR

@Composable
internal fun FavoriteDialogHeading(focusRequester: FocusRequester) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        text = uiStrings.getString(LocalizedR.string.ui_favoritar_aposta_a34b1e),
        modifier = (
            Modifier.fillMaxWidth()
                .focusRequester(focusRequester)
                .focusable()
                .semantics { heading() }
            ).uiId("bet.0021"),
        color = LoteriasCaixaDSColorTokens.ComboDark,
        style = LoteriasCaixaDS.typography.bodyLarge.copy(
            fontFamily = LoteriasCaixaDSRegularFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = TextStyle.Default.lineHeight,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    )
    Box(
        Modifier.fillMaxWidth().padding(top = 17.dp).height(1.dp)
            .background(LoteriasCaixaDSColorTokens.SimulationMuted)
    )
    Text(
        text = uiStrings.getString(LocalizedR.string.ui_informe_um_nome_para_salvar_sua_apos_2c27af),
        modifier = (Modifier.fillMaxWidth().padding(top = 16.dp)).uiId("bet.0022"),
        color = LoteriasCaixaDSColorTokens.SocialGraphite,
        style = LoteriasCaixaDS.typography.bodyLarge.copy(
            fontFamily = LoteriasCaixaDSRegularFontFamily,
            fontSize = 16.sp,
            lineHeight = TextStyle.Default.lineHeight,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    )
}
