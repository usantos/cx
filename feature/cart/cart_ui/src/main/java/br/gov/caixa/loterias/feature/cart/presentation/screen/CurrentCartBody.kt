package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.foundation.background
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
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSCurrentCartIcon
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSCurrentCartColors
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartNativeText
import br.gov.caixa.loterias.feature.cart.presentation.mapper.sectionTitle
import br.gov.caixa.loterias.feature.cart.presentation.mapper.visibleCartSections
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartViewModel
import br.gov.caixa.loterias.feature.cart.ui.R as DeliveryR
import java.time.Instant
import kotlin.math.roundToInt

@Composable
fun CurrentCartContentDelivered(
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
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = BiasAlignment(0f, CART_EMPTY_VERTICAL_BIAS)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    LoteriasCaixaDSCurrentCartIcon,
                    null,
                    (Modifier.legacyAutomationId("cart-empty-icon").size(140.dp)).uiId("cart.0101"),
                    tint = LoteriasCaixaDSCurrentCartColors.EmptyIcon
                )
                Text(
                    uiStrings.getString(DeliveryR.string.ui_seu_carrinho_esta_vazio_106816),
                    (Modifier.padding(top = 16.dp)).uiId("cart.0102"),
                    color = LoteriasCaixaDSColorTokens.Blue700,
                    style = cartNativeText(CART_BODY_TEXT_SIZE, FontWeight.SemiBold).copy(fontSize = messageSize)
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
                uiStrings.getString(DeliveryR.string.ui_confira_suas_apostas_e_siga_para_o_p_f4bc60),
                (Modifier.fillMaxWidth().padding(top = 24.dp)).uiId("cart.0104"),
                color = CurrentCartBlue,
                style = cartNativeText(CART_SECTION_TEXT_SIZE, FontWeight.Bold)
            )
            // item_header_carrinho_fixo da CX atual renderiza este recurso literalmente;
            // os limites configurados efetivos permanecem exclusivamente em CartViewModel e na finalização da compra.
            Text(
                uiStrings.getString(DeliveryR.string.ui_o_valor_minimo_do_carrinho_e_r_2_50_e14f4e),
                (Modifier.fillMaxWidth().padding(top = 7.dp)).uiId("cart.0105"),
                color = LoteriasCaixaDSColorTokens.Black,
                style = cartNativeText()
            )
            state.errorMessage?.let {
                Text(it, (Modifier.padding(top = 7.dp)).uiId("cart.0106"), color = LoteriasCaixaDS.colors.error)
            }
            if (state.cart?.expiredPoolShareRemoved == true) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_a_s_cota_s_expirada_s_foram_excluida_410ba9),
                    (Modifier.padding(top = 7.dp)).uiId("cart.0107"),
                    color = LoteriasCaixaDS.colors.error
                )
            }
        }
        visibleCartSections(items).forEach { section ->
            item { CartSectionDelivered(section.kind.sectionTitle) }
            items(section.items, key = { "${it.kind}-${it.id}" }) { item ->
                CurrentCartRowDelivered(
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
fun CartSectionDelivered(title: String) {
    Text(
        title,
        (Modifier.fillMaxWidth().padding(top = 40.dp)).uiId("cart.0108"),
        color = CurrentCartBlue,
        style = cartNativeText(CART_SECTION_TEXT_SIZE, FontWeight.Bold)
    )
    Spacer(Modifier.height(13.dp))
    Box(Modifier.fillMaxWidth().height(1.dp).background(LoteriasCaixaDSCurrentCartColors.SectionDivider))
    Spacer(Modifier.height(12.dp))
}

private const val CART_EMPTY_VERTICAL_BIAS = 0.1f
private const val CART_BODY_TEXT_SIZE = 12
private const val CART_SECTION_TEXT_SIZE = 14
