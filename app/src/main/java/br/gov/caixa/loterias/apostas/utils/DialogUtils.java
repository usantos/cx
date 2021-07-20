package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

/**
 * Created by joafilho on 10/04/2018.
 * Class DialogUtils
 */

public class DialogUtils {

    private static AlertDialog alertDialog;

    public static void show(Context contextParam) {
        if(alertDialog != null){
            alertDialog.dismiss();
        }
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
