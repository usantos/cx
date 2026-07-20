package br.gov.caixa.loterias.apostas.effect;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;

public abstract class ScreenSimulaEffect {

    private ScreenSimulaEffect() {
    }

    public static final class AbrirSurpresinha extends ScreenSimulaEffect {

        private final boolean esconderQuantidadeNumeros;
        private final boolean esconderPaginacao;

        public AbrirSurpresinha(
                boolean esconderQuantidadeNumeros,
                boolean esconderPaginacao
        ) {
            this.esconderQuantidadeNumeros = esconderQuantidadeNumeros;
            this.esconderPaginacao = esconderPaginacao;
        }

    }
    public static final class AtualizarQuantidadeTrevos
            extends ScreenSimulaEffect {

        private final ParametroValorApostaDTO valorSelecionado;

        public AtualizarQuantidadeTrevos(
                ParametroValorApostaDTO valorSelecionado
        ) {
            this.valorSelecionado = valorSelecionado;
        }

        public ParametroValorApostaDTO getValorSelecionado() {
            return valorSelecionado;
        }
    }

    public static final class VoltarEtapa
            extends ScreenSimulaEffect {

        private final EtapaAposta etapa;

        public VoltarEtapa(EtapaAposta etapa) {
            this.etapa = etapa;
        }

        public EtapaAposta getEtapa() {
            return etapa;
        }
    }
}