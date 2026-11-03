package br.gov.caixa.loterias.feature.cart.presentation.delivery

import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry

interface CartUiEntry :
    FeatureUiEntry,
    CartCartAdditionPoolScreenEntry,
    CartFavoriteCartsContentEntry,
    CartCartAdditionBetScreenEntry,
    CartFavoriteCartDetailContentEntry,
    CartCurrentCartBodyEntry,
    CartLocalCartReviewContentEntry
