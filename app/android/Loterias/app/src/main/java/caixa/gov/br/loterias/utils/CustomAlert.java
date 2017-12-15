package caixa.gov.br.loterias.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import caixa.gov.br.loterias.R;

public class CustomAlert {

    private static final String TAG = "TAG_CUSTOM_ALERT";

    public static void showAlertMessage(Context context, int titleId, int titleMessage) {
        showAlertMessage(context, titleId, titleMessage, true);
    }

    public static void showAlertMessage(Context context, int titleId, int titleMessage, Boolean canCancel) {
        showAlertMessage(context, context.getResources().getString(titleId), context.getResources().getString(titleMessage), canCancel);
    }

    public static void showAlertMessage(Context context, String title, String titleMessage, Boolean canCancel) {
        try {
            AlertDialog.Builder builder;

            builder = new AlertDialog.Builder(context);

            builder.setTitle(title);
            builder.setMessage(titleMessage);
            builder.setPositiveButton(R.string.ok, null);
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
