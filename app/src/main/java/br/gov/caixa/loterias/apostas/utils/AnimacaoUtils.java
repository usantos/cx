package br.gov.caixa.loterias.apostas.utils;

import android.animation.Animator;
import android.view.View;

import br.gov.caixa.loterias.apostas.model.bo.listener.OnAnimacaoViewListener;

public class AnimacaoUtils {
    private static final float ALPHA_FADE_OUT = 1.0f;

    public static void fadeIn(View view, int duracao, OnAnimacaoViewListener listener){
        view.animate().alpha(0.0f).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                listener.animacaoFinalizada();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    public static void fadeIn(View view, int traslationX, int translationY, int duracao, OnAnimacaoViewListener listener){
        view.animate().translationX(0).translationY(0).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                listener.animacaoFinalizada();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    public static void fadeOut(View view, int translationX, int translationY, int duracao, OnAnimacaoViewListener listener){
        view.animate().translationX(translationX).translationY(translationY).setDuration(duracao).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                listener.animacaoFinalizada();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    public static void fadeOut(View view, int duracao, OnAnimacaoViewListener listener){
        view.animate().alpha(ALPHA_FADE_OUT).setDuration(duracao).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                listener.animacaoFinalizada();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    public static void fadeOut(View view, float alpha, int duracao, OnAnimacaoViewListener listener){
        view.animate().alpha(alpha).setDuration(duracao).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                listener.animacaoFinalizada();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

}
