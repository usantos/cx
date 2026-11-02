package br.gov.caixa.loterias.feature.onboarding.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSSemiboldFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.onboarding.R

@Composable
internal fun StartupUnavailableScreen(
    kind: StartupStatusKind,
    appName: String,
    versionName: String,
    onAction: () -> Unit,
    toolbarIcon: @Composable () -> Unit,
    loading: Boolean,
) {
    Box(Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.White)) {
        Box(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                .background(LoteriasCaixaDSColorTokens.UnavailableBlue).legacyAutomationId("startupUnavailableContent")
        ) {
            Column(Modifier.fillMaxSize()) {
                Box(
                    Modifier.fillMaxWidth().height(64.dp)
                        .background(LoteriasCaixaDSColorTokens.UnavailableBlue)
                ) {
                    Text(
                        text = appName,
                        modifier = (Modifier.align(Alignment.Center)).uiId("start.0035"),
                        color = LoteriasCaixaDSColorTokens.White,
                        fontFamily = LoteriasCaixaDSFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Box(
                        Modifier.align(Alignment.CenterEnd).padding(end = 24.dp).size(24.dp),
                        contentAlignment = Alignment.Center
                    ) { toolbarIcon() }
                }

                Image(
                    painter = painterResource(R.drawable.app_indisponivel),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(top = 136.dp)
                        .size(width = 202.dp, height = 127.dp),
                    contentScale = ContentScale.Fit
                )

                Column(
                    Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(kind.message),
                        color = LoteriasCaixaDSColorTokens.White,
                        fontFamily = LoteriasCaixaDSSemiboldFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 37.sp,
                        textAlign = TextAlign.Center,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                        modifier = Modifier
                            .uiId("start.0036")
                    )
                    kind.detailMessage?.let { detail ->
                        Box(
                            Modifier.padding(top = 16.dp).width(276.dp).height(1.dp)
                                .background(LoteriasCaixaDSColorTokens.White.copy(alpha = 0.278f))
                        )
                        Text(
                            text = stringResource(detail),
                            modifier = (Modifier.padding(top = 16.dp)).uiId("start.0037"),
                            color = LoteriasCaixaDSColorTokens.White,
                            fontFamily = LoteriasCaixaDSFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 28.5.sp,
                            textAlign = TextAlign.Center,
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                StartupUnavailableFooter(kind, versionName, onAction, loading)
            }
        }
    }
}

@Composable
private fun StartupUnavailableFooter(kind: StartupStatusKind, versionName: String, onAction: () -> Unit, loading: Boolean) {
    Column(
        Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(44.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(LoteriasCaixaDSColorTokens.Orange600)
                .uiId("start.0043").clickable(enabled = !loading, role = Role.Button, onClick = onAction)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.width(16.dp))
            Image(
                painter = painterResource(R.drawable.ic_refresh),
                contentDescription = null,
                modifier = Modifier.size(20.dp).legacyAutomationId("startupRetryIcon")
            )
            Text(
                text = stringResource(if (loading) R.string.startup_retrying else kind.actionLabel),
                color = LoteriasCaixaDSColorTokens.White,
                fontFamily = LoteriasCaixaDSSemiboldFontFamily,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                // TextView do XML possui métricas naturais da CAIXA, incluindo espaçamento da fonte.
                // Um estilo novo evita herdar a altura de linha de 24sp do Material.
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                modifier = Modifier.uiId("start.0044")
            )
        }
        Text(
            text = stringResource(R.string.startup_version, versionName),
            modifier = (Modifier.padding(top = 36.dp)).uiId("start.0045"),
            color = LoteriasCaixaDSColorTokens.White,
            fontFamily = LoteriasCaixaDSFontFamily,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
        )
        Text(
            text = stringResource(R.string.startup_error_code, kind.errorCode.orEmpty()),
            color = LoteriasCaixaDSColorTokens.White,
            fontFamily = LoteriasCaixaDSFontFamily,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
            modifier = Modifier.uiId("start.0046")
        )
    }
}
