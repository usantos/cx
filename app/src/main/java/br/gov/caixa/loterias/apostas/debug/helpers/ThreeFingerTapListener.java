package br.gov.caixa.loterias.apostas.debug.helpers;

import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;

import br.gov.caixa.loterias.apostas.debug.activity.NetworkInspectorActivity;

public class ThreeFingerTapListener implements View.OnTouchListener {

    private final Activity activity;
    private boolean triggered = false;

    public ThreeFingerTapListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:

                if (event.getPointerCount() == 3 && !triggered) {
                    triggered = true;

                    activity.startActivity(
                            new Intent(activity, NetworkInspectorActivity.class)
                    );

                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                triggered = false;
                break;
        }

        return false;
    }
}
