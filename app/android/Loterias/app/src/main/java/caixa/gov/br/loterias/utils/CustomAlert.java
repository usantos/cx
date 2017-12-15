package caixa.gov.br.loterias.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import caixa.gov.br.loterias.R;

public class CustomAlert {

    private static final String TAG = "TAG_CUSTOM_ALERT";

    public static void showAlertMessage(Context context, int titleId, int titleMessage, String buttonName) {
        showAlertMessage(context, titleId, titleMessage, true, buttonName);
    }

    public static void showAlertMessage(Context context, int titleId, int titleMessage, Boolean canCancel,String buttonName) {
        showAlertMessage(context, context.getResources().getString(titleId), context.getResources().getString(titleMessage), canCancel,
                buttonName);
    }

    public static void showAlertMessage(Context context, String title, String titleMessage, Boolean canCancel, String buttonName) {
        try {
            AlertDialog.Builder builder;

            builder = new AlertDialog.Builder(context);

            builder.setTitle(title);
            builder.setMessage(titleMessage);

            if (!AppUtils.isStringEmpty(buttonName)) {
                builder.setPositiveButton(buttonName, null);
            }else{
                builder.setPositiveButton(R.string.ok, null);
            }
            builder.setCancelable(canCancel);
            builder.show();

        } catch (Exception ex) {
            Log.e(TAG, Log.getStackTraceString(ex));
        }
    }


    public interface OnMessageButtonPress {
        void onMessageButtonPress(boolean okButtonClick);
    }

    public interface OnMessageButtonPressWithLayout {
        void onMessageButtonPress(boolean okButtonClick, View layout);
    }

    public interface OnMessageButtonPressWithInput {
        void onMessageButtonPress(boolean okButtonClick, EditText editText);
    }
}
