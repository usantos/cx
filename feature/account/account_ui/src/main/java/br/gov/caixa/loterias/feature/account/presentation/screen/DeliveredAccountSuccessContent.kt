package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSHorizontalDivider as HorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.account.R as LocalizedR
import br.gov.caixa.loterias.feature.account.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.account.ui.R as DeliveryR

@Composable
fun RenderSuccessScreen(registration: Boolean) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_loterias_caixa_02d076),
        onBack = null,
        gradientTopBar = false,
        topBarActionWidth = 16.dp,
        customTopBar = if (registration) null else ({ PersonalDataSuccessHeader() }),
        containerColor = LoteriasCaixaDS.colors.surface,
        modifier = Modifier.uiId("account.0009")
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding).background(
                Brush.horizontalGradient(
                    listOf(LoteriasCaixaDSColorTokens.RegistrationGradientStart, LoteriasCaixaDSColorTokens.RegistrationGradientEnd)
                )
            ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.size(64.dp).background(LoteriasCaixaDSColorTokens.RegistrationCheckBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(DeliveryR.drawable.legacy_check_success),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Text(
                    text = legacySuccessTitle(registration),
                    modifier = (
                        Modifier.padding(top = 17.dp, bottom = 8.dp)
                            .then(if (registration) Modifier.padding(horizontal = 32.dp) else Modifier.heightIn(min = 32.dp))
                        )
                        .uiId("account.0010"),
                    color = LoteriasCaixaDSColorTokens.White,
                    fontSize = 19.sp,
                    style = TextStyle(fontFamily = LoteriasCaixaDSFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(
                    (Modifier.fillMaxWidth().padding(horizontal = 16.dp)).uiId("account.0011"),
                    thickness = 1.dp,
                    color = LoteriasCaixaDSColorTokens.TealAction
                )
                Text(
                    if (registration) {
                        uiStrings.getString(DeliveryR.string.ui_aguarde_apenas_um_instante_enquanto_7fde7c)
                    } else {
                        uiStrings.getString(DeliveryR.string.ui_aguarde_apenas_um_instante_enquanto_369064)
                    },
                    modifier = (Modifier.padding(top = 12.dp, start = 26.dp, end = 26.dp)).uiId("account.0012"),
                    color = LoteriasCaixaDSColorTokens.White,
                    fontSize = 16.sp,
                    style = TextStyle(
                        fontFamily = LoteriasCaixaDSFontFamily,
                        lineHeight = 20.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = true)
                    ),
                    minLines = 3,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PersonalDataSuccessHeader() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSStatusBarColor(
        LoteriasCaixaDSColorTokens.Blue700,
        darkIcons = false
    )
    Box(
        Modifier.fillMaxWidth().background(LoteriasCaixaDSColorTokens.Blue700).statusBarsPadding()
            .height(LoteriasCaixaDS.sizes.topBar),
        contentAlignment = Alignment.Center
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_loterias_caixa_02d076),
            color = LoteriasCaixaDSColorTokens.White,
            fontSize = 20.sp,
            fontFamily = LoteriasCaixaDSFontFamily,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.uiId("account.0013")
        )
        Image(
            painterResource(DeliveryR.drawable.legacy_confirmation_brand),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 21.dp).size(30.dp)
        )
    }
}

private fun legacySuccessTitle(registration: Boolean): AnnotatedString = buildAnnotatedString {
    if (registration) {
        withStyle(SpanStyle(fontFamily = LoteriasCaixaDSFuturaFontFamily, fontWeight = FontWeight.Bold)) { append("Excelente") }
        append(messageText("seu_cadastro_foi_realizado_com_s_23c238"))
    } else {
        withStyle(
            SpanStyle(
                fontFamily = LoteriasCaixaDSFuturaFontFamily,
                fontWeight = FontWeight.Bold
            )
        ) { append(messageText("seu_cadastro_3cf9f3")) }
        append(messageText("foi_atualizado_com_sucesso_6508c4"))
    }
}
