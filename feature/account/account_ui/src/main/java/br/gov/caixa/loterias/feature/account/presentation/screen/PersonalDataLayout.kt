package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountSaveFooter
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountTextStyle
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.account.ui.R as DeliveryR

/** Preserva as métricas naturais de fonte do TextView XML mais seus 4dp literais de lineSpacingExtra. */
@Composable
internal fun LegacyPersonalDataIntro(modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(DeliveryR.string.ui_estes_sao_seus_dados_pessoais_por_fa_7b7c92),
        (modifier).uiId("account.0064"),
        color = LoteriasCaixaDSColorTokens.AccountHeading,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        style = LoteriasCaixaDSAccountTextStyle
    )
}

@Composable
internal fun PersonalDataFooter(enabled: Boolean, onUpdate: () -> Unit) {
    LoteriasCaixaDSAccountSaveFooter("Atualizar", onUpdate, enabled)
}
