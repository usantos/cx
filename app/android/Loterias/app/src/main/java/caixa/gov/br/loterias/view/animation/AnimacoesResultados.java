package caixa.gov.br.loterias.view.animation;

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

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;

/**
 * Created by cedesbr450 on 22/03/18.
 */

public class AnimacoesResultados {
    private LinearLayout linearLayoutTopModalidades;
    private LinearLayout linearLayoutTopResultados;
    private Context context;
    private int valorSizeEncolhido;
    private int valorSizeExpandido;

    public void AnimacaoDescerContent(int positionBottom){
        translatePosicaoConteudoAbaixar(positionBottom);
        aumentarLarguraConteudo();
    }

    public void AnimacaoSubirContent(int positionBottom){
        translatePosicaoConteudoSubir(positionBottom);
        dimininuirLarguraConteudo();
    }

    //init

    public AnimacoesResultados(LinearLayout linearLayoutTopModalidades, LinearLayout linearLayoutTopResultados, Context context, int valorSizeEncolhido, int valorSizeExpandido) {
        this.linearLayoutTopModalidades = linearLayoutTopModalidades;
        this.linearLayoutTopResultados = linearLayoutTopResultados;
        this.context = context;
        this.valorSizeEncolhido = valorSizeEncolhido;
        this.valorSizeExpandido = valorSizeExpandido;
    }

    //public
    private void translatePosicaoConteudoAbaixar(int positionBottom){

        linearLayoutTopModalidades.animate().translationX(0).translationY(positionBottom).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    //public
    private void translatePosicaoConteudoSubir(int positionBottom){

        linearLayoutTopModalidades.animate().translationX(0).translationY(positionBottom).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }


    private void aumentarLarguraConteudo(){
        ValueAnimator animator = ValueAnimator.ofInt(valorSizeEncolhido, valorSizeExpandido);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = linearLayoutTopResultados.getLayoutParams();
                layoutParams.width = val;
                linearLayoutTopResultados.setLayoutParams(layoutParams);
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
                ViewGroup.LayoutParams layoutParams = linearLayoutTopResultados.getLayoutParams();
                layoutParams.width = val;
                linearLayoutTopResultados.setLayoutParams(layoutParams);
            }
        });
        animator.setDuration(500);
        animator.start();
    }
}
