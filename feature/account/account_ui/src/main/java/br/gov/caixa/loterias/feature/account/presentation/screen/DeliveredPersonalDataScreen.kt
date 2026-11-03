package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountBackground
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataUiState
import br.gov.caixa.loterias.feature.account.ui.R as DeliveryR

@Composable
fun RenderPersonalDataScreen(state: PersonalDataUiState, imeVisible: Boolean, onBack: () -> Unit, actions: PersonalDataActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(DeliveryR.string.ui_dados_pessoais_f10a9c),
        modifier = (Modifier.imePadding()).uiId("account.0050"),
        onBack = onBack,
        gradientTopBar = false,

        containerColor = LoteriasCaixaDSColorTokens.White,
        bottomBar = if (imeVisible) {
            null
        } else {
            (
                {
                    PersonalDataFooter(
                        enabled = canUpdatePersonalData(state),
                        onUpdate = actions.submit
                    )
                }
                )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).background(LoteriasCaixaDSColorTokens.White)) {
            Column(
                Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                    .background(LoteriasCaixaDSAccountBackground).uiId("account.0051").verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp)
            ) {
                LegacyPersonalDataIntro((Modifier.fillMaxWidth().padding(top = 16.dp)).uiId("account.0052"))
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    PersonalIdentityFields(state, actions)
                    PersonalAddressFields(state, actions)
                    PersonalLimitControl(state, actions)
                }
                Spacer(Modifier.height(16.dp))
                LegacyNotificationRow(state.acceptsNotifications, actions.toggleNotifications)
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
