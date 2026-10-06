package br.gov.caixa.loterias.apostas.utils;

import android.util.TypedValue;
import android.widget.TextView;

public class TextViewUtils {
    public static void mudarTamanhoPorPorcentagem(TextView textView, float porcentagem){
        float tamanhoAtual = textView.getTextSize();
        float tamanhoAtualSP = tamanhoAtual / textView.getResources().getDisplayMetrics().scaledDensity;
        float novoTamanho = tamanhoAtualSP * (1 + (porcentagem / 100f));

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, novoTamanho);
    }

}
