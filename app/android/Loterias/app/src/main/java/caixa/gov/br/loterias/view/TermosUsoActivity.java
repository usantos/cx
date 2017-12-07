package caixa.gov.br.loterias.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;


import com.android.volley.VolleyError;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.TermoDeUsoDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.UnidadeFederacaoDTO;
import caixa.gov.br.loterias.bo.silce.dto.UnidadeFederacaoDTOResponse;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.ViewUtils;

public class TermosUsoActivity extends AppCompatActivity {

    private Button termosUsoButton;
    private Button politicaButton;
    private Button duvidasButton;
    private WebView webViewConteudo;
    private String termoHtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termos_uso);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        termosUsoButton = findViewById(R.id.buttonTermos);
        duvidasButton = findViewById(R.id.buttonDuvidas);
        politicaButton = findViewById(R.id.buttonPolitica);
        webViewConteudo = findViewById(R.id.webViewConteudo);
        getTermo();

        webViewConteudo.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                LoadingViewLoterias.stop(loadViewProgress);
            }
        });
        listenerTermosUso();
        listenerDuvidas();
        listenerPolitica();
    }

    private View loadViewProgress = null;

    private void getTermo () {
        loadViewProgress = LoadingViewLoterias.init(this);
        DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(TermosUsoActivity.this);

        dadosCorporativosBO.termo(new SilceClient.Listener<TermoDeUsoDTOResponse>() {
            @Override
            public void onResponse(TermoDeUsoDTOResponse response) {
                termoHtml = response.getPayload().getConteudo();

                webViewConteudo.loadData(termoHtml, "text/html", "UTF-8");
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(TermosUsoActivity.this, "ERRO");
            }
        });
    }

    private void listenerTermosUso(){
        termosUsoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                termosUsoButton.setBackgroundResource(R.color.verdeazul);
                termosUsoButton.setTextColor(getResources().getColor(R.color.branco));
                duvidasButton.setBackgroundResource(R.color.cinzaclaro);
                duvidasButton.setTextColor(getResources().getColor(R.color.verdeazul));
                politicaButton.setBackgroundResource(R.color.cinzaclaro);
                politicaButton.setTextColor(getResources().getColor(R.color.verdeazul));

                webViewConteudo.loadData(termoHtml, "text/html", "UTF-8");
            }
        });
    }

    private void listenerPolitica(){
        politicaButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                termosUsoButton.setBackgroundResource(R.color.cinzaclaro);
                termosUsoButton.setTextColor(getResources().getColor(R.color.verdeazul));
                duvidasButton.setBackgroundResource(R.color.cinzaclaro);
                duvidasButton.setTextColor(getResources().getColor(R.color.verdeazul));
                politicaButton.setBackgroundResource(R.color.verdeazul);
                politicaButton.setTextColor(getResources().getColor(R.color.branco));

            }
        });
    }

    private void listenerDuvidas(){
        duvidasButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                termosUsoButton.setBackgroundResource(R.color.cinzaclaro);
                termosUsoButton.setTextColor(getResources().getColor(R.color.verdeazul));
                duvidasButton.setBackgroundResource(R.color.verdeazul);
                duvidasButton.setTextColor(getResources().getColor(R.color.branco));
                politicaButton.setBackgroundResource(R.color.cinzaclaro);
                politicaButton.setTextColor(getResources().getColor(R.color.verdeazul));

            }
        });
    }
}
