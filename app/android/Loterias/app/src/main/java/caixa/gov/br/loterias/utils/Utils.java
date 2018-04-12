package caixa.gov.br.loterias.utils;

import android.os.Handler;

/**
 * Created by cedesbr450 on 12/04/18.
 */

public class Utils {


    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(int secs, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, secs * 1000);
    }
}
