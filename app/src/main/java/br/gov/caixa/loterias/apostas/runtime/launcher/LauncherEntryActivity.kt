package br.gov.caixa.loterias.apostas.runtime.launcher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import br.gov.caixa.loterias.apostas.MainActivity

/** Encerra a tarefa temporária do alias antes que o calendário possa desabilitá-lo. */
class LauncherEntryActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(
            Intent(intent).setClass(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )
        finish()
    }
}
