package br.gov.caixa.loterias.apostas.state;

public abstract class ComandoTelaSimula {

    private ComandoTelaSimula() {
    }

    public static final class AbrirSurpresinha extends ComandoTelaSimula {

        private final boolean esconderQuantidadeNumeros;
        private final boolean esconderPaginacao;

        public AbrirSurpresinha(
                boolean esconderQuantidadeNumeros,
                boolean esconderPaginacao
        ) {
            this.esconderQuantidadeNumeros = esconderQuantidadeNumeros;
            this.esconderPaginacao = esconderPaginacao;
        }

        public boolean isEsconderQuantidadeNumeros() {
            return esconderQuantidadeNumeros;
        }

        public boolean isEsconderPaginacao() {
            return esconderPaginacao;
        }
    }
    public static final class VoltarCartela
            extends ComandoTelaSimula {
    }
}