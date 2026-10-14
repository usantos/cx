package br.gov.caixa.loterias.feature.account

import android.app.Activity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.auth.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.showBiometricPrompt
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSLoginSwitch
import androidx.compose.foundation.selection.toggleable
import br.gov.caixa.loterias.core.designsystem.CaixaDSFingerprintIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSLoteriasLogo

@Composable
fun LoginRoute(
    viewModel: LoginViewModel,
    authentication: AuthenticationGateway,
    versionName: String,
    onAuthenticated: (String) -> Unit,
    onTryNow: () -> Unit,
    onSessionInvalidated: () -> Unit,
    authorizeOnEnter: Boolean = false,
    onAuthorizationCancelled: () -> Unit = {},
    environmentSelectorVisible: Boolean = false,
    environmentSelector: @Composable (Boolean) -> Unit = {},
    biometricPrompt: (Activity, android.content.Intent, (android.content.Intent?) -> Unit) -> (() -> Unit) =
        ::showBiometricPrompt,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var automaticAuthorizationConsumed by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if ((result.resultCode == Activity.RESULT_CANCELED && result.data == null) ||
            authentication.isAuthorizationCancelled(result.data)) {
            viewModel.authorizationCancelled()
            onAuthorizationCancelled()
        } else viewModel.complete(result.data)
    }
    var biometricChallenge by rememberSaveable { mutableStateOf<android.content.Intent?>(null) }
    val credentialLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val challenge = biometricChallenge
        biometricChallenge = null
        if (result.resultCode == Activity.RESULT_OK && challenge != null) viewModel.completeBiometric(challenge)
        else {
            viewModel.authorizationCancelled()
            onAuthorizationCancelled()
        }
    }
    val promptHost = LocalView.current.context.findActivity()
    val currentCancellation by androidx.compose.runtime.rememberUpdatedState(onAuthorizationCancelled)
    DisposableEffect(biometricChallenge, promptHost) {
        val challenge = biometricChallenge
        val cancelPrompt = if (android.os.Build.VERSION.SDK_INT >= 29 && challenge != null && promptHost != null) {
            try {
                biometricPrompt(promptHost, challenge) { result ->
                    biometricChallenge = null
                    if (result != null) viewModel.completeBiometric(result)
                    else {
                        viewModel.authorizationCancelled()
                        currentCancellation()
                    }
                }
            } catch (_: Exception) {
                biometricChallenge = null
                viewModel.authorizationLaunchFailed()
                null
            }
        } else null
        onDispose { cancelPrompt?.invoke() }
    }
    val authorize = {
        if (viewModel.beginAuthorization()) {
            try {
                if (viewModel.canUseBiometricLogin()) {
                    checkNotNull(promptHost)
                    val challenge = (authentication as br.gov.caixa.loterias.core.auth.BiometricAuthenticationGateway)
                        .createBiometricAuthorizationIntent()
                    biometricChallenge = challenge
                    if (android.os.Build.VERSION.SDK_INT < 29) credentialLauncher.launch(challenge)
                } else launcher.launch(authentication.createAuthorizationIntent())
            } catch (_: Exception) { viewModel.authorizationLaunchFailed() }
        }
    }
    LaunchedEffect(authorizeOnEnter) {
        if (authorizeOnEnter && !automaticAuthorizationConsumed) {
            automaticAuthorizationConsumed = true
            authorize()
        }
    }
    LaunchedEffect(state.authenticated) {
        if (state.authenticated) onAuthenticated(state.destination)
    }
    LoginScreen(
        state = state,
        versionName = versionName,
        onTryNow = { if (viewModel.enterVisitor()) onTryNow() },
        onLogin = authorize,
        onBiometricChanged = viewModel::setBiometricEnabled,
        environmentSelector = environmentSelector,
        environmentSelectorVisible = environmentSelectorVisible,
    )
    if (state.blockedAccountCheckFailed) {
        br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Não foi possível concluir esta operação. Por favor, tente novamente mais tarde.",
            positiveLabel = "Entendi",
            onPositive = {
                if (viewModel.acknowledgeBlockedAccountFailure()) onSessionInvalidated()
            },
        )
    } else if (state.consultationOnlyNoticePending) {
        br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Usuário habilitado apenas para consultas.",
            positiveLabel = "Entendi",
            onPositive = { viewModel.acknowledgeConsultationOnlyNotice() },
        )
    }
}

