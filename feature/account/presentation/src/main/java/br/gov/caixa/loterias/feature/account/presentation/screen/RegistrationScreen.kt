package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryMark
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountBackground
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountCopy
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountSaveFooter
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.account.R as LocalizedR
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.presentation.state.RegistrationUiState

internal data class RegistrationActions(
    val changeGender: (Gender) -> Unit,
    val changeZipCode: (String) -> Unit,
    val toggleTerms: () -> Unit,
    val toggleNotifications: () -> Unit,
    val submit: () -> Unit,
)

@Composable
internal fun RegistrationScreen(
    state: RegistrationUiState,
    canSubmit: Boolean,
    actions: RegistrationActions,
    onBack: () -> Unit,
    onTerms: () -> Unit,
    onZipCodeRedirect: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_loterias_caixa_02d076),
        onBack = onBack,
        topBarAction = { LoteriasCaixaDSLotteryMark((Modifier.size(24.dp)).uiId("account.0019")) },
        topBarActionWidth = 48.dp,
        containerColor = LoteriasCaixaDSColorTokens.White,
        gradientTopBar = false,

        bottomBar = {
            LoteriasCaixaDSAccountSaveFooter(
                uiStrings.getString(LocalizedR.string.ui_finalizar_cadastro_77d482),
                actions.submit,
                canSubmit,
                disabledColor = LoteriasCaixaDS.colors.disabledButton
            )
        },
        modifier = Modifier.uiId("account.0020")
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(LoteriasCaixaDSColorTokens.White)
                .uiId("account.0021").verticalScroll(rememberScrollState())
        ) {
            Column(
                Modifier.fillMaxWidth().padding(
                    horizontal = 6.dp
                ).background(LoteriasCaixaDSAccountBackground).padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp)
            ) {
                LoteriasCaixaDSAccountCopy(
                    uiStrings.getString(LocalizedR.string.ui_que_bom_ver_voce_por_aqui_c7c430),
                    REGISTRATION_WELCOME_TEXT_SIZE_SP,
                    true,
                    (Modifier.fillMaxWidth().padding(top = 16.dp)).uiId("account.0022")
                )
                LoteriasCaixaDSAccountCopy(
                    uiStrings.getString(LocalizedR.string.ui_o_cadastro_e_bem_rapido_para_comecar_b409e1),
                    REGISTRATION_INTRO_TEXT_SIZE_SP,
                    false,
                    (Modifier.fillMaxWidth().padding(top = 12.dp)).uiId("account.0023")
                )
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    RegistrationIdentityFields(state, actions)
                    RegistrationAddressFields(state, actions, onZipCodeRedirect)
                }
                Spacer(Modifier.height(16.dp))
                TermsConsent(state.acceptsTerms, actions.toggleTerms, onTerms)
                NotificationConsent(state.acceptsNotifications, actions.toggleNotifications)
                state.errorMessage?.let {
                    Text(
                        it,
                        (Modifier.padding(top = 12.dp)).uiId("account.0024"),
                        color = LoteriasCaixaDS.colors.error,
                        fontSize = 14.sp
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

private const val REGISTRATION_WELCOME_TEXT_SIZE_SP = 18f
private const val REGISTRATION_INTRO_TEXT_SIZE_SP = 16f
