package br.gov.caixa.loterias.apostas.view.animation;

import android.content.Context;
import android.view.animation.AnimationUtils;

import br.gov.caixa.loterias.apostas.utils.SwipeLayout;

import br.gov.caixa.loterias.apostas.R;

/**
 * Created by joafilho on 05/02/2018.
 * Class AnimacaoSwipeList
 */

public class AnimacaoSwipeList {


    public static void animacaoSwipeList(final Context context, final int position, final SwipeLayout swipeLayout) {
        swipeLayout.post(() -> {
            swipeLayout.open();
            swipeLayout.postDelayed(() -> {
                swipeLayout.close();
                swipeLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce_left));
            }, (1200 + (position == 0 ? position + 2 : position * 8)));
        });
    }

}