@Composable
fun LoginScreen(
    state: LoginUiState,
    versionName: String,
    onTryNow: () -> Unit,
    onLogin: () -> Unit,
    onBiometricChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    environmentSelectorVisible: Boolean = false,
    environmentSelector: @Composable (Boolean) -> Unit = {},
) {
    val view = LocalView.current
    DisposableEffect(view) {
        val activity = view.context.findActivity()
        activity?.let {
            WindowCompat.getInsetsController(it.window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
        onDispose {
            activity?.let {
                WindowCompat.getInsetsController(it.window, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }
    if (state.isLoading || state.blockedAccountCheckFailed || state.consultationOnlyNoticePending) {
        AccountTokenScreen(modifier)
        return
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CaixaDS.colors.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(CaixaDS.colors.authenticationBackground),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(start = CaixaDS.spacing.xl, top = 130.dp, end = CaixaDS.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CaixaDSLoteriasLogo(
                contentDescription = "Logo Loterias CAIXA",
                modifier = Modifier.size(width = 93.dp, height = 95.dp),
            )
            Spacer(Modifier.height(57.dp))
            LegacyLoginButton(
                text = "Experimente",
                onClick = onTryNow,
                outlined = true,
                enabled = !state.isLoading && !state.authorizationPending && !state.blockedAccountCheckFailed,
            )
            Spacer(Modifier.height(42.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.weight(1f).height(1.dp).background(CaixaDS.colors.onBrand))
                Text(
                    text = "OU",
                    modifier = Modifier.padding(horizontal = 6.dp),
                    color = CaixaDS.colors.onBrand,
                    fontFamily = br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                )
                Box(Modifier.weight(1f).height(1.dp).background(CaixaDS.colors.onBrand))
            }
            Spacer(Modifier.height(42.dp))
            LegacyLoginButton(
                text = "Faça login/cadastre-se",
                onClick = onLogin,
                outlined = false,
                enabled = !state.isLoading && !state.authorizationPending && !state.blockedAccountCheckFailed,
                loading = state.isLoading,
            )
            state.errorMessage?.let { message ->
                Text(
                    text = message,
                    modifier = Modifier.padding(top = CaixaDS.spacing.md),
                    color = CaixaDS.colors.onBrand,
                    fontSize = 14.sp,
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 16.dp)
                    .toggleable(value = state.biometricEnabled, role = Role.Switch, onValueChange = onBiometricChanged),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CaixaDSFingerprintIcon(
                    color = CaixaDS.colors.onBrand,
                    modifier = Modifier.size(width = 23.dp, height = 30.dp),
                )
                Text(
                    text = "Acessar com biometria",
                    color = CaixaDS.colors.onBrand,
                    fontSize = 20.sp,
                    maxLines = 1,
                )
                CaixaDSLoginSwitch(checked = state.biometricEnabled)
            }
            if (environmentSelectorVisible) {
                Box(Modifier.fillMaxWidth().height(48.dp).padding(start = 10.dp)) {
                    environmentSelector(!state.authorizationPending && !state.isLoading)
                }
            } else Spacer(Modifier.height(CaixaDS.spacing.xl))
            Text(
                text = "v. $versionName",
                color = CaixaDS.colors.onBrand,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }

    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
private fun LegacyLoginButton(
    text: String,
    onClick: () -> Unit,
    outlined: Boolean,
    enabled: Boolean,
    loading: Boolean = false,
) {
    val background = if (outlined) CaixaDS.colors.authenticationBackground else CaixaDS.colors.actionHighlight
    val foreground = CaixaDS.colors.onBrand
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(CaixaDS.radii.sm),
        border = if (outlined) BorderStroke(1.dp, foreground) else null,
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = foreground,
            disabledContainerColor = background.copy(alpha = 0.55f),
            disabledContentColor = foreground.copy(alpha = 0.75f),
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = CaixaDS.elevation.none),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(CaixaDS.sizes.iconMd),
                color = foreground,
                strokeWidth = 2.dp,
            )
        } else {
            Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                fontFamily = androidx.compose.ui.text.font.FontFamily(androidx.compose.ui.text.font.Font(
                    br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_semi_bold)),
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)))
        }
    }
}
