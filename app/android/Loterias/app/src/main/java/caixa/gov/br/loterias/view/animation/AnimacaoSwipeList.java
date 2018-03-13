package caixa.gov.br.loterias.view.animation;

import android.content.Context;
import android.view.animation.AnimationUtils;

import com.daimajia.swipe.SwipeLayout;

import caixa.gov.br.loterias.R;

/**
 * Created by joafilho on 05/02/2018.
 * Class AnimacaoSwipeList
 */

public class AnimacaoSwipeList {


    public static void animacaoSwipeList(final Context context, final int position, final SwipeLayout swipeLayout) {
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.open();
                swipeLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.close();
                        swipeLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce_left));
                    }
                }, (1000 + (position == 0 ? position + 2 : position * 8)));
            }
        });
    }

}
