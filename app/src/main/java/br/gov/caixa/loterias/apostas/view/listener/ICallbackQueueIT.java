package br.gov.caixa.loterias.apostas.view.listener;

public interface ICallbackQueueIT {
    void callbackFila(boolean passouNaFila);
    void callbackAbrirFila();
    void callbackSaiuFila();
}
