package br.gov.caixa.loterias.apostas.utils;

import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;

public class FonteUtils {

    public static Typeface getFonte(FontCaixaEnum fontCaixaEnum) {
        Typeface typeface = ResourcesCompat.getFont(Aplicacao.application.getApplicationContext(), fontCaixaEnum.getId());
        return typeface;
    }

}
