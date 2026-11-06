package br.gov.caixa.loterias.apostas.effect;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;

public abstract class CarrinhoEffect {

    private CarrinhoEffect() {
    }

    public static final class ExecutarAdicionarCarrinhoEffect extends CarrinhoEffect {

        private final IdentificaoDeUmaApostaDas8Modalidades aposta;
        private final BarraTituloDTO barraTituloDTO;

        public ExecutarAdicionarCarrinhoEffect(
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

    public static final class MostrarConfirmacaoLimiteDiario extends CarrinhoEffect {
    }

    public static final class AdicionarTimeSurpresa extends CarrinhoEffect {

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
    public static final class AdicionarSurpresinha
            extends CarrinhoEffect {

        private final IncluirSurpresinhaDTO dto;

        public AdicionarSurpresinha(
                IncluirSurpresinhaDTO dto
        ) {
            this.dto = dto;
        }

        public IncluirSurpresinhaDTO getDto() {
            return dto;
        }
    }

}