package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.responsiblegaming.R as FeatureR
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState

@Composable
internal fun AssessmentStartScreen(state: ResponsibleGamingUiState, onAction: (ResponsibleGamingAction) -> Unit) {
    // A introdução legada consome o retorno do sistema; o botão opcional Fechar encerra explicitamente.
    BackHandler {}
    Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.White).statusBarsPadding().navigationBarsPadding()) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.horizontalGradient(listOf(CaixaDS.colors.teal, CaixaDS.colors.brand))
            )
        ) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    Modifier.weight(1f).fillMaxWidth().uiId("rg.0041").verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(40.dp))
                    Image(
                        painter = painterResource(R.drawable.ic_loterias_caixa),
                        contentDescription = stringResource(FeatureR.string.assessment_logo_description),
                        modifier = Modifier.width(138.dp).height(134.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.height(20.dp))
                    Image(
                        caixaDSDrawablePainter(R.drawable.ic_volantes),
                        null,
                        Modifier.width(180.dp).height(109.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        stringResource(FeatureR.string.assessment_welcome),
                        modifier = (Modifier.fillMaxWidth().padding(top = 40.dp)).uiId("rg.0042"),
                        color = CaixaDSColorTokens.White,
                        fontFamily = CaixaDSRegularFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
                    )
                    Text(
                        stringResource(FeatureR.string.assessment_introduction),
                        (Modifier.fillMaxWidth().padding(start = 40.dp, top = 30.dp, end = 40.dp)).uiId("rg.0043"),
                        color = CaixaDSColorTokens.White,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
                    )
                }
                WhiteOutlineButton(
                    stringResource(FeatureR.string.assessment_start),
                    { onAction(ResponsibleGamingAction.Start) },
                    (
                        Modifier.fillMaxWidth()
                            .padding(start = 55.dp, end = 55.dp, top = 8.dp, bottom = if (state.assessmentRequired) 65.dp else 7.dp)
                        )
                        .uiId("rg.0044")
                )
                if (!state.assessmentRequired) {
                    WhiteOutlineButton(
                        stringResource(FeatureR.string.assessment_close),
                        { onAction(ResponsibleGamingAction.Back) },
                        (
                            Modifier.fillMaxWidth()
                                .padding(start = 55.dp, end = 55.dp, bottom = 8.dp)
                            ).uiId("rg.0045")
                    )
                }
            }
        }
    }
}

@Composable
private fun WhiteOutlineButton(text: String, onClick: () -> Unit, modifier: Modifier) {
    Button(
        onClick = onClick,
        modifier = (modifier.heightIn(min = 50.dp)).uiId("rg.0046"),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.dp, CaixaDSColorTokens.White),
        colors = ButtonDefaults.buttonColors(containerColor = CaixaDSColorTokens.Transparent, contentColor = CaixaDSColorTokens.White)
    ) {
        Text(
            text,
            (Modifier.fillMaxWidth()).uiId("rg.0047"),
            fontFamily = CaixaDSFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
        )
    }
}
