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
    private static Context context;

    public static void show(Context contextParam) {
        context = contextParam;
        alertDialog = LoadingViewLoterias.show(context);
    }

    public static void dismiss() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }
}
