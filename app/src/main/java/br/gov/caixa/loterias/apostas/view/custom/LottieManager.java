package br.gov.caixa.loterias.apostas.view.custom;

import android.app.Activity;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class LottieManager {

    private static final float DEFAULT_SPEED = 1.0f;
    private static final int DEFAULT_REPEAT_COUNT  = 1;
    private float pendingSpeed = DEFAULT_SPEED;
    private int pendingRepeatCount = DEFAULT_REPEAT_COUNT;
    private final Activity activity;
    private final int rawRes;
    private LottieAnimationView lottieView;

    public LottieManager(Activity activity, int rawRes) {
        this.activity = activity;
        this.rawRes = rawRes;
    }

    public void start() {
        if (lottieView != null) return;

        lottieView = buildLottieView();

        ViewGroup root = activity.findViewById(android.R.id.content);
        root.addView(lottieView);

        lottieView.post(() -> {
           if(!lottieView.isAnimating()) {
               lottieView.playAnimation();
           }
        });
    }

    public void resume() {
        if(lottieView != null && !lottieView.isAnimating()) {
            lottieView.resumeAnimation();
        }
    }

    public void pause() {
        if(lottieView != null && !lottieView.isAnimating()) {
            lottieView.pauseAnimation();;
        }
    }

    public void destroy() {
        if(lottieView != null) {
            lottieView.cancelAnimation();

            ViewGroup parent = (ViewGroup) lottieView.getParent();
            if(parent != null) {
                parent.removeView(lottieView);
            }
        }
    }

    private LottieAnimationView buildLottieView() {
        LottieAnimationView view = new LottieAnimationView(activity);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        view.setLayoutParams(params);

        view.setBackgroundColor(Color.TRANSPARENT);
        view.setClickable(false);
        view.setFocusable(false);

        view.setAnimation(rawRes);
        view.setSpeed(pendingSpeed);

        int lottieCount = (pendingRepeatCount == 0) ? LottieDrawable.INFINITE : (pendingRepeatCount -1);
        view.setRepeatCount(lottieCount);

        return view;
    }

}
