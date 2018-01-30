package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.VolleyError;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.ApostaBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.ResultadoConsultaBilheteDTO;
import caixa.gov.br.loterias.bo.silce.dto.ResultadoConsultaBilheteDTOResponse;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

public class ResultadoLerBilhetesActivity extends AppCompatActivity {


    private ResultadoConsultaBilheteDTO resultadoConsultaBilheteDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_ler_bilhetes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        String codBarras =  getIntent().getExtras().getString("codigoBarras");
//        Toast.makeText(this, "Scanned: " + codBarras, Toast.LENGTH_LONG).show();
//        ViewUtils.alert(this, codBarras);
        //01416145

        postLerBilhetes(new ApostaBO(this), LoadingViewLoterias.init(this), "60002952423640400130000000158355b");


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);

        return true;
    }

    // Mudando o texto dos MenuItem de acordo com os dados
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            abrirTermosUso();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirTermosUso(){
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }


    private void postLerBilhetes(ApostaBO apostaBO, final View loadViewProgress, String CodBarra) {
        apostaBO.postConferirBilhetes(CodBarra, new SilceClient.Listener<ResultadoConsultaBilheteDTOResponse>() {
            @Override
            public void onResponse(ResultadoConsultaBilheteDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                resultadoConsultaBilheteDTO = response.getPayload();
                AtulizarLayout();

            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(ResultadoLerBilhetesActivity.this, MensagensNetwork.setMensagem(error, ResultadoLerBilhetesActivity.this));
            }
        });

    }


    private void AtulizarLayout(){
//        switch (resultadoConsultaBilheteDTO.getSituacao().getValor().intValue()){
//            case
//        }
//        String a = resultadoConsultaBilheteDTO.
    }

}
