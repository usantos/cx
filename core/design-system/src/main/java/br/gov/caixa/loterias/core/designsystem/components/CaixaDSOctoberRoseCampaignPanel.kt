package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

@Composable
fun CaixaDSOctoberRoseRibbon(modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Image(painterResource(R.drawable.legacy_campaign_ribbon), uiStrings.getString(LocalizedR.string.ui_outubro_rosa_1c2c8f), modifier)
}

@Composable
fun CaixaDSOctoberRoseCampaignPanel(descriptionModifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    // Versão legada dca5d9d9: content_principal_menu_rapido_outubro_rosa.xml.
    val legacyTextStyle = TextStyle(
        fontFamily = CaixaDSFontFamily,
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    )
    // Texto de corpo acompanha a escala de fonte do sistema.
    val paragraphStyle = legacyTextStyle
    Box(Modifier.fillMaxWidth().padding(top = 10.dp, start = 22.dp)) {
        Image(
            painter = painterResource(R.drawable.legacy_campaign_watermark),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd).size(width = 154.dp, height = 324.dp),
            contentScale = ContentScale.FillBounds
        )
        Column(
            descriptionModifier.fillMaxWidth().padding(top = 32.dp, end = 34.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_outubro_rosa_1c2c8f),
                color = CaixaDSColorTokens.OctoberRose,
                style = legacyTextStyle.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.End
            )
            Spacer(Modifier.height(16.dp))
            Text(
                buildAnnotatedString {
                    append(uiStrings.getString(LocalizedR.string.ui_campanha_anual_que_conscientiza_sobr_786d3a))
                    withStyle(SpanStyle(color = CaixaDSColorTokens.OctoberRoseDark, fontWeight = FontWeight.Bold)) {
                        append(uiStrings.getString(LocalizedR.string.ui_importancia_da_prevencao_do_diagnost_8972f9))
                    }
                    append(uiStrings.getString(LocalizedR.string.ui_do_cancer_de_mama_e_do_cancer_do_col_f675fd))
                },
                // Padrão de TextView do AppTheme medido no APK original.
                color = androidx.compose.ui.graphics.Color(0x8A000000),
                style = paragraphStyle,
                textAlign = TextAlign.End
            )
            Spacer(Modifier.height(16.dp))
            Text(
                buildAnnotatedString {
                    append(uiStrings.getString(LocalizedR.string.ui_o_ministerio_da_saude_orienta_3_dica_c96da5))
                    withStyle(SpanStyle(color = CaixaDSColorTokens.OctoberRoseDark, fontWeight = FontWeight.Bold)) {
                        append(uiStrings.getString(LocalizedR.string.ui_manter_habitos_saudaveis_ter_atencao_10f959))
                    }
                },
                // Padrão de TextView do AppTheme medido no APK original.
                color = androidx.compose.ui.graphics.Color(0x8A000000),
                style = paragraphStyle,
                textAlign = TextAlign.End
            )
        }
    }
}
