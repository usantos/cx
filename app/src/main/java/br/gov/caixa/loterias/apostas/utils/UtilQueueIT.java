package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.util.Log;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.view.listener.ICallbackQueueIT;

import com.queue_it.androidsdk.Error;
import com.queue_it.androidsdk.QueueITEngine;
import com.queue_it.androidsdk.QueueITException;
import com.queue_it.androidsdk.QueueListener;
import com.queue_it.androidsdk.QueuePassedInfo;

public class UtilQueueIT extends QueueListener {
    public UtilQueueIT(Activity activity, ICallbackQueueIT callback) {
        this.callback = callback;
        iniciaFila(activity);
    }
    private ICallbackQueueIT callback;
    private void iniciaFila(Activity activity) {
        try {
            QueueITEngine engine = new QueueITEngine(activity, BuildConfig.QUEUE_IT_CUSTOMER_ID, BuildConfig.QUEUE_IT_EVENT_ALIAS_ID, BuildConfig.QUEUE_IT_CUSTOM_LAYOUT_NAME, "pt-BR", this);
            engine.run(activity);
        } catch (QueueITException e) {
            Log.e(getClass().getName(), "Não consegui abrir a fila.");
        }
    }
    @Override
    public void onQueuePassed(QueuePassedInfo queuePassedInfo) {
        Log.d(getClass().getName(), "QueueITEngine-Método onQueuePassed" + queuePassedInfo.getQueueItToken());
        callback.callbackFila(true);
    } //Called when queue ends successful
    @Override
    public void onQueueViewWillOpen() {
        Log.d(getClass().getName(), "QueueITEngine-Método onQueueViewWillOpen");
        /*
         * Após pré processamento da fila e iniciar o carregamento da página encerramos a activity de origem.
         * Esse comportamento impede o usuário de voltar para splashscreem
         */
        this.callback.callbackAbrirFila();
    } // Called right before the Queue-it view opens

    @Override
    public void onQueueDisabled() {
        Log.d(getClass().getName(), "QueueITEngine-Método onQueueDisabled");
        callback.callbackFila(false);
    } // Called when the event is disabled.
    @Override
    public void onQueueItUnavailable() {
        Log.d(getClass().getName(), "QueueITEngine-Método onQueueItUnavailable");
        callback.callbackFila(false);
    } // Called when Queue-it API could not be reached
    @Override
    public void onError(Error error, String errorMessage) {
        Log.e(getClass().getName(), "QueueITEngine-Método onError..." + errorMessage);
        callback.callbackFila(false);
    }
    @Override
    public void onUserExited() {
        Log.d(getClass().getName(), "onUserExited");
        callback.callbackSaiuFila();
    }

}
