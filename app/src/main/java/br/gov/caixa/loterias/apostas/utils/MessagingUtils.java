package br.gov.caixa.loterias.apostas.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.messaging.FirebaseMessaging;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.LoginActivity;

public class MessagingUtils {

    private static final String TAG = "MessagingUtils";

    private static void promptUserToEnableNotifications(Activity activity) {

        new AlertDialog.Builder(activity)
                .setTitle("Habilitar notificações")
                .setMessage("As notificações estão desativadas. Você gostaria de ativá-las?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openNotificationSettings(activity);
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private static void openNotificationSettings(Activity activity) {

        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName())
                .putExtra(Settings.EXTRA_CHANNEL_ID, activity.getString(R.string.default_notification_channel_id));
        activity.startActivity(intent);
    }


    public static void getMessageTeoken() {

//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//
//                        // Get new FCM registration token
//                        String token = task.getResult();
//
//                        //Log.d(TAG, token);
//                    }
//                });
    }

}
