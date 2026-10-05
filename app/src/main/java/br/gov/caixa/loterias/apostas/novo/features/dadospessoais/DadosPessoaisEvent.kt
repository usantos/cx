package br.gov.caixa.loterias.apostas.novo.features.dadospessoais

sealed class DadosPessoaisEvent {

    fun interface Dispatcher {
        fun dispatch(event: DadosPessoaisEvent)
    }

    data object TelaIniciada : DadosPessoaisEvent()

    data object TelaRetomada : DadosPessoaisEvent()

    data class CepAlterado(val cep: String?) : DadosPessoaisEvent()

    data class LimiteAlterado(val limiteFormatado: String?) : DadosPessoaisEvent()

    data class SexoSelecionado(val posicao: Int) : DadosPessoaisEvent()

    data class NotificacaoAlterada(val aceitaNoticias: Boolean) : DadosPessoaisEvent()

    data object NaoSeiCepClicado : DadosPessoaisEvent()

    data object RedirecionamentoCorreiosConfirmado : DadosPessoaisEvent()

    data object RecalcularLimiteClicado : DadosPessoaisEvent()

    data object AtualizarClicado : DadosPessoaisEvent()

    data object VoltarClicado : DadosPessoaisEvent()

    data object ComandoRedeConsumido : DadosPessoaisEvent()

    data object ComandoDialogConsumido : DadosPessoaisEvent()

    data object ComandoNavegacaoConsumido : DadosPessoaisEvent()

    data object ComandoAnalyticsConsumido : DadosPessoaisEvent()
}
