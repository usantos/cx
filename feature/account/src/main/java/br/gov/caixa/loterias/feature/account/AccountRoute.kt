package br.gov.caixa.loterias.feature.account

import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSLotteryMark
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold

@Composable
fun AccountRoute(
    viewModel: AccountStatusViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    ssoAccountUrl: String,
) {
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AccountStatusEffect.Completed -> onComplete()
            }
        }
    }
    LaunchedEffect(viewModel) { viewModel.start() }
    when (viewModel.route) {
        "account/register-confirmation" -> SuccessScreen(registration = true)
        "account/personal-data-confirmation" -> SuccessScreen(registration = false)
        "account/change-login" -> ChangeLoginScreen(ssoAccountUrl, onBack)
        "account/token" -> AccountTokenScreen()
        else -> error("Rota de conta sem layout Compose: ${viewModel.route}")
    }
}

@Composable
private fun SuccessScreen(registration: Boolean) {
    CaixaDSLegacyScaffold(
        title = "Loterias Caixa",
        onBack = null,
        gradientTopBar = false,
        topBarActionWidth = 16.dp,
        topBarTitleContent = {
            Box(Modifier.fillMaxWidth()) {
            Text(
                "Loterias Caixa",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = CaixaDSColorTokens.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                style = TextStyle(fontFamily = CaixaDSFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)),
            )
            CaixaDSLotteryMark(Modifier.align(Alignment.CenterEnd).size(24.dp).padding(5.dp))
            }
        },
        containerColor = CaixaDS.colors.surface,
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding).background(
                Brush.horizontalGradient(listOf(CaixaDSColorTokens.LegacyRegistrationGradientStart, CaixaDSColorTokens.LegacyRegistrationGradientEnd)),
            ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier.size(64.dp).background(CaixaDSColorTokens.LegacyRegistrationCheckBackground, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.legacy_check_success),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                    )
                }
                Text(
                    text = legacySuccessTitle(registration),
                    modifier = Modifier.padding(top = 17.dp, bottom = 8.dp)
                        .then(if (registration) Modifier.padding(horizontal = 32.dp) else Modifier.height(32.dp)),
                    color = CaixaDSColorTokens.White,
                    fontSize = 19.sp,
                    style = TextStyle(fontFamily = CaixaDSFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                HorizontalDivider(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = CaixaDSColorTokens.LegacyTealAction,
                )
                Text(
                    if (registration) {
                        "Aguarde apenas um instante enquanto te redirecionamos para suas apostas."
                    } else {
                        "Aguarde apenas um instante enquanto te redirecionamos para a tela inicial."
                    },
                    modifier = Modifier.padding(top = 12.dp, start = 26.dp, end = 26.dp),
                    color = CaixaDSColorTokens.White,
                    fontSize = 16.sp,
                    style = TextStyle(fontFamily = CaixaDSFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    minLines = 3,
                    maxLines = 3,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private fun legacySuccessTitle(registration: Boolean): AnnotatedString = buildAnnotatedString {
    if (registration) {
        withStyle(SpanStyle(fontFamily = CaixaDSLegacyFuturaFontFamily, fontWeight = FontWeight.Bold)) { append("Excelente") }
        append("! Seu cadastro foi realizado com sucesso!")
    } else {
        withStyle(SpanStyle(fontFamily = CaixaDSLegacyFuturaFontFamily, fontWeight = FontWeight.Bold)) { append("Seu cadastro") }
        append(" foi atualizado com sucesso.")
    }
}

@Composable
private fun ChangeLoginScreen(url: String, onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(url) {
        runCatching { uriHandler.openUri(url) }
    }
    CaixaDSLegacyScaffold(
        title = "Alterar dados Login CAIXA",
        onBack = onBack,
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding).background(CaixaDS.colors.surface).padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "A página segura da Conta CAIXA foi aberta no navegador.",
                color = CaixaDS.colors.legacyPrimaryButton,
                textAlign = TextAlign.Center,
                fontSize = 17.sp,
            )
        }
    }
}
