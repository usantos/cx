package br.gov.caixa.loterias.apostas.controllers;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

public class AlterarDadosLoginCaixaActivity extends LoteriasBaseAppActivity {
    WebView paginaAlterarDadosWebView;
    AlertDialog loadViewProgresas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_alterar_dados_login_caixa );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setWebview();
    }

    private void setWebview() {
        paginaAlterarDadosWebView = findViewById( R.id.paginaAlterarDadosWebView );
        loadViewProgresas = LoadingViewLoterias.show(this);

        paginaAlterarDadosWebView.getSettings().setJavaScriptEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            paginaAlterarDadosWebView.getSettings().setMixedContentMode(0);
            paginaAlterarDadosWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            paginaAlterarDadosWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            paginaAlterarDadosWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        paginaAlterarDadosWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                loadViewProgresas.hide();
            }
        });


        if(AppUtils.isNetworkAvailable(this)){
            String stringBuilder = BuildConfigManager.getVariavel("URL_SSO_ACCOUNT") +
                    getResources().getString(R.string.referrer) +
                    BuildConfigManager.getVariavel("SSO_CLIENT_ID") +
                    getResources().getString(R.string.referrer_uri);

            paginaAlterarDadosWebView.loadUrl(stringBuilder);
            AppCenterManager.registraEvento(getResources().getString(R.string.event_alterar_dados_caixa));
        }else{
            loadViewProgresas.hide();
            DialogUtils.dialogEntendi(AlterarDadosLoginCaixaActivity.this, getResources().getString(R.string.seminternet));
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void finish() {
        loadViewProgresas.dismiss();
        super.finish();
    }
}
