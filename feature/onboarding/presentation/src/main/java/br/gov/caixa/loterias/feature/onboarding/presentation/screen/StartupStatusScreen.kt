package br.gov.caixa.loterias.feature.onboarding.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSSemiboldFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.onboarding.R

internal fun legacyStatusContentMiddle(availableHeight: androidx.compose.ui.unit.Dp): androidx.compose.ui.unit.Dp = availableHeight / 2

/** A tela de indisponibilidade de produção segue activity_app_indisponivel_novo do aplicativo CX. */
@Composable
fun StartupStatusScreen(
    kind: StartupStatusKind,
    appName: String,
    versionName: String,
    onAction: () -> Unit,
    toolbarIcon: @Composable () -> Unit,
    illustration: @Composable (Modifier) -> Unit,
    loading: Boolean = false,
) {
    if (kind == StartupStatusKind.UPDATE_REQUIRED) {
        StartupUpdateStatusScreen(
            kind = kind,
            appName = appName,
            versionName = versionName,
            onAction = onAction,
            toolbarIcon = toolbarIcon,
            illustration = illustration
        )
    } else {
        StartupUnavailableScreen(
            kind = kind,
            appName = appName,
            versionName = versionName,
            onAction = onAction,
            toolbarIcon = toolbarIcon,
            loading = loading
        )
    }
}

@Composable
private fun StartupUnavailableScreen(
    kind: StartupStatusKind,
    appName: String,
    versionName: String,
    onAction: () -> Unit,
    toolbarIcon: @Composable () -> Unit,
    loading: Boolean,
) {
    Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.White)) {
        Box(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                .background(CaixaDSColorTokens.UnavailableBlue).legacyAutomationId("startupUnavailableContent")
        ) {
            Column(Modifier.fillMaxSize()) {
                Box(
                    Modifier.fillMaxWidth().height(64.dp)
                        .background(CaixaDSColorTokens.UnavailableBlue)
                ) {
                    Text(
                        text = appName,
                        modifier = (Modifier.align(Alignment.Center)).uiId("start.0035"),
                        color = CaixaDSColorTokens.White,
                        fontFamily = CaixaDSFontFamily,
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
                        color = CaixaDSColorTokens.White,
                        fontFamily = CaixaDSSemiboldFontFamily,
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
                                .background(CaixaDSColorTokens.White.copy(alpha = 0.278f))
                        )
                        Text(
                            text = stringResource(detail),
                            modifier = (Modifier.padding(top = 16.dp)).uiId("start.0037"),
                            color = CaixaDSColorTokens.White,
                            fontFamily = CaixaDSFontFamily,
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
private fun StartupUpdateStatusScreen(
    kind: StartupStatusKind,
    appName: String,
    versionName: String,
    onAction: () -> Unit,
    toolbarIcon: @Composable () -> Unit,
    illustration: @Composable (Modifier) -> Unit,
) {
    BoxWithConstraints(
        Modifier.fillMaxSize().background(CaixaDSColorTokens.White)
            .statusBarsPadding().navigationBarsPadding()
    ) {
        val middle = legacyStatusContentMiddle(maxHeight)
        Row(
            Modifier.fillMaxWidth().height(56.dp)
                .background(CaixaDSColorTokens.Blue700)
                .padding(start = 48.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                appName,
                (Modifier.weight(1f)).uiId("start.0038"),
                color = CaixaDSColorTokens.White,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            toolbarIcon()
        }
        illustration(Modifier.align(Alignment.TopCenter).offset(y = middle - 90.dp).size(72.dp))
        Box(
            Modifier.align(Alignment.TopCenter).offset(y = middle).width(142.dp).height(1.dp)
                .background(CaixaDS.colors.line)
        )
        Text(
            stringResource(kind.message),
            (
                Modifier.align(Alignment.TopCenter).offset(y = middle + 18.dp)
                    .fillMaxWidth().padding(horizontal = 16.dp)
                ).uiId("start.0039"),
            style = TextStyle(
                fontFamily = CaixaDSFuturaFontFamily,
                fontSize = 18.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            ),
            minLines = 4,
            maxLines = 4,
            color = CaixaDS.colors.teal,
            textAlign = TextAlign.Center
        )
        Text(
            stringResource(R.string.startup_version, versionName),
            (Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp).offset(x = (-13).dp).width(100.dp).height(17.dp))
                .uiId("start.0040"),
            style = TextStyle(fontFamily = CaixaDSFuturaFontFamily, fontSize = 14.sp),
            color = CaixaDSColorTokens.StatusVersion,
            textAlign = TextAlign.Center
        )
        Box(
            Modifier.align(Alignment.BottomCenter).padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth().height(56.dp).background(CaixaDSColorTokens.YellowAction)
                .uiId("start.0041").clickable(enabled = true, role = Role.Button, onClick = onAction),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(kind.actionLabel),
                color = CaixaDSColorTokens.White,
                fontFamily = CaixaDSFuturaFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.uiId("start.0042")
            )
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
                .background(CaixaDSColorTokens.Orange600)
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
                color = CaixaDSColorTokens.White,
                fontFamily = CaixaDSSemiboldFontFamily,
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
            color = CaixaDSColorTokens.White,
            fontFamily = CaixaDSFontFamily,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
        )
        Text(
            text = stringResource(R.string.startup_error_code, kind.errorCode.orEmpty()),
            color = CaixaDSColorTokens.White,
            fontFamily = CaixaDSFontFamily,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
            modifier = Modifier.uiId("start.0046")
        )
    }
}
