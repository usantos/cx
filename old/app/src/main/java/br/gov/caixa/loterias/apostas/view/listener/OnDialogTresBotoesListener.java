package br.gov.caixa.loterias.apostas.view.listener;

import android.content.DialogInterface;

public interface OnDialogTresBotoesListener {
    void TopButton(DialogInterface dialog, int which);
    void CenterButton(DialogInterface dialog, int which);
    void BottomButton(DialogInterface dialog, int which);

}
