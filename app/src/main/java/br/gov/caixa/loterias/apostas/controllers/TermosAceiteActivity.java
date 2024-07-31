package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TermoDeUsoDTOResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class TermosAceiteActivity extends AppCompatActivity {

    private WebView webViewConteudo;
    private String termoHtml;
    private ConstraintLayout clTermoAceite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViews();
        setWebview();
        getTermo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AlertDialogUtils.isShow()){
            AlertDialogUtils.dismiss();
        }
    }

    private void setWebview() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webViewConteudo.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(getResources().getString(R.string.mailto_dois_pontos))) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse(url));
                    startActivity(Intent.createChooser(intent, getString(R.string.enviando_email)));
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            public void onPageFinished(WebView view, String url) {
            }
        });

    }

    private void setViews() {
        setContentView(R.layout.activity_termos_aceite);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_toolbar)));

        webViewConteudo = findViewById(R.id.webViewConteudo2);
        clTermoAceite = findViewById(R.id.cl_termo_aceite2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            setViews();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void getTermo() {
        AlertDialogUtils.show(this);
        DadosCorporativosBO.getInstance().termo(new RequestListener<TermoDeUsoDTOResponse>() {
            @Override
            public void onResponse(TermoDeUsoDTOResponse response) {
                AlertDialogUtils.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), TermosAceiteActivity.this);
                }

                termoHtml = response.getPayload().getConteudo();
                webViewConteudo.loadData(termoHtml, getResources().getString(R.string.text_html_charset_utf_8), getResources().getString(R.string.utf_8));
                selectTermoUso();
                AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_termos_de_uso));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, TermosAceiteActivity.this);
            }
        });
    }

    private void selectTermoUso() {
        clTermoAceite.setVisibility(View.VISIBLE);
        webViewConteudo.loadData(termoHtml, getResources().getString(R.string.text_html_charset_utf_8), getResources().getString(R.string.utf_8));
    }
}
