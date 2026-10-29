package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartSpokenDescription
import br.gov.caixa.loterias.feature.cart.presentation.mapper.poolExpiry
import java.time.Instant

@Composable
internal fun Modifier.cartWholeBlockReadout(item: CartItem, now: Instant): Modifier {
    val expiry = item.poolShare?.let { poolExpiry(it.expiresAt, now) }
    val description = remember(item, expiry) { cartSpokenDescription(item, expiry) }
    // Descendentes clicáveis (exclusão, detalhe e legenda) permanecem acessíveis de forma independente.
    return semantics(mergeDescendants = true) { contentDescription = description }
}
