package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.presentation.WorkflowField
import br.gov.caixa.loterias.core.presentation.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.WorkflowSpec

val bettingScreens = listOf(
    WorkflowSpec("betting/home", "Aposte", "Escolha uma modalidade para criar sua aposta.", primaryAction = "Escolher modalidade"),
    WorkflowSpec("betting/modalities", "Modalidades", "Mega-Sena, Lotofácil, Quina, Lotomania, Timemania, Dupla Sena, Dia de Sorte, Super Sete, +Milionária, Loteca e Federal.", primaryAction = "Selecionar"),
    WorkflowSpec("betting/simulation", "Simular aposta", "Marque as dezenas, use Surpresinha ou configure Teimosinha.", listOf(WorkflowField("numbers", "Dezenas", required = true), WorkflowField("draws", "Concursos", WorkflowFieldKind.NUMBER)), "Adicionar ao carrinho"),
    WorkflowSpec("betting/rapid", "Rapidão", "Configure apostas automáticas por modalidade, quantidade e valor.", listOf(WorkflowField("quantity", "Quantidade de apostas", WorkflowFieldKind.NUMBER, true), WorkflowField("value", "Valor", WorkflowFieldKind.CURRENCY, true)), "Gerar apostas"),
    WorkflowSpec("betting/rapid-list", "Rapidão", "Confira a lista de apostas geradas.", primaryAction = "Adicionar ao carrinho"),
    WorkflowSpec("betting/favorites", "Apostas favoritas", "Reaproveite jogos salvos ou escolha favoritos para o Rapidão.", primaryAction = "Apostar novamente"),
    WorkflowSpec("betting/pools", "Bolões", "Encontre bolões disponíveis, confira cotas e detalhes antes de comprar.", primaryAction = "Ver bolões"),
    WorkflowSpec("betting/pool-detail", "Detalhes do bolão", "Consulte concurso, apostas, dezenas, quantidade de cotas e valor por cota.", primaryAction = "Adicionar cota"),
    WorkflowSpec("betting/pool-view", "Bolão", "Confira todas as apostas que compõem o bolão.", primaryAction = "Comprar cota"),
    WorkflowSpec("betting/combos", "Combos", "Confira combinações prontas de apostas.", primaryAction = "Ver detalhes"),
    WorkflowSpec("betting/combo-detail", "Detalhes do combo", "Revise as modalidades e apostas incluídas neste combo.", primaryAction = "Adicionar ao carrinho"),
    WorkflowSpec("betting/tutorial", "Tutorial", "Veja como fazer uma aposta na modalidade escolhida.", primaryAction = "Entendi"),
    WorkflowSpec("betting/tutorial-plus", "+Milionária", "Conheça dezenas e trevos da +Milionária.", primaryAction = "Entendi"),
    WorkflowSpec("betting/tutorial-super-seven", "Super Sete", "Conheça as sete colunas da Super Sete.", primaryAction = "Entendi"),
)
