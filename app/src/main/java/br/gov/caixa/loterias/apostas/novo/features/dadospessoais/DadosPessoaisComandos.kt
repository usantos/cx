package br.gov.caixa.loterias.apostas.novo.features.dadospessoais

data class DadosPessoaisComandos(
    val rede: DadosPessoaisEffect? = null,
    val dialog: DadosPessoaisEffect? = null,
    val navegacao: DadosPessoaisEffect? = null,
    val analytics: DadosPessoaisEffect? = null
)
