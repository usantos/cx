package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSSemiboldFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR
import coil3.ImageLoader

@Composable
fun ShakeTutorialScreenDelivered(
    page: Int,
    loader: ImageLoader,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onPage: (Int) -> Unit,
    onComplete: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val descriptions =
        listOf(
            uiStrings.getString(DeliveryR.string.ui_nova_funcionalidade_aposta_shake_com_2bdadc),
            uiStrings.getString(DeliveryR.string.ui_volante_da_mega_sena_aberto_ainda_se_19046e),
            uiStrings.getString(DeliveryR.string.ui_ilustracao_de_um_celular_sendo_agita_56d513),
            uiStrings.getString(DeliveryR.string.ui_volante_da_mega_sena_com_seis_numero_0d92f4),
            uiStrings.getString(DeliveryR.string.ui_menu_lateral_com_a_opcao_aposta_shak_a3fa2a),
            uiStrings.getString(DeliveryR.string.ui_agora_e_so_ativar_a_funcionalidade_a_df881f)
        )
    val images =
        listOf(
            DeliveryR.raw.aposta_shake_apresentacao,
            DeliveryR.raw.aposta_shake_abrir_volante,
            DeliveryR.raw.aposta_shake_agitar,
            DeliveryR.raw.aposta_shake_preencher,
            DeliveryR.raw.aposta_shake_menu,
            DeliveryR.raw.aposta_shake_final
        )
    Column(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(LoteriasCaixaDSColorTokens.White, LoteriasCaixaDSColorTokens.ShakeTutorialBottom))
        )
            .statusBarsPadding().navigationBarsPadding().padding(top = 12.dp)
    ) {
        ShakeTutorialTitleDelivered(page)
        ShakeTutorialPageDelivered(
            page = page,
            image = images[page],
            description = descriptions[page],
            loader = loader,
            onPrevious = onPrevious,
            onNext = onNext,
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        ShakeTutorialPaginationDelivered(page, onPage)
        TextButton(onClick = onComplete, (Modifier.fillMaxWidth().padding(bottom = 12.dp).height(56.dp)).uiId("bet.0218")) {
            Text(
                if (page ==
                    FINAL_TUTORIAL_PAGE_INDEX
                ) {
                    uiStrings.getString(DeliveryR.string.ui_fechar_tutorial_e8e309)
                } else {
                    uiStrings.getString(DeliveryR.string.ui_pular_tutorial_4ba00f)
                },
                fontFamily = LoteriasCaixaDSSemiboldFontFamily,
                fontSize = 16.sp,
                color = LoteriasCaixaDSColorTokens.SocialGraphite,
                fontWeight = FontWeight.Normal,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.uiId("bet.0219")
            )
        }
    }
}

private const val FINAL_TUTORIAL_PAGE_INDEX = 5
