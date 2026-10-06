package br.gov.caixa.loterias.apostas.view.custom;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import br.gov.caixa.loterias.apostas.R;

public class LottieManager {

    private static final float DEFAULT_SPEED = 1.0f;
    private static final int DEFAULT_REPEAT_COUNT  = 1;
    private float pendingSpeed = DEFAULT_SPEED;
    private int pendingRepeatCount = DEFAULT_REPEAT_COUNT;
    private static final String PREF_NAME = "LottiePref";
    private static final String PREF_POPUP_SHOW_PREFIX = "popup_show_";
    private static final String PREF_DISABLE_ANIMATION_PREFIX = "animation_disabled_";
    private final Activity activity;
    private final int rawRes;
    private LottieAnimationView lottieView;
    private boolean isDismissed = false;

    public LottieManager(Activity activity, int rawRes) {
        this.activity = activity;
        this.rawRes = rawRes;
    }

    public void start() {
        if (isAnimationDisable(rawRes)) {
            return;
        }
        if (lottieView != null) return; //ja inciado

        lottieView = buildLottieView();

        ViewGroup root = activity.findViewById(android.R.id.content);
        root.addView(lottieView);

        lottieView.post(() -> {
           if(lottieView != null && !lottieView.isAnimating()) {
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
        isDismissed = true;
        if(lottieView != null) {
            lottieView.cancelAnimation();

            ViewGroup parent = (ViewGroup) lottieView.getParent();
            if(parent != null) {
                parent.removeView(lottieView);
            }
        }
    }

    public void dismissPresentation() {
        if (isDismissed) return;;
        isDismissed = true;

        if (lottieView != null) {
            lottieView.cancelAnimation();

            ViewGroup parent = (ViewGroup) lottieView.getParent();
            if (parent != null) {
                parent.removeView(lottieView);
            }
            lottieView = null;
        }

        if (!isPopupShow(rawRes)) {
            showOcultarDialog();
        }
    }

    public LottieAnimationView getLottieView() {
        return lottieView;
    }

    public void setSpeed(float speed) {
        pendingSpeed = speed;
        if (lottieView != null) {
            lottieView.setSpeed(speed);
        }
    }

    public void setRepeatCount(int count) {
        pendingRepeatCount = count;
        int lottieCount = (count == 0) ? LottieDrawable.INFINITE : (count -1);
        lottieView.setRepeatCount(lottieCount);
    }

    private LottieAnimationView buildLottieView() {
        LottieAnimationView view = new LottieAnimationView(activity);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        view.setLayoutParams(params);

        view.setBackgroundColor(Color.TRANSPARENT);
        view.setClickable(true);
        view.setFocusable(true);

        view.setOnClickListener( v -> dismissPresentation());

        view.setAnimation(rawRes);
        view.setSpeed(pendingSpeed);

        int lottieCount = (pendingRepeatCount == 0) ? LottieDrawable.INFINITE : (pendingRepeatCount -1);
        view.setRepeatCount(lottieCount);

        view.addAnimatorListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                dismissPresentation();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });

        return view;
    }

    private String getResourceKey(int rawRes) {
        try {
            return activity.getResources().getResourceEntryName(rawRes);
        } catch (Exception e ) {
            return String.valueOf(rawRes);
        }
    }
    private boolean isPopupShow(int rawRes) {
        String key = PREF_POPUP_SHOW_PREFIX + getResourceKey(rawRes);
        SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }
    private void setPopupShow(int rawRes, boolean show) {
        String key = PREF_POPUP_SHOW_PREFIX + getResourceKey(rawRes);
        SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, show).apply();
    }
    private boolean isAnimationDisable(int rawRes) {
        String key = PREF_DISABLE_ANIMATION_PREFIX + getResourceKey(rawRes);
        SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }
    private void setAnimationDisable(int rawRes, boolean disable) {
        String key = PREF_DISABLE_ANIMATION_PREFIX + getResourceKey(rawRes);
        SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, disable).apply();
    }


    private void showOcultarDialog() {
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        new AlertDialog.Builder(activity)
                .setMessage(R.string.animacao_msg)
                .setPositiveButton("Sim", (DialogInterface dialog, int which) -> {
                    setPopupShow(rawRes, true);
                    setAnimationDisable(rawRes, false);
                })
                .setNegativeButton("Não", (DialogInterface dialog, int which) -> {
                    setPopupShow(rawRes, true);
                    setAnimationDisable(rawRes, true);
                })
                .setCancelable(false)
                .show();
    }
}
