package br.gov.caixa.loterias.core.designsystem.components.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import kotlin.math.roundToInt

/** Cabeçalho personalizado de modalidade: recebe apenas valores visuais, sem dependências de concurso ou negócio. */
@Composable
fun CaixaDSModalityTopBar(
    title: String,
    backgroundColor: Color,
    contentColor: Color,
    contestNumber: String,
    drawLabel: String,
    drawDate: String,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    backgroundArtwork: Int? = null,
    titleScale: Float = 1f,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).statusBarsPadding()) {
        Box(Modifier.fillMaxWidth().background(if (backgroundArtwork == null) backgroundColor else CaixaDSColorTokens.White)) {
            if (backgroundArtwork != null) {
                Image(painterResource(backgroundArtwork), null, Modifier.matchParentSize(), contentScale = ContentScale.FillBounds)
            }
            // SimulaActivity/LotecaActivity usam cabecalho_simula, não o cabeçalho de confirmação.
            Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
                HeaderIcon(
                    R.drawable.caixads_modality_back,
                    stringResource(LocalizedR.string.format_back),
                    contentColor,
                    onBack,
                    (Modifier.size(48.dp)).uiId("ds.0116")
                )
                Text(
                    title,
                    (Modifier.weight(1f).padding(end = 24.dp).semantics { heading() }).uiId("ds.0117"),
                    style = headerText(16, FontWeight.Bold, title = true, scale = titleScale),
                    color = contentColor
                )
                HeaderDatum(
                    stringResource(LocalizedR.string.format_contest),
                    contestNumber,
                    contentColor,
                    (Modifier.align(Alignment.Top)).uiId("ds.0118"),
                    endPadding = 10
                )
                HeaderDatum(
                    drawLabel,
                    drawDate,
                    contentColor,
                    (Modifier.align(Alignment.Top)).uiId("ds.0119")
                )
                HeaderIcon(
                    R.drawable.caixads_modality_help,
                    uiStrings.getString(LocalizedR.string.ui_duvidas_10e026),
                    contentColor,
                    onHelp,
                    (Modifier.padding(end = 4.dp).size(48.dp)).uiId("ds.0120")
                )
            }
        }
    }
}

@Composable
private fun HeaderDatum(label: String, value: String, ink: Color, modifier: Modifier, endPadding: Int = 8) {
    Column(
        modifier.padding(top = 6.dp, end = endPadding.dp).semantics(mergeDescendants = true) {
            contentDescription = "$label $value"
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = ink, style = headerText(14), modifier = Modifier.uiId("ds.0121"))
        Text(value, color = ink, style = headerText(12, FontWeight.Bold), modifier = Modifier.uiId("ds.0122"))
    }
}

@Composable
private fun headerText(size: Int, weight: FontWeight = FontWeight.Normal, title: Boolean = false, scale: Float = 1f): TextStyle {
    val density = LocalDensity.current
    // Dimensões XML são arredondadas para pixels antes da redução percentual do título especial.
    val fontSize = with(density) { (size.sp.toPx().roundToInt() * scale).toSp() }
    return TextStyle(
        fontFamily = if (title) CaixaDSFuturaFontFamily else CaixaDSFontFamily,
        fontSize = fontSize,
        fontWeight = weight,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    )
}

@Composable
private fun HeaderIcon(resource: Int, description: String, tint: Color, onClick: () -> Unit, modifier: Modifier) {
    Box(
        modifier.uiId("ds.0123").clickable(onClick = onClick).semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Icon(painterResource(resource), null, tint = tint, modifier = Modifier.uiId("ds.0124"))
    }
}
