package br.gov.caixa.loterias.apostar.interno.view;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.AppUtils;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class AlterarDadosLoginCaixaActivity extends AppCompatActivity {
    WebView paginaAlterarDadosWebView;
    AlertDialog loadViewProgresas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_alterar_dados_login_caixa );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        loadViewProgresas = LoadingViewLoterias.show(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        paginaAlterarDadosWebView = findViewById( R.id.paginaAlterarDadosWebView );

        if(AppUtils.isNetworkAvailable( this )){
            String stringBuilder = BuildConfig.URL_SSO_ACCOUNT +
                    "?referrer=" +
                    BuildConfig.SSO_CLIENT_ID +
                    "&referrer_uri=http%3A%2F%2Fdf7390sr029.corp.caixa.gov.br%3A8280%2Fsilce-web%2F%3Fredirect_fragment%3D%252Fhome";

            paginaAlterarDadosWebView.getSettings().setJavaScriptEnabled(true);

            paginaAlterarDadosWebView.loadUrl(stringBuilder);
            FirebaseManager.registraEvento(this ,"ENTROU_ALTERAR_DADOS_CAIXA");
        }else{
            loadViewProgresas.hide();
            ViewUtils.alertTitulo( this, R.string.label_atencao, getResources().getString( R.string.seminternet ) );
        }

        paginaAlterarDadosWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                loadViewProgresas.hide();
            }
        });

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
