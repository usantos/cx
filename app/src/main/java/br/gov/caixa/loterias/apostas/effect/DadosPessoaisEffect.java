package br.gov.caixa.loterias.apostas.effect;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum;

/**
 * Comandos emitidos pela ViewModel dentro do DadosPessoaisState.

 * Nao usa SingleLiveEvent: cada comando fica em um slot do state e a
 * Activity devolve um evento ...Consumido para limpar o slot, no mesmo
 * padrao de CarrinhoEffect/AnalyticsEffect da SimulaViewModel.
 */
public abstract class DadosPessoaisEffect {

    private DadosPessoaisEffect() {
        // evita subclasses fora deste arquivo
    }

    public enum CampoObrigatorio {
        CEP,
        UF,
        MUNICIPIO
    }

    // ------------------------------------------------------------------
    // Slot: comandoRede
    // ------------------------------------------------------------------

    public static final class ErroDeRede extends DadosPessoaisEffect {
        private final VolleyError error;

        public ErroDeRede(VolleyError error) {
            this.error = error;
        }

        public VolleyError getError() {
            return error;
        }
    }

    public static final class Redirecionar extends DadosPessoaisEffect {
        private final RedirectEnum redirect;

        public Redirecionar(RedirectEnum redirect) {
            this.redirect = redirect;
        }

        public RedirectEnum getRedirect() {
            return redirect;
        }
    }

    // ------------------------------------------------------------------
    // Slot: comandoDialog
    // ------------------------------------------------------------------

    public static final class CamposObrigatoriosPendentes extends DadosPessoaisEffect {
        private final List<CampoObrigatorio> campos;

        public CamposObrigatoriosPendentes(List<CampoObrigatorio> campos) {
            this.campos = campos;
        }

        public List<CampoObrigatorio> getCampos() {
            return campos;
        }
    }

    public static final class MostrarAvisoNotificacao extends DadosPessoaisEffect {
        private final boolean ativado;

        public MostrarAvisoNotificacao(boolean ativado) {
            this.ativado = ativado;
        }

        public boolean isAtivado() {
            return ativado;
        }
    }

    public static final class ConfirmarSaidaParaCorreios extends DadosPessoaisEffect {
    }

    // ------------------------------------------------------------------
    // Slot: comandoNavegacao
    // ------------------------------------------------------------------

    public static final class CadastroAtualizado extends DadosPessoaisEffect {
    }

    public static final class AbrirSiteCorreios extends DadosPessoaisEffect {
    }

    public static final class FecharTela extends DadosPessoaisEffect {
    }

    // ------------------------------------------------------------------
    // Slot: comandoAnalytics
    // ------------------------------------------------------------------

    public static final class RegistrarEvento extends DadosPessoaisEffect {
        public enum Tipo {
            ENTROU_EDITAR_CADASTRO,
            EDICAO_CADASTRO_SUCESSO,
            ENTROU_RECALCULAR_LIMITE
        }

        private final Tipo tipo;

        public RegistrarEvento(Tipo tipo) {
            this.tipo = tipo;
        }

        public Tipo getTipo() {
            return tipo;
        }
    }
}
