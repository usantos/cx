package br.gov.caixa.loterias.apostas.effect;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;

public abstract class FavoritarApostaEffect {

    public static class ConfirmarValidacaoNegocial
            extends FavoritarApostaEffect {

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
            extends FavoritarApostaEffect {
    }

    public static class RedirecionarErro
            extends FavoritarApostaEffect {

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
