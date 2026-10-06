package br.gov.caixa.loterias.apostas.model.enums;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import br.gov.caixa.loterias.apostas.R;

public enum TipoApostaLinhaEnum {
    BOLAO(R.drawable.ic_bolao_linha, R.string.tipo_aposta_bolao),
    COMBO(R.drawable.ic_combo_linha, R.string.tipo_aposta_combo),
    ESPELHO(R.drawable.ic_espelho_linha, R.string.tipo_aposta_espelho),

    SURPRESINHA(R.drawable.ic_surpresinha_linha, R.string.tipo_aposta_surpresinha),
    TEIMOSINHA(R.drawable.ic_teimosinha_linha, R.string.tipo_aposta_teimosinha),
    TROCA(R.drawable.ic_troca_linha, R.string.tipo_aposta_troca);

    @DrawableRes
    private final int iconResId;
    @StringRes
    private final int textResId;


    TipoApostaLinhaEnum(@DrawableRes int iconResId, @StringRes int textResId) {
        this.iconResId = iconResId;
        this.textResId = textResId;
    }

    public  int getIconResId() {
        return iconResId;
    }

    public  int getTextResId() {
        return textResId;
    }
}
