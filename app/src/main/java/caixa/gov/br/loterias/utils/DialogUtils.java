package caixa.gov.br.loterias.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

/**
 * Created by joafilho on 10/04/2018.
 * Class DialogUtils
 */

public class DialogUtils {

    private static AlertDialog alertDialog;

    public static void show(Context contextParam) {
        alertDialog = LoadingViewLoterias.show(contextParam);
    }

    public static void dismiss() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }
}
