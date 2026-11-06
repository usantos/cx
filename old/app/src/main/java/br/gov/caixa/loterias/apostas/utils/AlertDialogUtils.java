package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

/**
 * Created by joafilho on 10/04/2018.
 * Class DialogUtils
 */

public class AlertDialogUtils {

    private static AlertDialog alertDialog;

    public static void show(Context contextParam) {
        try{
            if(!isShow()){
                alertDialog = LoadingViewLoterias.show(contextParam);
            }
        }catch (Exception e){}
    }

    public static boolean isShow(){
        try {
            if(alertDialog != null){
                return alertDialog.isShowing();
            }
            return false;
        } catch (Exception e){
            return false;
        }
    }

    public static void dismiss() {
        try {
            if (alertDialog != null && isShow()) {
                alertDialog.dismiss();
                alertDialog = null;
            }
        } catch (Exception e){}
    }

}
