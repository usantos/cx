package br.gov.caixa.loterias.feature.support.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as CaixaDSR
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.support.R as LocalizedR
import br.gov.caixa.loterias.feature.support.presentation.components.gameHelpDate
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState

@Composable
internal fun GameHelpScreen(state: SupportUiState, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val modalityId = state.gameHelpModalityId
    val modalityName = state.gameHelpModalityName
    val lotteryStyle = caixaDSLotteryStyle(modalityId, modalityName)
    val headerDark = when (modalityId) {
        8 -> CaixaDSColorTokens.LotomaniaDark

        // AjudaActivity usa esta cor para Lotofácil.
        14 -> CaixaDSColorTokens.LotogolDark

        else -> lotteryStyle.dark
    }
    val headerLight = when (modalityId) {
        16 -> CaixaDSColorTokens.HelpLotomania
        20 -> CaixaDSColorTokens.HelpTimemania
        19 -> CaixaDSColorTokens.HelpLoteca
        14 -> CaixaDSColorTokens.HelpLotogol
        else -> lotteryStyle.light
    }
    val title = modalityName.ifBlank { "Mega-Sena" }.lowercase()
    Column(
        modifier.fillMaxSize().background(headerDark).statusBarsPadding()
            .legacyAutomationId("support_game_help")
    ) {
        GameHelpTitle(modalityId, modalityName, title, headerLight, lotteryStyle.content, onBack)
        Column(
            Modifier
                .fillMaxSize()
                .background(CaixaDS.colors.graphite)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(157.dp)
                    .background(CaixaDS.colors.gameHelpHeader)
            ) {
                HelpMetadata(
                    stringResource(LocalizedR.string.format_concurso),
                    state.gameHelpContestNumber?.let { "Nº $it" } ?: uiStrings.getString(LocalizedR.string.ui_nao_informado_01af1e),
                    "Abertura",
                    uiStrings.getString(LocalizedR.string.ui_nao_informada_c6f460),
                    (Modifier.weight(1f).padding(start = 38.dp, top = 40.dp)).uiId("help.0006")
                )
                HelpMetadata(
                    uiStrings.getString(LocalizedR.string.ui_proximo_sorteio_a5035e),
                    gameHelpDate(state.gameHelpDrawDate),
                    "Encerramento",
                    gameHelpDate(state.gameHelpClosingDateTime, includeTime = true),
                    (Modifier.weight(1f).padding(start = 38.dp, top = 40.dp)).uiId("help.0007")
                )
            }
            GameHelpRules(state.htmlContent)
        }
    }
}

@Composable
private fun HelpMetadata(firstLabel: String, firstValue: String, secondLabel: String, secondValue: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        val style = TextStyle(
            fontFamily = CaixaDSRegularFontFamily,
            fontSize = 14.2.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
        Text(
            firstLabel,
            color = CaixaDS.colors.onBrand,
            style = style.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .uiId("help.0011")
        )
        Text(firstValue, color = CaixaDS.colors.onBrand, style = style, modifier = Modifier.uiId("help.0012"))
        Text(
            secondLabel,
            color = CaixaDS.colors.onBrand,
            style = style.copy(fontWeight = FontWeight.Bold),
            modifier = (Modifier.padding(top = 21.dp)).uiId("help.0013")
        )
        Text(secondValue, color = CaixaDS.colors.onBrand, style = style, modifier = Modifier.uiId("help.0014"))
    }
}

@Composable
private fun GameHelpTitle(
    modalityId: Int,
    modalityName: String,
    title: String,
    headerLight: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onBack: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().height(56.dp).background(headerLight)
    ) {
        Box(Modifier.width(72.dp).fillMaxSize(), contentAlignment = Alignment.CenterStart) {
            val trapezoid = caixaDSCampaignDrawable(modalityId, modalityName, CaixaDSLotteryArtwork.TRAPEZE) ?: when (modalityId) {
                3 -> CaixaDSR.drawable.legacy_help_trapeze_quina
                8 -> CaixaDSR.drawable.legacy_help_trapeze_lotofacil
                16 -> CaixaDSR.drawable.legacy_help_trapeze_lotomania
                18 -> CaixaDSR.drawable.legacy_help_trapeze_dupla_sena
                19 -> CaixaDSR.drawable.legacy_help_trapeze_loteca
                20 -> CaixaDSR.drawable.legacy_help_trapeze_timemania
                14 -> CaixaDSR.drawable.legacy_help_trapeze_lotogol
                else -> CaixaDSR.drawable.legacy_help_trapeze_mega_sena
            }
            Image(caixaDSDrawablePainter(trapezoid), null, Modifier.fillMaxSize())
            Image(
                caixaDSDrawablePainter(
                    caixaDSCampaignDrawable(modalityId, modalityName, CaixaDSLotteryArtwork.CLOVER_ON_DARK)
                        ?: CaixaDSR.drawable.legacy_help_trevo
                ),
                null,
                Modifier.padding(start = 16.dp, top = 16.dp).size(width = 22.dp, height = 23.dp)
            )
        }
        Row(Modifier.weight(1f).fillMaxSize()) {
            Text(
                title,
                (Modifier.weight(.8f).padding(top = 17.dp)).uiId("help.0004"),
                color = contentColor,
                fontFamily = CaixaDSFuturaFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Box(
                Modifier.weight(.2f).padding(start = 16.dp, top = 18.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Image(
                    painterResource(
                        if (title ==
                            "timemania"
                        ) {
                            CaixaDSR.drawable.legacy_help_fechar_verde
                        } else {
                            CaixaDSR.drawable.legacy_help_btn_fechar
                        }
                    ),
                    uiStrings.getString(LocalizedR.string.ui_fechar_0f2bd8),
                    Modifier.size(20.dp).focusProperties { canFocus = false }.uiId("help.0005").clickable(onClick = onBack)
                )
            }
        }
    }
}

@Composable
private fun GameHelpRules(htmlContent: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(bottom = 39.dp)
            .uiId("help.0008").verticalScroll(rememberScrollState())
            .padding(horizontal = 36.dp)
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_regras_do_jogo_e96ce2),
            color = CaixaDS.colors.onBrand,
            style = TextStyle(
                fontFamily = CaixaDSRegularFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.2.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            ),
            modifier = Modifier.uiId("help.0009")
        )
        Text(
            AnnotatedString.fromHtml(htmlContent),
            color = CaixaDS.colors.onBrand,
            style = TextStyle(
                fontFamily = CaixaDSRegularFontFamily,
                fontSize = 14.2.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true),
                lineBreak = LineBreak.Paragraph
            ),
            modifier = (Modifier.padding(top = 14.2.dp, bottom = 39.dp)).uiId("help.0010")
        )
    }
}
