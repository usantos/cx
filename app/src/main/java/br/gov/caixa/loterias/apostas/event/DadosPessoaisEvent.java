package br.gov.caixa.loterias.apostas.event;

/**
 * Eventos disparados pela View (Activity) e tratados exclusivamente
 * pelo metodo onEvent da DadosPessoaisViewModel.
 *
 * Inclui os eventos de consumo de comando, no mesmo padrao do
 * SimulaUiEvent (ComandoCarrinhoConsumido, ComandoAnalyticsConsumido...).
 */
public abstract class DadosPessoaisEvent {

    private DadosPessoaisEvent() {
        // evita subclasses fora deste arquivo
    }

    /** Contrato do lambda usado na Activity para lancar eventos. */
    public interface Dispatcher {
        void dispatch(DadosPessoaisEvent event);
    }

    // ------------------------------------------------------------------
    // Ciclo de vida
    // ------------------------------------------------------------------

    public static final class TelaIniciada extends DadosPessoaisEvent {
    }

    public static final class TelaRetomada extends DadosPessoaisEvent {
    }

    // ------------------------------------------------------------------
    // Edicao de campos
    // ------------------------------------------------------------------

    public static final class CepAlterado extends DadosPessoaisEvent {
        private final String cep;

        public CepAlterado(String cep) {
            this.cep = cep;
        }

        public String getCep() {
            return cep;
        }
    }

    public static final class LimiteAlterado extends DadosPessoaisEvent {
        private final String limiteFormatado;

        public LimiteAlterado(String limiteFormatado) {
            this.limiteFormatado = limiteFormatado;
        }

        public String getLimiteFormatado() {
            return limiteFormatado;
        }
    }

    public static final class SexoSelecionado extends DadosPessoaisEvent {
        private final int posicao;

        public SexoSelecionado(int posicao) {
            this.posicao = posicao;
        }

        public int getPosicao() {
            return posicao;
        }
    }

    public static final class NotificacaoAlterada extends DadosPessoaisEvent {
        private final boolean aceitaNoticias;

        public NotificacaoAlterada(boolean aceitaNoticias) {
            this.aceitaNoticias = aceitaNoticias;
        }

        public boolean isAceitaNoticias() {
            return aceitaNoticias;
        }
    }

    // ------------------------------------------------------------------
    // Acoes do usuario
    // ------------------------------------------------------------------

    public static final class NaoSeiCepClicado extends DadosPessoaisEvent {
    }

    public static final class RedirecionamentoCorreiosConfirmado extends DadosPessoaisEvent {
    }

    public static final class RecalcularLimiteClicado extends DadosPessoaisEvent {
    }

    public static final class AtualizarClicado extends DadosPessoaisEvent {
    }

    public static final class VoltarClicado extends DadosPessoaisEvent {
    }

    // ------------------------------------------------------------------
    // Consumo dos comandos (limpam o slot no state)
    // ------------------------------------------------------------------

    public static final class ComandoRedeConsumido extends DadosPessoaisEvent {
    }

    public static final class ComandoDialogConsumido extends DadosPessoaisEvent {
    }

    public static final class ComandoNavegacaoConsumido extends DadosPessoaisEvent {
    }

    public static final class ComandoAnalyticsConsumido extends DadosPessoaisEvent {
    }
}
