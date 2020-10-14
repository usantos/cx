package br.gov.caixa.loterias.apostas.view.custom;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import br.gov.caixa.loterias.apostas.R;

/**
 * Created by cedesbr450 on 06/12/17.
 */

public class LoadingViewLoterias {

    public static AlertDialog show(Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.DialogFullScreenTheme);

        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        ImageView imageView = layout.findViewById(R.id.logoImg);
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(imageView, "rotation", imageView.getRotation() + 360).setDuration(1200);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();
        layout.setAlpha(0.0f);
        layout.animate().alpha(1.0f).setDuration(200).setStartDelay(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        AlertDialog dialog = dialogBuilder.setView(layout)
                .setCancelable(false)
                .create();
        dialog.show();

        return dialog;
    }
}
