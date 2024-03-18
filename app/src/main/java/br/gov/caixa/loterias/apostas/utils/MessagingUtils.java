package br.gov.caixa.loterias.apostas.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

//import com.google.firebase.messaging.FirebaseMessaging;

import br.gov.caixa.loterias.apostas.R;

public class MessagingUtils {

    private static final String TAG = "MessagingUtils";
    private static final int NOTIFICATION_ID = 1; //Mesmo ID sobrepoe se existir a mensagem
    private static final int NOTIFICATION_VARIAVEL = (int) System.currentTimeMillis();

    //IMPORTANCE Utilizado antes do Android 8 (API 26)
    private static final int IMPORTANCE = NotificationManager.IMPORTANCE_HIGH; //IMPORTANCE_DEFAULT;
    //PRIORITI Utilizado durante e após o Android 8 (API 26)
    private static final int PRIORITY  = NotificationCompat.PRIORITY_HIGH; //PRIORITY_DEFAULT;

    public static void criaCanalNotificacaoApp(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = context.getString(R.string.default_notification_channel_id);
            String channelName = context.getString(R.string.default_notification_name);
            String channelDescription = context.getString(R.string.default_notification_channel_descricao);

            NotificationChannel channel = new NotificationChannel(channelId, channelName, IMPORTANCE);
            channel.setDescription(channelDescription);
            channel.setSound(null, null); // Adicionado para suprimir o som
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void criaNotificacaoSobreposta(Context context, String titulo, String texto) {
        String channelId = context.getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.trevo)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setPriority(PRIORITY);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void criaNotificacaoNova(Context context, String titulo, String texto) {
        String channelId = context.getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.trevo)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setPriority(PRIORITY);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_VARIAVEL, builder.build());
    }

    public static void limpaNotificacoesDoApp(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

//    private static void promptUserToEnableNotifications(Activity activity) {
//
//        new AlertDialog.Builder(activity)
//                .setTitle("Habilitar notificações")
//                .setMessage("As notificações estão desativadas. Você gostaria de ativá-las?")
//                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        openNotificationSettings(activity);
//                    }
//                })
//                .setNegativeButton("Não", null)
//                .show();
//    }
//
//    private static void openNotificationSettings(Activity activity) {
//
//        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
//                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                .putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName())
//                .putExtra(Settings.EXTRA_CHANNEL_ID, activity.getString(R.string.default_notification_channel_id));
//        activity.startActivity(intent);
//    }


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
