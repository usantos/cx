package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import androidx.core.content.res.ResourcesCompat;

import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;

public class FonteUtils {

    public static Typeface getFonte(FontCaixaEnum fontCaixaEnum) {
        Typeface typeface = ResourcesCompat.getFont(Aplicacao.application.getApplicationContext(), fontCaixaEnum.getId());
        return typeface;
    }

    public static SpannableStringBuilder textCaixaSTDBold(Context context, String texto) {
        try{
            Typeface font = FonteUtils.getFonte(FontCaixaEnum.BOLD);

            PositionStringBold positionStringBold = ViewUtils.positionString(texto);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(texto.replace("_", ""));
            spannableStringBuilder.setSpan(new CustomTypefaceSpan("", font), positionStringBold.getStart(), positionStringBold.getEnd(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            return spannableStringBuilder;
        }catch (Exception e){
            return  new SpannableStringBuilder().append(texto);
        }
    }

}
