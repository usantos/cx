package br.gov.caixa.loterias.core.designsystem.components.state

import android.content.Context
import android.widget.Toast

/** Retorno visual nativo breve compartilhado pelos hospedeiros de permissões e navegação. */
fun showCaixaDSMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
