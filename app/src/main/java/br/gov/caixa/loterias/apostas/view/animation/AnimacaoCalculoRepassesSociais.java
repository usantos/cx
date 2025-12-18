package br.gov.caixa.loterias.apostas.view.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RepassometroDTO;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

/**
 * Created by cedesbr450 on 07/03/18.
 */

public class AnimacaoCalculoRepassesSociais {

    private AnimacaoCalculoRepassesSociais(){ }

    public static void start(final TextView textView, double calculo, final RepassometroDTO repassesSociais){
        Date dataBase = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
        double oldValue ;
        try {
            if(repassesSociais.getDataBase() != null && !repassesSociais.getDataBase().isEmpty()){
                dataBase  = simpleDate.parse(repassesSociais.getDataBase());
            }
        } catch (ParseException e) {
            Log.e("ERRO_ACRS", e.toString());
        }
        Date todayDate = new Date();
        double seconds = ((double) todayDate.getTime() - dataBase.getTime()) / org.apache.commons.lang.time.DateUtils.MILLIS_PER_SECOND;
        oldValue = calculo;

        double arrecadacaoAcumulada = 0.0;
        if (repassesSociais.getArrecadacaoAcumulada() != null) {
            arrecadacaoAcumulada = repassesSociais.getArrecadacaoAcumulada().doubleValue() * 0.48;
        }
        if(repassesSociais.getArrecadacaoUltimaSemana() != null) {
            calculo = arrecadacaoAcumulada + ((repassesSociais.getArrecadacaoUltimaSemana().doubleValue() * 0.48) / 604800.0) * seconds;
        }
        oldValue = oldValue == 0 ? arrecadacaoAcumulada * 0.48 : oldValue;

        final double calculoFinal = calculo;

        ValueAnimator animator = new  ValueAnimator();
        animator.setObjectValues(oldValue, calculo);
        animator.addUpdateListener(animation -> {
            String valorUpdate = String.format("%.2f", animation.getAnimatedValue());
            valorUpdate = valorUpdate.replace(",", ".");
            String valorFormatado = ViewUtils.getMoedaFormat(new BigDecimal(valorUpdate));
            textView.setText(valorFormatado);

            String novoContentDescription = "Em " + Calendar.getInstance().get(Calendar.YEAR)
                    + " as Loterias CAIXA já destinaram " + valorFormatado + " para melhorar o país.";

            CharSequence atualContentDescription = textView.getContentDescription();
            if (atualContentDescription == null || !atualContentDescription.toString().equals(novoContentDescription)) {
                textView.setContentDescription(novoContentDescription);
            }

        });
        // problem here
        animator.setEvaluator(( TypeEvaluator<Double>)(fraction, startValue, endValue) -> (startValue + (endValue - startValue) * fraction));
        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                // done
                start(textView, calculoFinal, repassesSociais);
            }
        });
        animator.setDuration(1000);
        animator.setStartDelay(400);
        animator.start();
    }
}
