package caixa.gov.br.loterias.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import caixa.gov.br.loterias.R;


/**
 * Created by cedesbr450 on 06/12/17.
 */

public class LoadingViewLoterias {
    public static LinearLayout init(Activity activity) {
        Context context = activity.getApplicationContext();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(context.getResources().getColor(R.color.transparenteAzul10));
        layout.setGravity(Gravity.CENTER);
        layout.setAlpha(0.0f);
        layout.setClickable(true);
        ImageView imageView = new ImageView(context);
        int sizeImage = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, activity.getResources().getDisplayMetrics());
        int margemImage = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, activity.getResources().getDisplayMetrics());


        RelativeLayout.LayoutParams lpImage = new RelativeLayout.LayoutParams(sizeImage, sizeImage);
        imageView.setLayoutParams(lpImage);
        imageView.setImageResource(R.drawable.icon_loteria);
        layout.addView(imageView);

        TextView textView = new TextView(context);
        RelativeLayout.LayoutParams lptextView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lptextView.setMargins(0,margemImage,0,0);
        textView.setLayoutParams(lptextView);
        textView.setText("Carregando...");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(context.getResources().getColor(R.color.branco));
        textView.setTypeface(ViewUtils.getFontFutura(activity));
        layout.addView(textView);


        ObjectAnimator animator = ObjectAnimator
                .ofFloat(imageView, "rotation", imageView.getRotation() + 360).setDuration(1200);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();
        activity.getWindow().addContentView(layout,layoutParams);

        layout.animate().alpha(1.0f).setDuration(200).setListener(new Animator.AnimatorListener() {
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

        return layout;
        }

        public static void stop(View view){
            ViewGroup viewParent = (ViewGroup) view.getParent();
            viewParent.removeView(view);
        }
}
