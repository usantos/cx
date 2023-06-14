package br.gov.caixa.loterias.apostas.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;

public class MessagingService  extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            //String notificationBody = remoteMessage.getNotification().getBody();
            if (remoteMessage.getNotification().getBody() != null) {
                //sendNotification(remoteMessage.getNotification());
                sendNotificationHeadsUp(remoteMessage.getNotification());
            }
        }
    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        //Envia Token servidor Caixa
        //sendRegistrationToServer(token);
    }


    private void sendNotification(RemoteMessage.Notification notification) {
        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.trevo)
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getBody())
                        //.setColor(ContextCompat.getColor(this, R.color.branco))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    getString(R.string.default_notification_channel_descricao),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    //Notificacao com Banner - Utiliza a prioridade ALTA
    private void sendNotificationHeadsUp(RemoteMessage.Notification notification) {
        String CHANNEL_ID = getString(R.string.default_notification_channel_id);
        String CHANNEL_NAME = getString(R.string.default_notification_name);
        String CHANNEL_DESCRIPTION = getString(R.string.default_notification_channel_descricao);
        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        // Cria uma intent que será disparada quando o usuário clica na notificação
        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.trevo)             //Utilizado se o app estiver aberto, se fechado utiliza o do próprio app
                //.setSmallIcon(R.mipmap.ic_launcher)       //Icone do app
                .setContentTitle(notification.getTitle())   //Pode alterar o titulo/text se o app estiver aberto, se fechado utiliza o recebido na msg
                .setContentText(notification.getBody())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setSound(defaultSoundUri)
                //.setColor(ContextCompat.getColor(this, R.color.branco))
                .setAutoCancel(true)                        //Indica que a notificação será removida após o clicar nela
                .setContentIntent(pendingIntent);           // Redireciona após o clic

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.setSound(null, null); // Adicionado para suprimir o som
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        //Utilizar NotificationManagerCompat para compatibilizar versoes mais antigas
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int notificationId = 0; //(int) System.currentTimeMillis();
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

}
