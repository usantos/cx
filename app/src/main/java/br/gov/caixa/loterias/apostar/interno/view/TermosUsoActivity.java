package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ListSecaoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ListSecaoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.TermoDeUsoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.ItemDuvidaAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightRecyclerView;

public class TermosUsoActivity extends AppCompatActivity {

    private RadioButton termosUsoButton, politicaButton, duvidasButton;
    private Button buttonTermoAceite;
    private WebView webViewConteudo;
    private String termoHtml;
    private boolean isTermoAceito;
    private RelativeLayout footerAceiteTermo, relativeLayouTermoAceite;
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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            openAjuda = bundle.getBoolean("ajuda");
        }

        setWebview();

        termosUsoButton.setChecked(Boolean.TRUE);
        listenerTermosUso();
        listenerDuvidas();
        checaAceiteTermoEConfiguraTela();
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
                if (url.startsWith("mailto:")) {
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
                        ViewUtils.alertTitleButton(TermosUsoActivity.this, R.string.label_atencao, MensagensNetwork.setMensagem(error, getApplicationContext()), getApplicationContext().getResources().getString( R.string.ok ));
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
        relativeLayouTermoAceite = findViewById(R.id.relativeLayouTermoAceite);
        recyclerViewDuvidas = findViewById(R.id.recyclerViewDuvidas);
        buttonTermoAceite = findViewById(R.id.buttonTermoAceite);
        layoutClickAceiteTermo = findViewById(R.id.layoutClickAceiteTermo);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            setViews();

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                openAjuda = bundle.getBoolean("ajuda");
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
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), TermosUsoActivity.this);
                }
                
                termoHtml = response.getPayload().getConteudo();
                webViewConteudo.loadData(termoHtml, "text/html; charset=UTF-8", "UTF-8");
                DialogUtils.dismiss();
                selectTermoUso();
                FirebaseManager.registraEvento(TermosUsoActivity.this ,"ENTROU_TERMOS_DE_USO");
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
        relativeLayouTermoAceite.setVisibility(View.VISIBLE);
        webViewConteudo.loadData(termoHtml, "text/html; charset=UTF-8", "UTF-8");
        unSelectDuvidas();
    }

    private void unSelectTermoUso() {
        relativeLayouTermoAceite.setVisibility(View.GONE);
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

        dadosCorporativosBO.validaTermoAceito(new RequestListener<RetornoPadraoDTO>() {

            @Override
            public void onResponse(RetornoPadraoDTO result) {
                isTermoAceito = Boolean.TRUE;
                scrollViewTermoAceite.setPadding(0, 0, 0, 0);
                getSupportActionBar().setDisplayHomeAsUpEnabled(isTermoAceito);
                footerAceiteTermo.setVisibility(View.GONE);
                DialogUtils.dismiss();
                if (openAjuda == true) {
                    duvidasButton.performClick();
                }else{
                    termosUsoButton.performClick();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                exibeRodapeAceite();
                RedirectNetwork.checkRedirect(error, TermosUsoActivity.this);
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
        if (isTermoAceito) {
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
                    if (response.getRedirect() != null){
                        RedirectNetwork.checkRedirectSucesso( response.getRedirect(), TermosUsoActivity.this);
                    }

                    DialogUtils.dismiss();
                    secaoDuvidas = response.getPayload();
                    createRecyclerView();
                    FirebaseManager.registraEvento(TermosUsoActivity.this ,"ENTROU_DUVIDAS");
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
