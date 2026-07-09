package br.gov.caixa.loterias.apostas.state;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;

public abstract class ComandoCarrinho {

    private ComandoCarrinho() {
    }

    public static final class ExecutarAdicionarCarrinho extends ComandoCarrinho {

        private final IdentificaoDeUmaApostaDas8Modalidades aposta;
        private final BarraTituloDTO barraTituloDTO;

        public ExecutarAdicionarCarrinho(
                IdentificaoDeUmaApostaDas8Modalidades aposta,
                BarraTituloDTO barraTituloDTO
        ) {
            this.aposta = aposta;
            this.barraTituloDTO = barraTituloDTO;
        }

        public IdentificaoDeUmaApostaDas8Modalidades getAposta() {
            return aposta;
        }

        public BarraTituloDTO getBarraTituloDTO() {
            return barraTituloDTO;
        }
    }

    public static final class MostrarConfirmacaoLimiteDiario extends ComandoCarrinho {
    }

    public static final class AdicionarTimeSurpresa extends ComandoCarrinho {

        private final ParametroEquipe equipeSelecionada;

        public AdicionarTimeSurpresa(
                ParametroEquipe equipeSelecionada
        ) {
            this.equipeSelecionada = equipeSelecionada;
        }

        public ParametroEquipe getEquipeSelecionada() {
            return equipeSelecionada;
        }
    }
}