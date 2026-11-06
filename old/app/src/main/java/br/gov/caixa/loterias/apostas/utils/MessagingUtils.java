package br.gov.caixa.loterias.apostas.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

//import com.google.firebase.messaging.FirebaseMessaging;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;

public class MessagingUtils {
    private static final String CHANNEL_ID = "CanalLoteriasId";
    private static final String CHANNEL_NAME = "Loterias";
    private static final String TAG = "MessagingUtils";
    private static final int NOTIFICATION_ID = 0; //Mesmo ID sobrepoe se existir a mensagem
    //private static final int NOTIFICATION_VARIAVEL = (int) System.currentTimeMillis();
    private static final int IMPORTANCE = NotificationManager.IMPORTANCE_HIGH; //IMPORTANCE_DEFAULT;
    private static final int PRIORITY = NotificationCompat.PRIORITY_HIGH; //PRIORITY_DEFAULT;

    public static void criaCanalNotificacaoApp(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE);
            channel.setDescription(context.getString(R.string.default_notification_channel_descricao));
            channel.setSound(null, null);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void criaNotificacaoSobreposta(Context context, String titulo, String texto) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(imagemPadrao())
                .setContentTitle(titulo)
                .setContentText(texto)
                .setPriority(PRIORITY);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void criaNotificacaoNova(Context context, String titulo, String texto) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(imagemPadrao())
                .setContentTitle(titulo)
                .setContentText(texto)
                .setPriority(PRIORITY);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            return;
        }
        notificationManager.notify(getNotificacaoVariavelId(), builder.build());
    }
    public static void criaNotificacaoNovaExpansivel(Context context, String titulo,
                                                     String texto, String textoExpansivel) {
        String channelId = context.getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.trevo)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(textoExpansivel))
                .setPriority(PRIORITY);

        enviaNotificacao(context, builder, false);
    }

    //Notificacao com Banner
    public static void sendNotificationHeadsUp(Context context, String titulo, String mensagem) {

        // Cria uma intent que será disparada quando o usuário clica na notificação
        Intent intent = new Intent(context, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(imagemPadrao())
                .setContentTitle(titulo)   //Pode alterar o titulo/text se o app estiver aberto, se fechado utiliza o recebido na msg
                .setContentText(mensagem)
                .setPriority(PRIORITY)
                //.setSound(defaultSoundUri)
                //.setColor(ContextCompat.getColor(this, R.color.branco))
                .setAutoCancel(true)                        //Indica que a notificação será removida após o clicar nela
                .setContentIntent(pendingIntent);           // Redireciona após o clic

        //Utilizar NotificationManagerCompat para compatibilizar versoes mais antigas
        enviaNotificacao(context, notificationBuilder, true);
    }

    private static void enviaNotificacao(Context context, NotificationCompat.Builder notificationBuilder,
                                         boolean sobrepoeNotificacao) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (sobrepoeNotificacao)
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        else
            notificationManager.notify(getNotificacaoVariavelId(), notificationBuilder.build());
    }

    private static int getNotificacaoVariavelId() {
        return (int) System.currentTimeMillis();
    }

    private static int imagemPadrao() {
        //return R.mipmap.ic_launcher; //Imagem do app
        return R.drawable.trevo;
    }

    public static void limpaNotificacoesDoApp(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

}
