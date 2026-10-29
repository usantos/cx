package br.gov.caixa.loterias.feature.betting.presentation.screen

import br.gov.caixa.loterias.core.presentation.state.WorkflowField
import br.gov.caixa.loterias.core.presentation.state.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.betting.R

fun bettingScreens(text: (Int) -> String): List<WorkflowSpec> =
    bettingSelectionScreens(text) + bettingOfferScreens(text) + bettingTutorialScreens(text)

private fun bettingSelectionScreens(text: (Int) -> String) = listOf(
    WorkflowSpec(
        "betting/home",
        text(R.string.workflow_betting_aposte),
        text(R.string.workflow_betting_escolha_uma_modalidade_para_criar_sua_aposta),
        primaryAction = text(R.string.workflow_betting_escolher_modalidade)
    ),
    WorkflowSpec(
        "betting/modalities",
        text(R.string.workflow_betting_modalidades),
        text(R.string.workflow_betting_mega_sena_lotofacil_quina_lotomania_timemania_dupla_sena_dia_de_s),
        primaryAction = text(R.string.workflow_betting_selecionar)
    ),
    WorkflowSpec(
        "betting/simulation",
        text(R.string.workflow_betting_simular_aposta),
        text(R.string.workflow_betting_marque_as_dezenas_use_surpresinha_ou_configure_teimosinha),
        listOf(
            WorkflowField("numbers", text(R.string.workflow_betting_dezenas), required = true),
            WorkflowField("draws", text(R.string.workflow_betting_concursos), WorkflowFieldKind.NUMBER)
        ),
        text(R.string.workflow_betting_adicionar_ao_carrinho)
    ),
    WorkflowSpec(
        "betting/rapid",
        text(R.string.workflow_betting_rapidao),
        text(R.string.workflow_betting_configure_apostas_automaticas_por_modalidade_quantidade_e_valor),
        listOf(
            WorkflowField("quantity", text(R.string.workflow_betting_quantidade_de_apostas), WorkflowFieldKind.NUMBER, true),
            WorkflowField("value", text(R.string.workflow_betting_valor), WorkflowFieldKind.CURRENCY, true)
        ),
        text(R.string.workflow_betting_gerar_apostas)
    ),
    WorkflowSpec(
        "betting/rapid-list",
        text(R.string.workflow_betting_rapidao),
        text(R.string.workflow_betting_confira_a_lista_de_apostas_geradas),
        primaryAction = text(R.string.workflow_betting_adicionar_ao_carrinho)
    ),
    WorkflowSpec(
        "betting/favorites",
        text(R.string.workflow_betting_apostas_favoritas),
        text(R.string.workflow_betting_reaproveite_jogos_salvos_ou_escolha_favoritos_para_o_rapidao),
        primaryAction = text(R.string.workflow_betting_apostar_novamente)
    )
)

private fun bettingOfferScreens(text: (Int) -> String) = listOf(
    WorkflowSpec(
        "betting/pools",
        text(R.string.workflow_betting_boloes),
        text(R.string.workflow_betting_encontre_boloes_disponiveis_confira_cotas_e_detalhes_antes_de_com),
        primaryAction = text(R.string.workflow_betting_ver_boloes)
    ),
    WorkflowSpec(
        "betting/pool-detail",
        text(R.string.workflow_betting_detalhes_do_bolao),
        text(R.string.workflow_betting_consulte_concurso_apostas_dezenas_quantidade_de_cotas_e_valor_por),
        primaryAction = text(R.string.workflow_betting_adicionar_cota)
    ),
    WorkflowSpec(
        "betting/pool-view",
        text(R.string.workflow_betting_bolao),
        text(R.string.workflow_betting_confira_todas_as_apostas_que_compoem_o_bolao),
        primaryAction = text(R.string.workflow_betting_comprar_cota)
    ),
    WorkflowSpec(
        "betting/combos",
        text(R.string.workflow_betting_combos),
        text(R.string.workflow_betting_confira_combinacoes_prontas_de_apostas),
        primaryAction = text(R.string.workflow_betting_ver_detalhes)
    ),
    WorkflowSpec(
        "betting/combo-detail",
        text(R.string.workflow_betting_detalhes_do_combo),
        text(R.string.workflow_betting_revise_as_modalidades_e_apostas_incluidas_neste_combo),
        primaryAction = text(R.string.workflow_betting_adicionar_ao_carrinho)
    )
)

private fun bettingTutorialScreens(text: (Int) -> String) = listOf(
    WorkflowSpec(
        "betting/tutorial",
        text(R.string.workflow_betting_tutorial),
        text(R.string.workflow_betting_veja_como_fazer_uma_aposta_na_modalidade_escolhida),
        primaryAction = text(R.string.workflow_betting_entendi)
    ),
    WorkflowSpec(
        "betting/tutorial-plus",
        text(R.string.workflow_betting_milionaria),
        text(R.string.workflow_betting_conheca_dezenas_e_trevos_da_milionaria),
        primaryAction = text(R.string.workflow_betting_entendi)
    ),
    WorkflowSpec(
        "betting/tutorial-super-seven",
        text(R.string.workflow_betting_super_sete),
        text(R.string.workflow_betting_conheca_as_sete_colunas_da_super_sete),
        primaryAction = text(R.string.workflow_betting_entendi)
    )
)
