package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.automationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSFingerprintIcon
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSLoteriasLogo
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSLoginSwitch
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.account.R as LocalizedR
import br.gov.caixa.loterias.feature.account.presentation.components.LegacyLoginButton
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState

@Composable
internal fun LoginPrimaryContent(
    state: LoginUiState,
    onTryNow: () -> Unit,
    onLogin: () -> Unit,
    largeFont: Boolean,
    modifier: Modifier = Modifier,
    onSwitchUser: () -> Unit = {},
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .padding(start = CaixaDS.spacing.xl, top = if (largeFont) 24.dp else 130.dp, end = CaixaDS.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CaixaDSLoteriasLogo(
            contentDescription = uiStrings.getString(LocalizedR.string.ui_logo_loterias_caixa_0ec4dc),
            modifier = (Modifier.size(width = 93.dp, height = 95.dp)).uiId("account.0037")
        )
        Spacer(Modifier.height(57.dp))
        LegacyLoginButton(
            text = uiStrings.getString(LocalizedR.string.ui_experimente_ccdddf),
            onClick = onTryNow,
            outlined = true,
            modifier = Modifier.automationId("simlo.account.login.experimente"),
            enabled = !state.isLoading && !state.authorizationPending && !state.blockedAccountCheckFailed
        )
        Spacer(Modifier.height(42.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f).height(1.dp).background(CaixaDS.colors.onBrand))
            Text(
                text = uiStrings.getString(LocalizedR.string.ui_ou_79a904),
                modifier = (Modifier.padding(horizontal = 6.dp)).uiId("account.0039"),
                color = CaixaDS.colors.onBrand,
                fontFamily = br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
            )
            Box(Modifier.weight(1f).height(1.dp).background(CaixaDS.colors.onBrand))
        }
        Spacer(Modifier.height(42.dp))
        LegacyLoginButton(
            text = state.rememberedFirstName?.let { uiStrings.getString(LocalizedR.string.ui_entrar_como, it) }
                ?: uiStrings.getString(LocalizedR.string.ui_faca_login_cadastre_se_491977),
            onClick = onLogin,
            outlined = false,
            modifier = Modifier.automationId("simlo.account.login.entrar"),
            enabled = !state.isLoading && !state.authorizationPending && !state.blockedAccountCheckFailed,
            loading = state.isLoading
        )
        if (state.rememberedLoginAvailable) {
            Box(
                Modifier.fillMaxWidth().heightIn(min = 48.dp).automationId("simlo.account.login.trocar_usuario").clickable(
                    enabled = !state.isLoading && !state.authorizationPending && !state.blockedAccountCheckFailed,
                    role = Role.Button,
                    onClick = onSwitchUser
                ).padding(horizontal = CaixaDS.spacing.md),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiStrings.getString(LocalizedR.string.ui_trocar_usuario),
                    modifier = (Modifier.fillMaxWidth()).uiId("account.0042"),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = CaixaDS.colors.onBrand,
                    fontSize = 16.sp
                )
            }
        }
        state.errorMessage?.let { message ->
            Text(
                text = message,
                modifier = (Modifier.padding(top = CaixaDS.spacing.md)).uiId("account.0043"),
                color = CaixaDS.colors.onBrand,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
internal fun LoginFooterContent(
    state: LoginUiState,
    versionName: String,
    onBiometricChanged: (Boolean) -> Unit,
    environmentSelectorVisible: Boolean,
    environmentSelector: @Composable (Boolean) -> Unit,
    largeFont: Boolean,
    modifier: Modifier = Modifier,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .padding(bottom = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.biometricRowVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (largeFont) Modifier.heightIn(min = 40.dp) else Modifier.height(40.dp))
                    .padding(horizontal = 16.dp)
                    .automationId("simlo.account.login.biometria").toggleable(
                        value = state.biometricEnabled,
                        role = Role.Switch,
                        onValueChange = onBiometricChanged
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CaixaDSFingerprintIcon(
                    color = CaixaDS.colors.onBrand,
                    modifier = (Modifier.size(width = 23.dp, height = 30.dp)).uiId("account.0045")
                )
                Text(
                    text = uiStrings.getString(LocalizedR.string.ui_acessar_com_biometria_3765f3),
                    color = CaixaDS.colors.onBrand,
                    fontSize = 20.sp,
                    modifier = (if (largeFont) Modifier.weight(1f).padding(horizontal = 8.dp) else Modifier).uiId("account.0046"),
                    maxLines = if (largeFont) Int.MAX_VALUE else 1
                )
                CaixaDSLoginSwitch(checked = state.biometricEnabled, modifier = Modifier.uiId("account.0047"))
            }
        }
        if (environmentSelectorVisible) {
            Box(Modifier.fillMaxWidth().height(48.dp).padding(start = 10.dp)) {
                environmentSelector(!state.authorizationPending && !state.isLoading)
            }
        } else {
            Spacer(Modifier.height(CaixaDS.spacing.xl))
        }
        Text(
            text = uiStrings.getString(LocalizedR.string.ui_v_1_s_8c8fcd, versionName),
            color = CaixaDS.colors.onBrand,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            modifier = Modifier.uiId("account.0048")
        )
    }
}
