package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSCurrentCartDeleteIcon
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSCurrentCartColors
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.ui.R as DeliveryR
import java.util.Locale

@Composable
fun CurrentCartDeleteDelivered(onDelete: () -> Unit, size: Int) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val artworkSize = when (size) {
        // O recurso nativo de lixeira possui uma caixa intrínseca de 20dp com seus próprios recuos de visualização.
        // Reaplicar o espaçamento de ImageButton do XML deixa apenas cerca de 4 x 5dp de desenho visível.
        INDIVIDUAL_DELETE_TARGET_DP -> INDIVIDUAL_DELETE_ARTWORK_DP

        COMBO_DELETE_TARGET_DP -> COMBO_DELETE_ARTWORK_DP

        // ImageButton do combo: 32dp menos 7dp de espaçamento em ambos os lados.
        else -> POOL_DELETE_ARTWORK_DP // ImageView do bolão ajusta o drawable aos seus limites de 24dp.
    }
    IconButton(onClick = onDelete, modifier = (Modifier.size(size.dp)).uiId("cart.0132")) {
        Icon(
            LoteriasCaixaDSCurrentCartDeleteIcon,
            uiStrings.getString(DeliveryR.string.ui_excluir_8b1951),
            (Modifier.size(artworkSize.dp)).uiId("cart.0133"),
            tint = if (size == INDIVIDUAL_DELETE_TARGET_DP) LoteriasCaixaDSCurrentCartColors.Delete else Color.Unspecified
        )
    }
}

@Composable
fun CurrentCartDividerDelivered() {
    Spacer(Modifier.height(14.dp))
    Box(Modifier.fillMaxWidth().height(1.dp).background(LoteriasCaixaDSCurrentCartColors.Divider))
    Spacer(Modifier.height(12.dp))
}

@Composable
fun currentCartModalityColor(code: String): Color {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val supported = code.uppercase(Locale.ROOT).replace("-", "").replace("_", "") in setOf(
        "MEGASENA", "LOTOFACIL", uiStrings.getString(LocalizedR.string.ui_lotofacil_dd4339), "QUINA", "LOTOMANIA", "TIMEMANIA", "DUPLASENA",
        "DIADESORTE", "SUPER7", "SUPERSETE", "MAISMILIONARIA", "+MILIONARIA", "LOTECA"
    )
    if (!supported) return CurrentCartBlue
    val style = loteriasCaixaDSLotteryStyle(0, code)
    val nativeLightTitle = code.uppercase(Locale.ROOT).replace("-", "").replace("_", "") in
        setOf("SUPER7", "SUPERSETE", "TIMEMANIA", "DIADESORTE")
    return if (nativeLightTitle) style.light else style.listText
}

private const val INDIVIDUAL_DELETE_TARGET_DP = 25
private const val INDIVIDUAL_DELETE_ARTWORK_DP = 20
private const val COMBO_DELETE_TARGET_DP = 32
private const val COMBO_DELETE_ARTWORK_DP = 18
private const val POOL_DELETE_ARTWORK_DP = 24
