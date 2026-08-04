package br.gov.caixa.loterias.apostas.effect;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

public abstract class AnalyticsEffect {

    private final ModalidadeEnum modalidade;

    protected AnalyticsEffect(ModalidadeEnum modalidade) {
        this.modalidade = modalidade;
    }

    public String getModalidade() {
        return ModalidadeEnum.fromString(modalidade);
    }

    public static class QuantidadeNumerosConfirmada
            extends AnalyticsEffect {

        private final String valorSelecionado;

        public QuantidadeNumerosConfirmada(
                ModalidadeEnum modalidade,
                String valorSelecionado
        ) {
            super(modalidade);
            this.valorSelecionado = valorSelecionado;
        }

        public String getValorSelecionado() {
            return valorSelecionado;
        }
    }

    public static class TeimosinhaConfirmada
            extends AnalyticsEffect {

        private final String valorSelecionado;

        public TeimosinhaConfirmada(
                ModalidadeEnum modalidade,
                String valorSelecionado
        ) {
            super(modalidade);
            this.valorSelecionado = valorSelecionado;
        }

        public String getValorSelecionado() {
            return valorSelecionado;
        }
    }

    public static class QtdSurpresinhas
            extends AnalyticsEffect {

        private final int qtdSurpresinhas;

        public QtdSurpresinhas(
                ModalidadeEnum modalidade,
                int qtdSurpresinhas
        ) {
            super(modalidade);
            this.qtdSurpresinhas = qtdSurpresinhas;
        }

        public int getQtdSurpresinhas() {
            return qtdSurpresinhas;
        }
    }

    public static class FavoritarAposta
            extends AnalyticsEffect {

        public FavoritarAposta(
                ModalidadeEnum modalidade
        ) {
            super(modalidade);
        }
    }

    public static class AdicionarCarrinho
            extends AnalyticsEffect {

        public AdicionarCarrinho(
                ModalidadeEnum modalidade
        ) {
            super(modalidade);
        }
    }

    public static class NextStep
            extends AnalyticsEffect {

        public NextStep(
                ModalidadeEnum modalidade
        ) {
            super(modalidade);
        }
    }

    public static class CompletarAposta
            extends AnalyticsEffect {

        public CompletarAposta(
                ModalidadeEnum modalidade
        ) {
            super(modalidade);
        }
    }
}