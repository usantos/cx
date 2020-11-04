package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListSecaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListSecaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TermoDeUsoDTOResponse;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.FirebaseManager;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.ItemDuvidaAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;

public class TermosUsoActivity extends AppCompatActivity {

    private RadioButton termosUsoButton, politicaButton, duvidasButton;
    private Button buttonTermoAceite;
    private WebView webViewConteudo;
    private String termoHtml;
    private boolean isTermoAceito, isCadastro;
    private RelativeLayout footerAceiteTermo;
    private ConstraintLayout clTermoAceite;
    private ScrollView scrollViewTermoAceite;
    private LinearLayout layoutClickAceiteTermo, layoutDuvidas;
    private AppCompatCheckBox selcioneAceiteTermoUso;
    private ExpandableHeightRecyclerView recyclerViewDuvidas;
    private List<ListSecaoDTO> secaoDuvidas;
    private Boolean openAjuda = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViews();
        setListeners();
        getExtras();
        setWebview();
        listenerTermosUso();
        listenerDuvidas();
        checaAceiteTermoEConfiguraTela();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(DialogUtils.isShow()){
            DialogUtils.dismiss();
        }
    }

    private  void getExtras(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            openAjuda = bundle.getBoolean(getResources().getString(R.string.extra_ajuda));
            isCadastro = bundle.getBoolean("IS_CADASTRO");
        }
    }

    private void checaAceiteTermoEConfiguraTela() {
        if (DadosUsuarioBO.checarUsuarioLogado(this)) {
            listenerPolitica();
            verificaTermoAceito();
        } else {
            isTermoAceito = Boolean.TRUE;
            scrollViewTermoAceite.setPadding(0, 0, 0, 0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(isTermoAceito);
            footerAceiteTermo.setVisibility(View.GONE);
            termosUsoButton.performClick();
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

    private void setListeners() {
        buttonTermoAceite.setOnClickListener(view -> {
            if (selcioneAceiteTermoUso.isChecked()){
                DialogUtils.show(TermosUsoActivity.this);
                DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(TermosUsoActivity.this);
                dadosCorporativosBO.aceiteTermo(new RequestListener<ResourceResponse>() {
                    @Override
                    public void onResponse(ResourceResponse response) {
                        DialogUtils.dismiss();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogUtils.dismiss();
                        RedirectNetwork.checkRedirect(error,TermosUsoActivity.this);
                    }
                });
            }else {
                ViewUtils.alertTitleButton( TermosUsoActivity.this, R.string.label_atencao, TermosUsoActivity.this.getResources().getString( R.string.MN003 ), getApplicationContext().getResources().getString( R.string.ok ) );
            }

        });

        layoutClickAceiteTermo.setOnClickListener(view -> {
            if (selcioneAceiteTermoUso.isChecked()) {
                selcioneAceiteTermoUso.setChecked(Boolean.FALSE);
            } else {
                selcioneAceiteTermoUso.setChecked(Boolean.TRUE);
            }
        });
    }

    private void setViews() {
        setContentView(R.layout.activity_termos_uso);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_toolbar)));

        termosUsoButton = findViewById(R.id.buttonTermos);
        duvidasButton = findViewById(R.id.buttonDuvidas);
        politicaButton = findViewById(R.id.buttonPolitica);

        webViewConteudo = findViewById(R.id.webViewConteudo);
        footerAceiteTermo = findViewById(R.id.footerAceiteTermo);
        scrollViewTermoAceite = findViewById(R.id.scrollViewTermoAceite);
        selcioneAceiteTermoUso = findViewById(R.id.selcioneAceiteTermoUso);
        layoutDuvidas = findViewById(R.id.layoutDuvidas);
        clTermoAceite = findViewById(R.id.cl_termo_aceite);
        recyclerViewDuvidas = findViewById(R.id.recyclerViewDuvidas);
        buttonTermoAceite = findViewById(R.id.buttonTermoAceite);
        layoutClickAceiteTermo = findViewById(R.id.layoutClickAceiteTermo);

        termosUsoButton.setChecked(Boolean.TRUE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            setViews();

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                openAjuda = bundle.getBoolean(getResources().getString(R.string.extra_ajuda));
            }

            listenerTermosUso();
            listenerDuvidas();

            if (duvidasButton.isChecked()) {
                selectDuvidas();
            }else{
                checaAceiteTermoEConfiguraTela();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void getTermo() {
        DialogUtils.show(this);
        DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(TermosUsoActivity.this);

        dadosCorporativosBO.termo(new RequestListener<TermoDeUsoDTOResponse>() {
            @Override
            public void onResponse(TermoDeUsoDTOResponse response) {
                DialogUtils.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), TermosUsoActivity.this);
                }

                termoHtml = response.getPayload().getConteudo();
                webViewConteudo.loadData(termoHtml, getResources().getString(R.string.text_html_charset_utf_8), getResources().getString(R.string.utf_8));
                selectTermoUso();
                FirebaseManager.registraEvento(TermosUsoActivity.this ,getResources().getString(R.string.evento_entrou_termos_de_uso));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, TermosUsoActivity.this);
            }
        });
    }

    private void listenerTermosUso() {
        termosUsoButton.setOnClickListener(v -> getTermo());
    }

    private void listenerPolitica() {
        politicaButton.setOnClickListener(v -> selectPoliticas());
    }

    private void listenerDuvidas() {
        duvidasButton.setOnClickListener(v -> selectDuvidas());
    }

    private void selectTermoUso() {
        termosUsoButton.setChecked(Boolean.TRUE);
        clTermoAceite.setVisibility(View.VISIBLE);
        webViewConteudo.loadData(termoHtml, getResources().getString(R.string.text_html_charset_utf_8), getResources().getString(R.string.utf_8));
        unSelectDuvidas();
    }

    private void unSelectTermoUso() {
        clTermoAceite.setVisibility(View.GONE);
    }

    private void selectPoliticas() {
        politicaButton.setChecked(Boolean.TRUE);
        unSelectTermoUso();
        unSelectDuvidas();
    }

    private void selectDuvidas() {
        duvidasButton.setChecked(Boolean.TRUE);
        layoutDuvidas.setVisibility(View.VISIBLE);
        if (secaoDuvidas == null) {
            initReciclerViewDuvidas();
        }
        unSelectTermoUso();
    }

    private void unSelectDuvidas() {
        layoutDuvidas.setVisibility(View.GONE);
    }

    private void verificaTermoAceito() {
        DialogUtils.show(this);
        DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(TermosUsoActivity.this);

        dadosCorporativosBO.validaTermoAceito(new RequestListener<ResourceResponse>() {

            @Override
            public void onResponse(ResourceResponse result) {
                DialogUtils.dismiss();
                isTermoAceito = Boolean.TRUE;
                scrollViewTermoAceite.setPadding(0, 0, 0, 0);
                getSupportActionBar().setDisplayHomeAsUpEnabled(isTermoAceito);
                footerAceiteTermo.setVisibility(View.GONE);
                if (openAjuda == true) {
                    duvidasButton.performClick();
                }else{
                    termosUsoButton.performClick();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, TermosUsoActivity.this);
                getTermo();
                if(!isCadastro) {
                    exibeRodapeAceite();
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(isCadastro);
                    footerAceiteTermo.setVisibility(View.GONE);
                }
            }
        });
    }

    private void exibeRodapeAceite() {
        termosUsoButton.setEnabled( false );
        politicaButton.setEnabled( false );
        duvidasButton.setEnabled( false );
        footerAceiteTermo.setVisibility( View.VISIBLE );
        isTermoAceito = Boolean.FALSE;
        getSupportActionBar().setDisplayHomeAsUpEnabled(isTermoAceito);
    }

    @Override
    public void onBackPressed() {
        if (isTermoAceito || isCadastro) {
            super.onBackPressed();
        }
    }

    private void initReciclerViewDuvidas() {
        if (secaoDuvidas == null) {
            DialogUtils.show(this);
            DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(getApplicationContext());

            dadosCorporativosBO.secaoDuvidas(new RequestListener<ListSecaoDTOResponse>() {
                @Override
                public void onResponse(ListSecaoDTOResponse response) {
                    DialogUtils.dismiss();
                    if (response.getRedirect() != null){
                        RedirectNetwork.checkRedirectSucesso( response.getRedirect(), TermosUsoActivity.this);
                    }
                    secaoDuvidas = response.getPayload();
                    createRecyclerView();
                    FirebaseManager.registraEvento(TermosUsoActivity.this ,getResources().getString(R.string.evento_entrou_duvidas));
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogUtils.dismiss();
                    RedirectNetwork.checkRedirect(error, TermosUsoActivity.this);
                }
            });
        } else {
            createRecyclerView();
        }
    }

    private void createRecyclerView() {
        ItemDuvidaAdapter itemDuvidaAdapter = new ItemDuvidaAdapter(secaoDuvidas);
        recyclerViewDuvidas.setAdapter(itemDuvidaAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerViewDuvidas.setLayoutManager(layoutManager);
        recyclerViewDuvidas.setExpanded(Boolean.TRUE);
        recyclerViewDuvidas.setNestedScrollingEnabled(false);
    }
}
