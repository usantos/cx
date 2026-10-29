package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.mapper.locationActionLabel
import br.gov.caixa.loterias.feature.retail.presentation.mapper.locationMessage
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateStatus
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateUiState

@Composable
internal fun LocationGateScreen(
    state: LocationGateUiState,
    versionName: String,
    @DrawableRes lotteryIconResourceId: Int,
    onAction: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    BoxWithConstraints(Modifier.fillMaxSize().background(CaixaDS.colors.surface).statusBarsPadding().navigationBarsPadding()) {
        LocationGateTitle(lotteryIconResourceId)
        BoxWithConstraints(Modifier.fillMaxSize()) {
            Icon(
                painterResource(R.drawable.legacy_location),
                contentDescription = null,
                modifier = (Modifier.align(Alignment.Center).offset(y = (-54).dp).size(72.dp)).uiId("retail.0049"),
                tint = Color.Unspecified
            )
            Box(
                Modifier.align(Alignment.Center).width(142.dp).height(1.dp)
                    .background(CaixaDS.colors.line)
            )
            when (state.status) {
                LocationGateStatus.CHECKING -> CircularProgressIndicator(
                    Modifier.align(Alignment.Center).offset(y = 80.dp),
                    color = CaixaDS.colors.teal
                )

                else -> Text(
                    locationMessage(state.status),
                    (Modifier.align(Alignment.TopCenter).offset(y = maxHeight / 2 + 18.dp).padding(horizontal = 16.dp)).uiId("retail.0050"),
                    color = CaixaDSColorTokens.TealAction,
                    fontFamily = CaixaDSFuturaFontFamily,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    minLines = 4,
                    maxLines = 4
                )
            }
            Text(
                uiStrings.getString(LocalizedR.string.ui_v_1_s_7ec02d, versionName),
                (Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)).uiId("retail.0051"),
                color = CaixaDSColorTokens.StatusVersion,
                fontFamily = CaixaDSFuturaFontFamily,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            locationActionLabel(state.status)?.let { label ->
                Button(
                    onClick = onAction,
                    modifier = (
                        Modifier.align(
                            Alignment.BottomCenter
                        ).fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp).height(56.dp)
                        ).uiId("retail.0052"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CaixaDSColorTokens.YellowAction,
                        contentColor = CaixaDSColorTokens.White
                    ),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        label,
                        fontFamily = CaixaDSFuturaFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .uiId("retail.0053")
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationGateTitle(@DrawableRes lotteryIconResourceId: Int) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().background(
            CaixaDSColorTokens.Blue700
        ).height(56.dp).padding(start = 32.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_loterias_caixa_267b9d),
            (Modifier.weight(1f)).uiId("retail.0048"),
            color = CaixaDSColorTokens.White,
            fontSize = 16.sp,
            fontFamily = CaixaDSFuturaFontFamily,
            fontWeight = FontWeight.Bold
        )
        if (lotteryIconResourceId != 0) {
            Image(
                painterResource(lotteryIconResourceId),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}
