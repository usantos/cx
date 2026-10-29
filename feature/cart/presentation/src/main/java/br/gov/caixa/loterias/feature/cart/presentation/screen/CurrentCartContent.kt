package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSCurrentCartIcon
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSCurrentCartColors
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartNativeText
import br.gov.caixa.loterias.feature.cart.presentation.mapper.sectionTitle
import br.gov.caixa.loterias.feature.cart.presentation.mapper.visibleCartSections
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartViewModel
import java.time.Instant
import kotlin.math.roundToInt

@Composable
internal fun CurrentCartContent(
    state: CartUiState,
    padding: PaddingValues,
    items: List<CartItem>,
    nowMillis: Long,
    onRemove: (CartItem) -> Unit,
    onPoolDetail: (String) -> Unit,
    onComboDetail: (Long) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (items.isEmpty()) {
        // textSize do XML é arredondado para um pixel inteiro antes de TextView desenhar os glifos.
        val messageSize = with(LocalDensity.current) { 12.sp.toPx().roundToInt().toFloat().toSp() }
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = BiasAlignment(0f, 0.1f)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    CaixaDSCurrentCartIcon,
                    null,
                    (Modifier.legacyAutomationId("cart-empty-icon").size(140.dp)).uiId("cart.0101"),
                    tint = CaixaDSCurrentCartColors.EmptyIcon
                )
                Text(
                    uiStrings.getString(LocalizedR.string.ui_seu_carrinho_esta_vazio_106816),
                    (Modifier.padding(top = 16.dp)).uiId("cart.0102"),
                    color = CaixaDSColorTokens.Blue700,
                    style = cartNativeText(12, FontWeight.SemiBold).copy(fontSize = messageSize)
                )
            }
        }
        return
    }
    LazyColumn(
        (Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp))
            .uiId("cart.0103"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                uiStrings.getString(LocalizedR.string.ui_confira_suas_apostas_e_siga_para_o_p_f4bc60),
                (Modifier.fillMaxWidth().padding(top = 24.dp)).uiId("cart.0104"),
                color = CurrentCartBlue,
                style = cartNativeText(14, FontWeight.Bold)
            )
            // item_header_carrinho_fixo da CX atual renderiza este recurso literalmente;
            // os limites configurados efetivos permanecem exclusivamente em CartViewModel e na finalização da compra.
            Text(
                uiStrings.getString(LocalizedR.string.ui_o_valor_minimo_do_carrinho_e_r_2_50_e14f4e),
                (Modifier.fillMaxWidth().padding(top = 7.dp)).uiId("cart.0105"),
                color = CaixaDSColorTokens.Black,
                style = cartNativeText()
            )
            state.errorMessage?.let {
                Text(it, (Modifier.padding(top = 7.dp)).uiId("cart.0106"), color = CaixaDS.colors.error)
            }
            if (state.cart?.expiredPoolShareRemoved == true) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_a_s_cota_s_expirada_s_foram_excluida_410ba9),
                    (Modifier.padding(top = 7.dp)).uiId("cart.0107"),
                    color = CaixaDS.colors.error
                )
            }
        }
        visibleCartSections(items).forEach { section ->
            item { CartSection(section.kind.sectionTitle) }
            items(section.items, key = { "${it.kind}-${it.id}" }) { item ->
                CurrentCartRow(
                    item,
                    now = Instant.ofEpochMilli(nowMillis),
                    onDelete = { onRemove(item) },
                    onDetail = when (item.kind) {
                        CartItemKind.POOL_SHARE -> ({ item.poolShare?.code?.let(onPoolDetail) })
                        CartItemKind.COMBO -> ({ onComboDetail(item.id) })
                        CartItemKind.INDIVIDUAL_BET -> null
                    }
                )
            }
        }
    }
}

@Composable
internal fun CartSection(title: String) {
    Text(
        title,
        (Modifier.fillMaxWidth().padding(top = 40.dp)).uiId("cart.0108"),
        color = CurrentCartBlue,
        style = cartNativeText(14, FontWeight.Bold)
    )
    Spacer(Modifier.height(13.dp))
    Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDSCurrentCartColors.SectionDivider))
    Spacer(Modifier.height(12.dp))
}

@Composable
internal fun CartError(message: String, padding: PaddingValues, onRetry: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.fillMaxSize().padding(padding).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = CaixaDS.colors.error, modifier = Modifier.uiId("cart.0109"))
        Text(
            uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            (Modifier.uiId("cart.0110").clickable(onClick = onRetry).padding(20.dp)).uiId("cart.0111"),
            color = CaixaDS.colors.brand
        )
    }
}
