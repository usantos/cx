package br.gov.caixa.loterias.apostas.view.listener;

import android.content.DialogInterface;

public interface OnDialogDoisBotoesListener {
    void PositiveButton(DialogInterface dialog, int which);
    void NegativeButton(DialogInterface dialog, int which);
}
