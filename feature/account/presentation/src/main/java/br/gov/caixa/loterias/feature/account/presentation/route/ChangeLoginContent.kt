package br.gov.caixa.loterias.feature.account.presentation.route

import android.content.ActivityNotFoundException
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSPrimaryButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.account.R as LocalizedR

@Composable
internal fun ChangeLoginScreen(url: String, onBack: () -> Unit) {
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
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_alterar_dados_login_caixa_c7bbfc),
        onBack = onBack,
        modifier = Modifier.uiId("account.0014")
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding).background(LoteriasCaixaDS.colors.surface).padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            when (opened) {
                true -> Text(
                    uiStrings.getString(LocalizedR.string.ui_a_pagina_segura_da_conta_caixa_foi_a_9f4f21),
                    color = LoteriasCaixaDS.colors.primaryButton,
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    modifier = Modifier.uiId("account.0015")
                )

                false -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_abrir_a_pagina_segu_7efc3e),
                        color = LoteriasCaixaDS.colors.primaryButton,
                        textAlign = TextAlign.Center,
                        fontSize = 17.sp,
                        modifier = Modifier.uiId("account.0016")
                    )
                    Spacer(Modifier.height(16.dp))
                    LoteriasCaixaDSPrimaryButton(
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
