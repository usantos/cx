package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.core.presentation.WorkflowSpec

val cartScreens = listOf(
    WorkflowSpec("cart/current", "Carrinho de apostas", "Confira apostas individuais, cotas de bolão e combos antes de avançar.", primaryAction = "Avançar\npara a forma de pagamento", secondaryAction = "Continuar apostando"),
    WorkflowSpec("cart/included", "Aposta adicionada", "Sua aposta foi incluída no carrinho.", primaryAction = "Ver carrinho", secondaryAction = "Continuar apostando"),
    WorkflowSpec("cart/pool-included", "Cota adicionada", "A cota de bolão foi incluída no carrinho.", primaryAction = "Ver carrinho", secondaryAction = "Continuar"),
    WorkflowSpec("cart/favorite-carts", "Carrinhos favoritos", "Consulte carrinhos salvos para repetir suas apostas.", primaryAction = "Selecionar"),
    WorkflowSpec("cart/favorite-detail", "Favoritas", "Apostas do carrinho favorito escolhido", primaryAction = "Excluir aposta"),
)
