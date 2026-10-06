package br.gov.caixa.loterias.apostas.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";
    private static final String TOKEN_DISPOSITIVO = "TOKEN_DISPOSITIVO";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() == null) {
            if (remoteMessage.getData() != null) {
                MessagingUtils.sendNotificationHeadsUp(this,
                        remoteMessage.getData().get("title"),
                        remoteMessage.getData().get("message"));
            }
        } else if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getBody() != null) {
                MessagingUtils.sendNotificationHeadsUp(this,
                        remoteMessage.getNotification().getTitle(),
                        remoteMessage.getNotification().getBody());
            }
        }
    }

//    @Override
//    public void onNewToken(String token) {
//        Log.d(TAG, "Refreshed token: " + token);
//
//        //Salva Token para futuro envio para o Servico Caixa de Mensagens
//        SharedPreferencesUtils.setValor(TOKEN_DISPOSITIVO, token);
//    }

}
