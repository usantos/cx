package br.gov.caixa.loterias.feature.onboarding.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.onboarding.R

@Composable
internal fun StartupUpdateStatusScreen(
    kind: StartupStatusKind,
    appName: String,
    versionName: String,
    onAction: () -> Unit,
    toolbarIcon: @Composable () -> Unit,
    illustration: @Composable (Modifier) -> Unit,
) {
    BoxWithConstraints(
        Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.White)
            .statusBarsPadding().navigationBarsPadding()
    ) {
        val middle = legacyStatusContentMiddle(maxHeight)
        Row(
            Modifier.fillMaxWidth().height(56.dp)
                .background(LoteriasCaixaDSColorTokens.Blue700)
                .padding(start = 48.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                appName,
                (Modifier.weight(1f)).uiId("start.0038"),
                color = LoteriasCaixaDSColorTokens.White,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            toolbarIcon()
        }
        illustration(Modifier.align(Alignment.TopCenter).offset(y = middle - 90.dp).size(72.dp))
        Box(
            Modifier.align(Alignment.TopCenter).offset(y = middle).width(142.dp).height(1.dp)
                .background(LoteriasCaixaDS.colors.line)
        )
        Text(
            stringResource(kind.message),
            (
                Modifier.align(Alignment.TopCenter).offset(y = middle + 18.dp)
                    .fillMaxWidth().padding(horizontal = 16.dp)
                ).uiId("start.0039"),
            style = TextStyle(
                fontFamily = LoteriasCaixaDSFuturaFontFamily,
                fontSize = 18.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            ),
            minLines = 4,
            maxLines = 4,
            color = LoteriasCaixaDS.colors.teal,
            textAlign = TextAlign.Center
        )
        Text(
            stringResource(R.string.startup_version, versionName),
            (Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp).offset(x = (-13).dp).width(100.dp).height(17.dp))
                .uiId("start.0040"),
            style = TextStyle(fontFamily = LoteriasCaixaDSFuturaFontFamily, fontSize = 14.sp),
            color = LoteriasCaixaDSColorTokens.StatusVersion,
            textAlign = TextAlign.Center
        )
        Box(
            Modifier.align(Alignment.BottomCenter).padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth().height(56.dp).background(LoteriasCaixaDSColorTokens.YellowAction)
                .uiId("start.0041").clickable(enabled = true, role = Role.Button, onClick = onAction),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(kind.actionLabel),
                color = LoteriasCaixaDSColorTokens.White,
                fontFamily = LoteriasCaixaDSFuturaFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.uiId("start.0042")
            )
        }
    }
}
