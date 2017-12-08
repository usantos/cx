package caixa.gov.br.loterias.utils;

/**
 * Created by lcardoso on 15/09/2017.
 */

public enum TipoModalidadeEnum {
        MEGASENA("Mega-sena"),
        LOTOFACIL("Lotofácil"),
        QUINA("Quina"),
        DUPLASENA("Dupla Sena"),
        FEDERAL("Federal"),
        LOTECA("Loteca"),
        LOTOGOL("Lotogol"),
        LOTOMANIA("Lotomania"),
        TIMEMANIA("Timemania");

        private String text;

        TipoModalidadeEnum(String text) {
                this.text = text;
        }

        public String getText() {
                return this.text;
        }

        public static TipoModalidadeEnum fromString(String text) {
                for (TipoModalidadeEnum b : TipoModalidadeEnum.values()) {
                        if (b.text.equalsIgnoreCase(text)) {
                                return b;
                        }
                }
                return null;
        }
}
