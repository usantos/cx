package br.gov.caixa.loterias.apostar.interno.view.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RepassometroDTO;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

/**
 * Created by cedesbr450 on 07/03/18.
 */

public class AnimacaoCalculoRepassesSociais {
    public static void start(final TextView textView, double oldValue, double calculo, final RepassometroDTO repassesSociais){
        Date dataBase = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
        try {
            dataBase  = simpleDate.parse(repassesSociais.getDataBase());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date todayDate = new Date();
        double seconds = (todayDate.getTime() - dataBase.getTime()) / org.apache.commons.lang.time.DateUtils.MILLIS_PER_SECOND;
        oldValue = calculo;

        double arrecadacaoAcumulada = 0.0;
        if (repassesSociais.getArrecadacaoAcumulada() != null) {
            arrecadacaoAcumulada = repassesSociais.getArrecadacaoAcumulada().doubleValue() * 0.48;
        }
        calculo = arrecadacaoAcumulada + ((repassesSociais.getArrecadacaoUltimaSemana().doubleValue() * 0.48) / 604800.0) * seconds;
        oldValue = oldValue == 0 ? arrecadacaoAcumulada * 0.48 : oldValue;

        final double calculoFinal = calculo;
        final double oldValueFinal = oldValue;

        ValueAnimator animator = new  ValueAnimator();
        animator.setObjectValues(oldValue, calculo); //double value
        animator.addUpdateListener(animation -> {
            String valorUpdate = String.format( "%.2f", animation.getAnimatedValue());
            valorUpdate = valorUpdate.replace(",", ".");
            textView.setText(ViewUtils.getMoedaFormat(new BigDecimal(valorUpdate)));
        });
        // problem here
        animator.setEvaluator(( TypeEvaluator<Double>)(fraction, startValue, endValue) -> (startValue + (endValue - startValue) * fraction));
        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                // done
                start(textView, oldValueFinal, calculoFinal, repassesSociais);
            }
        });
        animator.setDuration(1000);
        animator.setStartDelay(400);
        animator.start();
    }
}
