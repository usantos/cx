package br.gov.caixa.loterias.apostar.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import br.gov.caixa.loterias.apostar.view.custom.LoadingViewLoterias;

/**
 * Created by joafilho on 10/04/2018.
 * Class DialogUtils
 */

public class DialogUtils {

    private static AlertDialog alertDialog;

    public static void show(Context contextParam) {
        alertDialog = LoadingViewLoterias.show(contextParam);
    }

    public static boolean isShow(){
        if(alertDialog != null){
            return alertDialog.isShowing();
        }
        return false;
    }

    public static void dismiss() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }
}
