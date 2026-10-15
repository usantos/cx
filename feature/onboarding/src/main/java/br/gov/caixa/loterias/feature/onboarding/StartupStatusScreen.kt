package br.gov.caixa.loterias.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacySemiboldFontFamily

internal fun legacyStatusContentMiddle(availableHeight: androidx.compose.ui.unit.Dp): androidx.compose.ui.unit.Dp =
    availableHeight / 2

/** Production unavailable screen follows activity_app_indisponivel_novo in the CX app. */
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
            illustration = illustration,
        )
    } else {
        StartupUnavailableScreen(
            kind = kind,
            appName = appName,
            versionName = versionName,
            onAction = onAction,
            toolbarIcon = toolbarIcon,
        )
    }
    if (loading) CaixaDSAuthenticationLoadingDialog()
}

@Composable
private fun StartupUnavailableScreen(
    kind: StartupStatusKind,
    appName: String,
    versionName: String,
    onAction: () -> Unit,
    toolbarIcon: @Composable () -> Unit,
) {
    Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.White)) {
      Box(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
            .background(CaixaDSColorTokens.LegacyUnavailableBlue).testTag("startupUnavailableContent"),
      ) {
        Column(Modifier.fillMaxSize()) {
            Box(
                Modifier.fillMaxWidth().height(64.dp)
                    .background(CaixaDSColorTokens.LegacyUnavailableBlue),
            ) {
                Text(
                    text = appName,
                    modifier = Modifier.align(Alignment.Center),
                    color = CaixaDSColorTokens.White,
                    fontFamily = CaixaDSFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )
                Box(
                    Modifier.align(Alignment.CenterEnd).padding(end = 24.dp).size(24.dp),
                    contentAlignment = Alignment.Center,
                ) { toolbarIcon() }
            }

            Image(
                painter = painterResource(R.drawable.app_indisponivel),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .padding(top = 136.dp)
                    .size(width = 202.dp, height = 127.dp),
                contentScale = ContentScale.Fit,
            )

            Column(
                Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = kind.message,
                    color = CaixaDSColorTokens.White,
                    fontFamily = CaixaDSLegacySemiboldFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 37.sp,
                    textAlign = TextAlign.Center,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                )
                kind.detailMessage?.let { detail ->
                    Box(
                        Modifier.padding(top = 16.dp).width(276.dp).height(1.dp)
                            .background(CaixaDSColorTokens.White.copy(alpha = 0.278f)),
                    )
                    Text(
                        text = detail,
                        modifier = Modifier.padding(top = 16.dp),
                        color = CaixaDSColorTokens.White,
                        fontFamily = CaixaDSFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 28.5.sp,
                        textAlign = TextAlign.Center,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Column(
                Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(CaixaDSColorTokens.Orange600)
                        .clickable(role = Role.Button, onClick = onAction)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Spacer(Modifier.width(16.dp))
                    Image(
                        painter = painterResource(R.drawable.ic_refresh),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp).testTag("startupRetryIcon"),
                    )
                    Text(
                        text = kind.actionLabel,
                        color = CaixaDSColorTokens.White,
                        fontFamily = CaixaDSLegacySemiboldFontFamily,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        // XML TextView has natural CAIXA metrics, including font padding.
                        // A fresh style avoids inheriting Material's 24sp line height.
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    )
                }
                Text(
                    text = startupStatusVersion(versionName),
                    modifier = Modifier.padding(top = 36.dp),
                    color = CaixaDSColorTokens.White,
                    fontFamily = CaixaDSFontFamily,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                )
                Text(
                    text = "Código do erro: LTR-503",
                    color = CaixaDSColorTokens.White,
                    fontFamily = CaixaDSFontFamily,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                )
            }
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
    BoxWithConstraints(Modifier.fillMaxSize().background(CaixaDSColorTokens.White)
        .statusBarsPadding().navigationBarsPadding()) {
        val middle = legacyStatusContentMiddle(maxHeight)
        Row(Modifier.fillMaxWidth().height(56.dp)
            .background(Brush.horizontalGradient(listOf(CaixaDS.colors.legacyTeal, CaixaDS.colors.brand)))
            .padding(start = 48.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(appName, Modifier.weight(1f), color = CaixaDSColorTokens.White,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            toolbarIcon()
        }
        illustration(Modifier.align(Alignment.TopCenter).offset(y = middle - 90.dp).size(72.dp))
        Box(Modifier.align(Alignment.TopCenter).offset(y = middle).width(142.dp).height(1.dp)
            .background(CaixaDS.colors.legacyLine))
        Text(kind.message, Modifier.align(Alignment.TopCenter).offset(y = middle + 18.dp)
            .fillMaxWidth().padding(horizontal = 16.dp),
            style = TextStyle(fontFamily = CaixaDSLegacyFuturaFontFamily, fontSize = 18.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)),
            minLines = 4, maxLines = 4, color = CaixaDS.colors.legacyTeal, textAlign = TextAlign.Center)
        Text(startupStatusVersion(versionName),
            Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp).offset(x = (-13).dp).width(100.dp).height(17.dp),
            style = TextStyle(fontFamily = CaixaDSLegacyFuturaFontFamily, fontSize = 14.sp),
            color = CaixaDSColorTokens.LegacyStatusVersion, textAlign = TextAlign.Center)
        Box(Modifier.align(Alignment.BottomCenter).padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .fillMaxWidth().height(56.dp).background(CaixaDSColorTokens.YellowAction)
            .clickable(enabled = true, role = Role.Button, onClick = onAction), contentAlignment = Alignment.Center) {
            Text(kind.actionLabel, color = CaixaDSColorTokens.White,
                fontFamily = CaixaDSLegacyFuturaFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
