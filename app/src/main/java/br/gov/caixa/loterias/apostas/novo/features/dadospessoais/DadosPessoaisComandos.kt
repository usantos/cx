package br.gov.caixa.loterias.apostas.novo.features.dadospessoais

/**
 * Agrupa os comandos de uso unico (one-shot) emitidos pela ViewModel.
 *
 * Diferente de [DadosPessoaisState], que representa o estado persistente e
 * idempotente da tela, cada comando aqui deve ser consumido pela Activity e
 * imediatamente limpo atraves dos eventos `Comando*Consumido`.
 */
data class DadosPessoaisComandos(
    val rede: DadosPessoaisEffect? = null,
    val dialog: DadosPessoaisEffect? = null,
    val navegacao: DadosPessoaisEffect? = null,
    val analytics: DadosPessoaisEffect? = null
)
