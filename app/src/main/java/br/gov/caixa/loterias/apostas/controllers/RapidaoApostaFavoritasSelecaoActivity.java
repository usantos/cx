package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.FavoritasRapidaoRecyclerAdapter;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

public class RapidaoApostaFavoritasSelecaoActivity extends LoteriasBaseAppActivity {

    protected RecyclerView recycleApostasFavoritasRapidao;
    protected Button btnSalvar;

    List<ApostaFavoritaDTO> apostaFavoritaLista = new ArrayList<>();
    ArrayList<ApostaFavoritaDTO> apostasFavoritaListaSelecionadas = new ArrayList<>();

    int offsetItem = 0;
    Boolean bloquearRequestScroll = false;
    Boolean isMaisCompras = true;
    public ProgressBar progressLoadingMore;
    FavoritasRapidaoRecyclerAdapter apostaFavoritaAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapidao_aposta_favorita_selecao);

        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_lista_rapidao)));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String arrayApostas =  getIntent().getExtras().getString(getResources().getString(R.string.extra_apostas_favoritas));
        apostasFavoritaListaSelecionadas = new Gson().fromJson(arrayApostas, new TypeToken<ArrayList<ApostaFavoritaDTO>>(){}.getType());

        progressLoadingMore = findViewById(R.id.favoritas_loading_more_rapidao);
        recycleApostasFavoritasRapidao = findViewById(R.id.recycleApostasFavoritasRapidao);
        btnSalvar = findViewById(R.id.salvarNumerosApostaRapidaoSelecao);

        listeners();
        callWebservice();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void listeners() {
        btnSalvar.setOnClickListener((View v) -> {
            Intent intent = new Intent();

            String arrayNumeros = new Gson().toJson(apostasFavoritaListaSelecionadas);
            intent.putExtra(getResources().getString(R.string.extra_apostas_salvas), arrayNumeros);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void callWebservice() {
        offsetItem = 0;
        isMaisCompras = true;
        bloquearRequestScroll = false;

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        ServicoFactoryUtil.getApostaService().getApostasFavoritas(offsetItem, new RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse response) {
                loadViewProgress.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), RapidaoApostaFavoritasSelecaoActivity.this);
                }
                LinearLayoutManager layoutManager;

                offsetItem = 8;

                apostaFavoritaLista = response.getPayload().getLista();
                //recyclerView
                apostaFavoritaAdapter  = new FavoritasRapidaoRecyclerAdapter(RapidaoApostaFavoritasSelecaoActivity.this,
                                                                 RapidaoApostaFavoritasSelecaoActivity.this,
                                                                        apostaFavoritaLista, apostasFavoritaListaSelecionadas,
                                                       false);

                layoutManager = new LinearLayoutManager(RapidaoApostaFavoritasSelecaoActivity.this);

                recycleApostasFavoritasRapidao.setHasFixedSize(true);
                recycleApostasFavoritasRapidao.setAdapter(apostaFavoritaAdapter);
                recycleApostasFavoritasRapidao.setLayoutManager(layoutManager);

                recycleApostasFavoritasRapidao.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        View view = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                        int diff = (view.getBottom() - (recyclerView.getHeight() + recyclerView.getScrollY()));

                        // if diff is zero, then the bottom has been reached
                        if (diff <= 0 && bloquearRequestScroll == false) {
                            // do stuff
                            getFavoritasScroll();
                            bloquearRequestScroll = true;
                        }

                    }
                });

                apostaFavoritaAdapter.setOnFavorito(new FavoritasRapidaoRecyclerAdapter.Favorito() {
                    @Override
                    public void onRemover(ApostaFavoritaDTO aposta) {
                        for (ApostaFavoritaDTO apostasFavoritaListaSelecionada : apostasFavoritaListaSelecionadas) {
                            if (apostasFavoritaListaSelecionada.getId().compareTo(aposta.getId()) == 0) {
                                apostasFavoritaListaSelecionadas.remove(apostasFavoritaListaSelecionadas.indexOf(apostasFavoritaListaSelecionada));
                                break;
                            }
                        }
                    }

                    @Override
                    public void onIncluso(ApostaFavoritaDTO aposta) {
                        boolean duplicado = false;

                        for (ApostaFavoritaDTO apostasFavoritaListaSelecionada : apostasFavoritaListaSelecionadas) {
                            if (apostasFavoritaListaSelecionada.getId().compareTo(aposta.getId()) == 0) {
                                duplicado = true;
                            }
                        }

                        if (!apostasFavoritaListaSelecionadas.contains(aposta) && !duplicado) {
                            apostasFavoritaListaSelecionadas.add(aposta);
                        }
                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, RapidaoApostaFavoritasSelecaoActivity.this );

            }
        });
    }


    private void getFavoritasScroll(){
        if (isMaisCompras == false) {
            return;
        }
        progressLoadingMore.setVisibility(View.VISIBLE);
        ServicoFactoryUtil.getApostaService().getApostasFavoritas(offsetItem ,new RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse response) {
                progressLoadingMore.setVisibility(View.GONE);
                if(response.getPayload().getLista().size() == 0){
                    isMaisCompras = false;
                }else{
                    apostaFavoritaLista.addAll(response.getPayload().getLista());
                    apostaFavoritaAdapter.notifyDataSetChanged();
                    offsetItem += 8;
                }
                bloquearRequestScroll = false;
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), RapidaoApostaFavoritasSelecaoActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                progressLoadingMore.setVisibility(View.GONE);
                bloquearRequestScroll = false;
                RedirectNetwork.checkRedirect( error, RapidaoApostaFavoritasSelecaoActivity.this );
            }
        });
    }
}

