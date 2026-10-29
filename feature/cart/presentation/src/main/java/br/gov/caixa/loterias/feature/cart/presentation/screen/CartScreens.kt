package br.gov.caixa.loterias.feature.cart.presentation.screen

import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.cart.R

fun cartScreens(text: (Int) -> String) = listOf(
    WorkflowSpec(
        "cart/current",
        text(R.string.workflow_cart_carrinho_de_apostas),
        text(R.string.workflow_cart_confira_apostas_individuais_cotas_de_bolao_e_combos_antes_de_avan),
        primaryAction = text(R.string.workflow_cart_avancar_npara_a_forma_de_pagamento),
        secondaryAction = text(R.string.workflow_cart_continuar_apostando)
    ),
    WorkflowSpec(
        "cart/included",
        text(R.string.workflow_cart_aposta_adicionada),
        text(R.string.workflow_cart_sua_aposta_foi_incluida_no_carrinho),
        primaryAction = text(R.string.workflow_cart_ver_carrinho),
        secondaryAction = text(R.string.workflow_cart_continuar_apostando)
    ),
    WorkflowSpec(
        "cart/pool-included",
        text(R.string.workflow_cart_cota_adicionada),
        text(R.string.workflow_cart_a_cota_de_bolao_foi_incluida_no_carrinho),
        primaryAction = text(R.string.workflow_cart_ver_carrinho),
        secondaryAction = text(R.string.workflow_cart_continuar)
    ),
    WorkflowSpec(
        "cart/favorite-carts",
        text(R.string.workflow_cart_carrinhos_favoritos),
        text(R.string.workflow_cart_consulte_carrinhos_salvos_para_repetir_suas_apostas),
        primaryAction = text(R.string.workflow_cart_selecionar)
    ),
    WorkflowSpec(
        "cart/favorite-detail",
        text(R.string.workflow_cart_favoritas),
        text(R.string.workflow_cart_apostas_do_carrinho_favorito_escolhido),
        primaryAction = text(R.string.workflow_cart_excluir_aposta)
    )
)
