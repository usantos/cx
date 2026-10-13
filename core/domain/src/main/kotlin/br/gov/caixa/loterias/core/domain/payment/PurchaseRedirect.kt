package br.gov.caixa.loterias.core.domain.payment

/** Known purchase destinations; CART and FAVORITE_CARTS belong only to the HTTP error handler. */
enum class PurchaseRedirect { LOGIN, ACCEPT_TERMS, REGISTER_ACCOUNT, UNAVAILABLE, CART, FAVORITE_CARTS }
