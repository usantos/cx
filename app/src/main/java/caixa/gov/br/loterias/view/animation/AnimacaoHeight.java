package caixa.gov.br.loterias.view.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

public class AnimacaoHeight {
    public static void animateHeight(final View view, final int newHeight, ValueAnimator.AnimatorUpdateListener animatorUpdateListener, Animator.AnimatorListener animationListener) {
        ValueAnimator anim = ValueAnimator.ofInt( view.getMeasuredHeight(), newHeight );

        if (animatorUpdateListener == null){
            animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height = val;
                    view.setLayoutParams(layoutParams);
                }
            };
        }
        anim.addUpdateListener(animatorUpdateListener);

        if (animationListener == null){
            animationListener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setEnabled(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            };
        }

        anim.addListener( animationListener );
        anim.setDuration(500);
        anim.start();
    }
}
