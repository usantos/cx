package caixa.gov.br.loterias.view;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.DetalhesPremioDTO;
import caixa.gov.br.loterias.view.custom.ContenteResultadoPremiado;

@EActivity(R.layout.activity_resultado_aposta_confirmada)
public class ResultadoApostaConfirmadaActivity extends AppCompatActivity {

    @ViewById
    protected ContenteResultadoPremiado contenteResultadoPremiado;

    @Extra
    protected DetalhesPremioDTO detalhesPremioDTO;

    private AlertDialog alertDialog;

    @AfterViews
    protected void init() {

    }
}
