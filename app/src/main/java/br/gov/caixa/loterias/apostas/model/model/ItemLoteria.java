package br.gov.caixa.loterias.apostas.model.model;

public abstract class ItemLoteria {

    public static class Titulo extends ItemLoteria {
        private String concurso;
        public Titulo(String concurso) {
            this.concurso = concurso;
        }

        public String getConcurso() {
            return concurso;
        }
    }

    public static class Corpo extends ItemLoteria {
        private String descricao;
        private String valor;

        public Corpo(String descricao, String valor) {
            this.descricao = descricao;
            this.valor = valor;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getValor() {
            return valor;
        }
    }

    public static class Divider extends ItemLoteria {}
}
