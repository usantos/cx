package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;

import com.filabra.adapter.android.FilaBRAClient;
import com.filabra.adapter.android.FilaBRAClientListener;

public class FilaBRAManager {

    public void iniciaFila(Activity activity, FilaBRAClientListener listener) {
        FilaBRAClient client = new FilaBRAClient(
                activity,
                null,
                BuildConfigManager.getVariavel("FILABRA_ACCOUNT_SYSTEM_NAME"),
                BuildConfigManager.getVariavel("FILABRA_QUEUE_SYSTEM_NAME"),
                null,
                listener
        );
        client.go();
    }

    public void resetaFila(Activity activity) {
        FilaBRAClient.resetAdapter(activity);
        FilaBRAClient.resetFilaBRA(activity);
    }
}
