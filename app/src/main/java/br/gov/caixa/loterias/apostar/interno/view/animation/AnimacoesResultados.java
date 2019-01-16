package br.gov.caixa.loterias.apostar.interno.view.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

/**
 * Created by cedesbr450 on 22/03/18.
 */

public class AnimacoesResultados {
    private RelativeLayout relativeLayoutTopResultados;
    private LinearLayout linearLayoutResultadosApostas;
    private Context context;
    private int valorSizeExpandido;
    private int valorSizeEncolhido;

    public void AnimacaoDescerContent(int alturaConteudo){
        aumentarAlturaConteudo(alturaConteudo);
        aumentarLarguraConteudo();
    }

    public void AnimacaoSubirContent(){
        diminuirAlturaConteudo();
        dimininuirLarguraConteudo();
    }

    //init

    public AnimacoesResultados(RelativeLayout relativeLayoutTopResultados, LinearLayout  linearLayoutResultadosApostas,Context context, int valorSizeEncolhido, int valorSizeExpandido) {
        this.relativeLayoutTopResultados = relativeLayoutTopResultados;
        this.linearLayoutResultadosApostas = linearLayoutResultadosApostas;
        this.context = context;
        this.valorSizeExpandido = valorSizeExpandido;
        this.valorSizeEncolhido = valorSizeEncolhido;
    }

    private void aumentarLarguraConteudo(){
        ValueAnimator animator = ValueAnimator.ofInt(valorSizeEncolhido, valorSizeExpandido);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = relativeLayoutTopResultados.getLayoutParams();
                layoutParams.width = val;
                relativeLayoutTopResultados.setLayoutParams(layoutParams);

                ViewGroup.LayoutParams layoutParamsScroll = linearLayoutResultadosApostas.getLayoutParams();
                layoutParamsScroll.width = val;
                linearLayoutResultadosApostas.setLayoutParams(layoutParamsScroll);
            }
        });
        animator.setDuration(500);
        animator.start();
    }

    private void dimininuirLarguraConteudo(){
        ValueAnimator animator = ValueAnimator.ofInt(valorSizeExpandido, valorSizeEncolhido);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = relativeLayoutTopResultados.getLayoutParams();
                layoutParams.width = val;
                relativeLayoutTopResultados.setLayoutParams(layoutParams);

                ViewGroup.LayoutParams layoutParamsScroll = linearLayoutResultadosApostas.getLayoutParams();
                layoutParamsScroll.width = val;
                linearLayoutResultadosApostas.setLayoutParams(layoutParamsScroll);
            }
        });
        animator.setDuration(500);
        animator.start();
    }


    private void aumentarAlturaConteudo(int alturaConteudo){
        ValueAnimator animator = ValueAnimator.ofInt(linearLayoutResultadosApostas.getHeight(), alturaConteudo);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = linearLayoutResultadosApostas.getLayoutParams();
                layoutParams.height = val;
                linearLayoutResultadosApostas.setLayoutParams(layoutParams);
            }
        });
        animator.setDuration(500);
        animator.start();
    }

    private void diminuirAlturaConteudo(){
        ValueAnimator animator = ValueAnimator.ofInt(linearLayoutResultadosApostas.getHeight(), 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = linearLayoutResultadosApostas.getLayoutParams();
                layoutParams.height = val;
                linearLayoutResultadosApostas.setLayoutParams(layoutParams);
            }
        });
        animator.setDuration(500);
        animator.start();
    }
}
