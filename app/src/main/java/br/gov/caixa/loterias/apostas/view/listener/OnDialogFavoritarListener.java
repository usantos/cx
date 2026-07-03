package br.gov.caixa.loterias.apostas.view.listener;

public interface OnDialogFavoritarListener {
    void Confirmar(String text, boolean manterSurpresinhas);

    void Cancelar();
}
