package br.gov.caixa.loterias.feature.account.presentation.route

import android.content.ActivityNotFoundException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalUriHandler
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
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryMark
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSHorizontalDivider as HorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSPrimaryButton
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.account.R
import br.gov.caixa.loterias.feature.account.R as LocalizedR
import br.gov.caixa.loterias.feature.account.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.account.presentation.screen.AccountTokenScreen
import br.gov.caixa.loterias.feature.account.presentation.state.AccountStatusEffect
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.AccountStatusViewModel

@Composable
fun AccountRoute(viewModel: AccountStatusViewModel, onBack: () -> Unit, onComplete: () -> Unit, ssoAccountUrl: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
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
        "account/token" -> AccountTokenScreen(modifier = Modifier.uiId("account.0008"))
        else -> error(uiStrings.getString(LocalizedR.string.ui_rota_de_conta_sem_layout_compose_1_s_19cdf9, viewModel.route))
    }
}

@Composable
private fun SuccessScreen(registration: Boolean) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_loterias_caixa_02d076),
        onBack = null,
        gradientTopBar = false,
        topBarActionWidth = 16.dp,
        customTopBar = if (registration) null else ({ PersonalDataSuccessHeader() }),
        containerColor = CaixaDS.colors.surface,
        modifier = Modifier.uiId("account.0009")
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding).background(
                Brush.horizontalGradient(listOf(CaixaDSColorTokens.RegistrationGradientStart, CaixaDSColorTokens.RegistrationGradientEnd))
            ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.size(64.dp).background(CaixaDSColorTokens.RegistrationCheckBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.legacy_check_success),
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
                    color = CaixaDSColorTokens.White,
                    fontSize = 19.sp,
                    style = TextStyle(fontFamily = CaixaDSFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true)),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(
                    (Modifier.fillMaxWidth().padding(horizontal = 16.dp)).uiId("account.0011"),
                    thickness = 1.dp,
                    color = CaixaDSColorTokens.TealAction
                )
                Text(
                    if (registration) {
                        uiStrings.getString(LocalizedR.string.ui_aguarde_apenas_um_instante_enquanto_7fde7c)
                    } else {
                        uiStrings.getString(LocalizedR.string.ui_aguarde_apenas_um_instante_enquanto_369064)
                    },
                    modifier = (Modifier.padding(top = 12.dp, start = 26.dp, end = 26.dp)).uiId("account.0012"),
                    color = CaixaDSColorTokens.White,
                    fontSize = 16.sp,
                    style = TextStyle(
                        fontFamily = CaixaDSFontFamily,
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
    br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBarColor(
        CaixaDSColorTokens.Blue700,
        darkIcons = false
    )
    Box(
        Modifier.fillMaxWidth().background(CaixaDSColorTokens.Blue700).statusBarsPadding()
            .height(CaixaDS.sizes.topBar),
        contentAlignment = Alignment.Center
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_loterias_caixa_02d076),
            color = CaixaDSColorTokens.White,
            fontSize = 20.sp,
            fontFamily = CaixaDSFontFamily,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.uiId("account.0013")
        )
        Image(
            painterResource(R.drawable.legacy_confirmation_brand),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 21.dp).size(30.dp)
        )
    }
}

private fun legacySuccessTitle(registration: Boolean): AnnotatedString = buildAnnotatedString {
    if (registration) {
        withStyle(SpanStyle(fontFamily = CaixaDSFuturaFontFamily, fontWeight = FontWeight.Bold)) { append("Excelente") }
        append(messageText("seu_cadastro_foi_realizado_com_s_23c238"))
    } else {
        withStyle(
            SpanStyle(
                fontFamily = CaixaDSFuturaFontFamily,
                fontWeight = FontWeight.Bold
            )
        ) { append(messageText("seu_cadastro_3cf9f3")) }
        append(messageText("foi_atualizado_com_sucesso_6508c4"))
    }
}

@Composable
private fun ChangeLoginScreen(url: String, onBack: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val uriHandler = LocalUriHandler.current
    var opened by rememberSaveable(url) { mutableStateOf<Boolean?>(null) }
    var attempt by remember(url) { mutableIntStateOf(0) }
    LaunchedEffect(url, attempt) {
        if (opened == null) {
            opened = try {
                uriHandler.openUri(url)
                true
            } catch (_: ActivityNotFoundException) {
                false
            } catch (_: SecurityException) {
                false
            } catch (_: IllegalArgumentException) {
                false
            }
        }
    }
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_alterar_dados_login_caixa_c7bbfc),
        onBack = onBack,
        modifier = Modifier.uiId("account.0014")
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding).background(CaixaDS.colors.surface).padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            when (opened) {
                true -> Text(
                    uiStrings.getString(LocalizedR.string.ui_a_pagina_segura_da_conta_caixa_foi_a_9f4f21),
                    color = CaixaDS.colors.primaryButton,
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    modifier = Modifier.uiId("account.0015")
                )

                false -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_abrir_a_pagina_segu_7efc3e),
                        color = CaixaDS.colors.primaryButton,
                        textAlign = TextAlign.Center,
                        fontSize = 17.sp,
                        modifier = Modifier.uiId("account.0016")
                    )
                    Spacer(Modifier.height(16.dp))
                    CaixaDSPrimaryButton(
                        text = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                        onClick = {
                            opened = null
                            attempt++
                        },
                        modifier = Modifier.uiId("account.0017")
                    )
                }

                null -> Unit
            }
        }
    }
}
