package br.gov.caixa.loterias.feature.support.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as LoteriasCaixaDSR
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.layout.loteriasCaixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.feature.support.R as LocalizedR

@Composable
internal fun GameHelpTitle(
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
            val trapezoid =
                loteriasCaixaDSCampaignDrawable(modalityId, modalityName, LoteriasCaixaDSLotteryArtwork.TRAPEZE) ?: when (modalityId) {
                    HELP_QUINA_ID -> LoteriasCaixaDSR.drawable.legacy_help_trapeze_quina
                    HELP_LOTOFACIL_ID -> LoteriasCaixaDSR.drawable.legacy_help_trapeze_lotofacil
                    HELP_LOTOMANIA_ID -> LoteriasCaixaDSR.drawable.legacy_help_trapeze_lotomania
                    HELP_DUPLA_SENA_ID -> LoteriasCaixaDSR.drawable.legacy_help_trapeze_dupla_sena
                    HELP_LOTECA_ID -> LoteriasCaixaDSR.drawable.legacy_help_trapeze_loteca
                    HELP_TIMEMANIA_ID -> LoteriasCaixaDSR.drawable.legacy_help_trapeze_timemania
                    HELP_LOTOGOL_ID -> LoteriasCaixaDSR.drawable.legacy_help_trapeze_lotogol
                    else -> LoteriasCaixaDSR.drawable.legacy_help_trapeze_mega_sena
                }
            Image(loteriasCaixaDSDrawablePainter(trapezoid), null, Modifier.fillMaxSize())
            Image(
                loteriasCaixaDSDrawablePainter(
                    loteriasCaixaDSCampaignDrawable(modalityId, modalityName, LoteriasCaixaDSLotteryArtwork.CLOVER_ON_DARK)
                        ?: LoteriasCaixaDSR.drawable.legacy_help_trevo
                ),
                null,
                Modifier.padding(start = 16.dp, top = 16.dp).size(width = 22.dp, height = 23.dp)
            )
        }
        Row(Modifier.weight(1f).fillMaxSize()) {
            Text(
                title,
                (Modifier.weight(HELP_TITLE_WEIGHT).padding(top = 17.dp)).uiId("help.0004"),
                color = contentColor,
                fontFamily = LoteriasCaixaDSFuturaFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Box(
                Modifier.weight(HELP_CLOSE_WEIGHT).padding(start = 16.dp, top = 18.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Image(
                    painterResource(
                        if (title ==
                            "timemania"
                        ) {
                            LoteriasCaixaDSR.drawable.legacy_help_fechar_verde
                        } else {
                            LoteriasCaixaDSR.drawable.legacy_help_btn_fechar
                        }
                    ),
                    uiStrings.getString(LocalizedR.string.ui_fechar_0f2bd8),
                    Modifier.size(20.dp).focusProperties { canFocus = false }.uiId("help.0005").clickable(onClick = onBack)
                )
            }
        }
    }
}

private const val HELP_QUINA_ID = 3
private const val HELP_LOTOFACIL_ID = 8
private const val HELP_LOTOMANIA_ID = 16
private const val HELP_DUPLA_SENA_ID = 18
private const val HELP_LOTECA_ID = 19
private const val HELP_TIMEMANIA_ID = 20
private const val HELP_LOTOGOL_ID = 14
private const val HELP_TITLE_WEIGHT = .8f
private const val HELP_CLOSE_WEIGHT = .2f
