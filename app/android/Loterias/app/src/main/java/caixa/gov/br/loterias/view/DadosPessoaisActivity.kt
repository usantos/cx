package caixa.gov.br.loterias.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import caixa.gov.br.loterias.R
import caixa.gov.br.loterias.utils.ViewUtils

import kotlinx.android.synthetic.main.activity_dados_pessoais.*
import kotlinx.android.synthetic.main.app_bar_degrade_extend.*

class DadosPessoaisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dados_pessoais)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = ViewUtils.textFuturaAndFuturaBold(this, title.toString())
        subtitleToolbar.setText("Estes são seus dados pessoais.\nPor favor, mantenha-os sempre atualizados.")

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


}
