package caixa.gov.br.loterias.view;

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
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.android.volley.VolleyError;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.model.bo.silce.SilceClient;
import caixa.gov.br.loterias.model.bo.silce.dto.ListSecaoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ListSecaoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ResourceResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.TermoDeUsoDTOResponse;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.ItemDuvidaAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

public class TermosUsoActivity extends AppCompatActivity {

    private Button termosUsoButton, politicaButton, duvidasButton, buttonTermoAceite;
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
        setContentView(R.layout.activity_termos_uso);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            openAjuda = bundle.getBoolean("ajuda");
        }

        buttonTermoAceite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View loadViewProgress = LoadingViewLoterias.init(TermosUsoActivity.this);
                DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(TermosUsoActivity.this);
                dadosCorporativosBO.aceiteTermo(new SilceClient.Listener<ResourceResponse>() {
                    @Override
                    public void onResponse(ResourceResponse response) {
                        LoadingViewLoterias.stop(loadViewProgress);
                        finish();
                    }
                }, new SilceClient.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LoadingViewLoterias.stop(loadViewProgress);
                        ViewUtils.alert(TermosUsoActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
                    }
                });

            }
        });

        layoutClickAceiteTermo = findViewById(R.id.layoutClickAceiteTermo);
        layoutClickAceiteTermo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selcioneAceiteTermoUso.isChecked()) {
                    selcioneAceiteTermoUso.setChecked(Boolean.FALSE);
                    disableButton();
                } else {
                    selcioneAceiteTermoUso.setChecked(Boolean.TRUE);
                    enableButton();
                }
            }
        });

        getTermo();

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
        termosUsoButton.setSelected(Boolean.TRUE);
        listenerTermosUso();
        listenerDuvidas();
        listenerPolitica();
        if (openAjuda == true){
            duvidasButton.performClick();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void getTermo() {
        final View loadViewProgress = LoadingViewLoterias.init(this);
        DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(TermosUsoActivity.this);

        dadosCorporativosBO.termo(new SilceClient.Listener<TermoDeUsoDTOResponse>() {
            @Override
            public void onResponse(TermoDeUsoDTOResponse response) {
                termoHtml = response.getPayload().getConteudo();

                webViewConteudo.loadData(termoHtml, "text/html; charset=UTF-8", "UTF-8");
                LoadingViewLoterias.stop(loadViewProgress);
                verificaTermoAceito();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(TermosUsoActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    private void listenerTermosUso() {
        termosUsoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectTermoUso();

            }
        });
    }

    private void listenerPolitica() {
        politicaButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectPoliticas();

            }
        });
    }

    private void listenerDuvidas() {
        duvidasButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectDuvidas();

            }
        });
    }

    private void selectTermoUso() {
        termosUsoButton.setSelected(Boolean.TRUE);
        termosUsoButton.setTextColor(getResources().getColor(R.color.branco));
        relativeLayouTermoAceite.setVisibility(View.VISIBLE);
        webViewConteudo.loadData(termoHtml, "text/html; charset=UTF-8", "UTF-8");
        unSelectPoliticas();
        unSelectDuvidas();
    }

    private void unSelectTermoUso() {
        termosUsoButton.setSelected(Boolean.FALSE);
        termosUsoButton.setTextColor(getResources().getColor(R.color.verdeazul));
        relativeLayouTermoAceite.setVisibility(View.GONE);
    }

    private void selectPoliticas() {
        politicaButton.setSelected(Boolean.TRUE);
        politicaButton.setTextColor(getResources().getColor(R.color.branco));
        unSelectTermoUso();
        unSelectDuvidas();
    }

    private void unSelectPoliticas() {
        politicaButton.setSelected(Boolean.FALSE);
        politicaButton.setTextColor(getResources().getColor(R.color.verdeazul));
    }

    private void selectDuvidas() {
        duvidasButton.setSelected(Boolean.TRUE);
        duvidasButton.setTextColor(getResources().getColor(R.color.branco));
        layoutDuvidas.setVisibility(View.VISIBLE);
        initReciclerViewDuvidas();
        unSelectTermoUso();
        unSelectPoliticas();
    }

    private void unSelectDuvidas() {
        duvidasButton.setSelected(Boolean.FALSE);
        duvidasButton.setTextColor(getResources().getColor(R.color.verdeazul));
        layoutDuvidas.setVisibility(View.GONE);
    }

    private void verificaTermoAceito() {
        final View loadViewProgress = LoadingViewLoterias.init(this);
        DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(TermosUsoActivity.this);

        dadosCorporativosBO.validaTermoAceito(new SilceClient.Listener<ResourceResponse>() {
            @Override
            public void onResponse(ResourceResponse response) {
                isTermoAceito = Boolean.TRUE;
                exibeRodapeAceite();
                LoadingViewLoterias.stop(loadViewProgress);
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isTermoAceito = Boolean.FALSE;
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(TermosUsoActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    private void exibeRodapeAceite() {
        if (isTermoAceito) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            footerAceiteTermo.setVisibility(View.GONE);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            footerAceiteTermo.setVisibility(View.VISIBLE);
            scrollViewTermoAceite.setPadding(0, 0, 0, 230);
        }
    }

    @Override
    public void onBackPressed() {
        if (isTermoAceito) {
            super.onBackPressed();
        }
    }

    private void disableButton() {
        buttonTermoAceite.setClickable(Boolean.FALSE);
        buttonTermoAceite.setBackgroundResource(R.color.azulclaro);
    }

    private void enableButton() {
        buttonTermoAceite.setClickable(Boolean.TRUE);
        buttonTermoAceite.setBackgroundResource(R.color.amarelo);
    }

    private void initReciclerViewDuvidas() {
        if (secaoDuvidas == null) {
            final View loadViewProgress = LoadingViewLoterias.init(this);
            DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(getApplicationContext());

            dadosCorporativosBO.secaoDuvidas(new SilceClient.Listener<ListSecaoDTOResponse>() {
                @Override
                public void onResponse(ListSecaoDTOResponse response) {
                    secaoDuvidas = response.getPayload();
                    createRecyclerView();
                    LoadingViewLoterias.stop(loadViewProgress);
                }
            }, new SilceClient.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LoadingViewLoterias.stop(loadViewProgress);
                    ViewUtils.alert(TermosUsoActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
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
        recyclerViewDuvidas.setLayoutManager(layoutManager);
        recyclerViewDuvidas.setExpanded(Boolean.TRUE);
    }
}
