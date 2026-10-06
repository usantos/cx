package br.gov.caixa.loterias.apostas.view.listener;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.filabra.adapter.android.FilaBRAClientListener;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.AppIndisponivelActivity;
import br.gov.caixa.loterias.apostas.controllers.SplashScreenActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;

public class FilaBRAListener implements FilaBRAClientListener {

    private final SplashScreenActivity activity;

    public FilaBRAListener(SplashScreenActivity activity) {
        this.activity = activity;
    }

    @Override
    //Called by the adapter when someone is passed by the queue, either immediately (with passType "SafeGuard")
    //or from a queue (with passType "Queued").
    //People are considered passed for the PassedLifetime from your queue settings in the portal.  If someone
    //goes back and through again, they get passType "Repass".
    public void onPass(String passType) {
        Log.d(getClass().getName(), "FilaBRAClient-Método onPass: " + passType);
        SessaoUsuario sessaoUser = SessaoUsuario.getInstance();
        sessaoUser.setPassouPelaFilaBRA(true);
        activity.verificaVersao();
    }
    @Override
    //Called by the adapter when it is about to show a Queue, PreSale, PostSale or Hold page.
    public void onShow() {
        Log.d(getClass().getName(), "FilaBRAClient-Método onShow");
    }

    @Override
    public void onNoInternet() {
        Log.e(getClass().getName(), "FilaBRAClient-Método onNoInternet");
        DialogUtils.dialogEntendi(activity,activity.getResources().getString(R.string.seminternet));
        //activity.startActivityForResult(new Intent(activity, AppIndisponivelActivity.class), 1);
    }
    @Override
    public void onError(String errorMessage) {
        Log.e(getClass().getName(), "FilaBRAClient-Método onError... " + errorMessage);
        //activity.startActivityForResult(new Intent(activity, AppIndisponivelActivity.class), 1);
        SessaoUsuario sessaoUser = SessaoUsuario.getInstance();
        sessaoUser.setPassouPelaFilaBRA(true);
        activity.verificaVersao();
    }
    @Override
    // Called by the adapter when a user has left the queue, for example by pressing the back button,
    // or opening another app.  Their place is saved, and when they come back, they will be
    // recognised.  If they leave the app open with the queue displayed on the screen, this method is not
    // called.
    public void onAbandon(String cause) {
        Log.d(getClass().getName(), "FilaBRAClient-Método onUserExited: " + cause);
        if ("Back".equals(cause)) {
            activity.runOnUiThread(() -> {
                new Handler().postDelayed(activity::finish, 50); // Minimal delay to ensure we're back in SplashScreenActivity
            });
        }
    }

    @Override
    // Called by the adapter when a user is assigned a queue position.  You can use this with
    // your notification system to send a Push Notification to a user who has closed your app
    // that they have reached the front of the queue.
    public void onJoin(int request) {
        //FilaBRAAndroidService.getPreference(activity, "mostRecentRequestNumber");
        Log.d(getClass().getName(), "FilaBRAClient-Método onjoin: " + request);
    }
}