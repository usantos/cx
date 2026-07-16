package br.gov.caixa.loterias.apostas.state;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;

public abstract class ComandoFavoritarAposta {

    public static class ConfirmarValidacaoNegocial
            extends ComandoFavoritarAposta {

        private final ApostaFavoritaDTO aposta;
        private final String mensagem;

        public ConfirmarValidacaoNegocial(
                ApostaFavoritaDTO aposta,
                String mensagem
        ) {
            this.aposta = aposta;
            this.mensagem = mensagem;
        }

        public ApostaFavoritaDTO getApostaFavorita() {
            return aposta;
        }

        public String getMensagem() {
            return mensagem;
        }
    }

    public static class Sucesso
            extends ComandoFavoritarAposta {
    }

    public static class RedirecionarErro
            extends ComandoFavoritarAposta {

        private final VolleyError error;

        public RedirecionarErro(
                VolleyError error
        ) {
            this.error = error;
        }

        public VolleyError getError() {
            return error;
        }
    }
}
