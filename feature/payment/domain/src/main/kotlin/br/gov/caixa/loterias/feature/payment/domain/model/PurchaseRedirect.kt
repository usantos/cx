package br.gov.caixa.loterias.feature.payment.domain.model

/** Destinos conhecidos de compra; CART e FAVORITE_CARTS pertencem somente ao tratador de erro HTTP. */
enum class PurchaseRedirect { LOGIN, ACCEPT_TERMS, REGISTER_ACCOUNT, UNAVAILABLE, CART, FAVORITE_CARTS }
