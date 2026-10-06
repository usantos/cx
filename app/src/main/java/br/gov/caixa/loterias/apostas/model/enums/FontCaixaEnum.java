
package br.gov.caixa.loterias.apostas.model.enums;

import br.gov.caixa.loterias.apostas.R;

public enum FontCaixaEnum {
    BOLD(R.font.caixa_std_bold),
    REGULAR(R.font.caixa_std_regular),
    ITALIC(R.font.caixa_std_italic),
    BOLD_ITALIC(R.font.caixa_std_bold_italic),
    BOOK(R.font.caixa_std_book),
    BOOK_ITALIC(R.font.caixa_std_book_italic),
    EXTRA_BOLD(R.font.caixa_std_extra_bold),
    EXTRA_BOLD_ITALIC(R.font.caixa_std_extra_bold_italic),
    LIGHT(R.font.caixa_std_light),
    LIGHT_ITALIC(R.font.caixa_std_light_italic),
    SEMI_BOLD(R.font.caixa_std_semi_bold),
    SEMI_BOLD_ITALIC(R.font.caixa_std_semi_bold_italic);

    private int id;

    FontCaixaEnum(int valor){
        this.id = valor;
    }

    public int getId() {
        return id;
    }
}
