package br.gov.caixa.loterias.apostas.event

/**
 * Eventos disparados pela View (Activity) e tratados exclusivamente
 * pelo metodo onEvent da DadosPessoaisViewModel.
 *
 * Inclui os eventos de consumo de comando, no mesmo padrao do
 * SimulaUiEvent (ComandoCarrinhoConsumido, ComandoAnalyticsConsumido...).
 */
sealed class DadosPessoaisEvent {

    /** Contrato do lambda usado na Activity para lancar eventos. */
    fun interface Dispatcher {
        fun dispatch(event: DadosPessoaisEvent)
    }

    // ------------------------------------------------------------------
    // Ciclo de vida
    // ------------------------------------------------------------------

    data object TelaIniciada : DadosPessoaisEvent()

    data object TelaRetomada : DadosPessoaisEvent()

    // ------------------------------------------------------------------
    // Edicao de campos
    // ------------------------------------------------------------------

    data class CepAlterado(val cep: String?) : DadosPessoaisEvent()

    data class LimiteAlterado(val limiteFormatado: String?) : DadosPessoaisEvent()

    data class SexoSelecionado(val posicao: Int) : DadosPessoaisEvent()

    data class NotificacaoAlterada(val aceitaNoticias: Boolean) : DadosPessoaisEvent()

    // ------------------------------------------------------------------
    // Acoes do usuario
    // ------------------------------------------------------------------

    data object NaoSeiCepClicado : DadosPessoaisEvent()

    data object RedirecionamentoCorreiosConfirmado : DadosPessoaisEvent()

    data object RecalcularLimiteClicado : DadosPessoaisEvent()

    data object AtualizarClicado : DadosPessoaisEvent()

    data object VoltarClicado : DadosPessoaisEvent()

    // ------------------------------------------------------------------
    // Consumo dos comandos (limpam o slot no state)
    // ------------------------------------------------------------------

    data object ComandoRedeConsumido : DadosPessoaisEvent()

    data object ComandoDialogConsumido : DadosPessoaisEvent()

    data object ComandoNavegacaoConsumido : DadosPessoaisEvent()

    data object ComandoAnalyticsConsumido : DadosPessoaisEvent()
}
