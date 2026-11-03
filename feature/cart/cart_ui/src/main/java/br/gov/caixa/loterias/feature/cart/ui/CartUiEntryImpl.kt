package br.gov.caixa.loterias.feature.cart.ui

import androidx.annotation.Keep
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartCartAdditionBetScreenEntry
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartCartAdditionPoolScreenEntry
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartCurrentCartBodyEntry
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartFavoriteCartDetailContentEntry
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartFavoriteCartsContentEntry
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartLocalCartReviewContentEntry
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartUiEntry

@Keep
class CartUiEntryImpl :
    CartUiEntry,

    CartFavoriteCartDetailContentEntry by CartFavoriteCartDetailContentRenderer(),
    CartCartAdditionBetScreenEntry by CartCartAdditionBetScreenRenderer(),

    CartLocalCartReviewContentEntry by CartLocalCartReviewContentRenderer(),
    CartFavoriteCartsContentEntry by CartFavoriteCartsContentRenderer(),
    CartCartAdditionPoolScreenEntry by CartCartAdditionPoolScreenRenderer(),

    CartCurrentCartBodyEntry by CartCurrentCartBodyRenderer()
