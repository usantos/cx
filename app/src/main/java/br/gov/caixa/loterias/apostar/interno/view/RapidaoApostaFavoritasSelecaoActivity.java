package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.NumerosSelecionadosRapidao;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.ApostaFavoritaAdapter;
import br.gov.caixa.loterias.apostar.interno.view.adapter.FavoritasRapidaoRecyclerAdapter;
import br.gov.caixa.loterias.apostar.interno.view.adapter.FavoritasRecyclerViewAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class RapidaoApostaFavoritasSelecaoActivity extends AppCompatActivity {

    protected RecyclerView recycleApostasFavoritasRapidao;
    protected Button btnSalvar;

    List<ApostaFavoritaDTO> apostaFavoritaLista = new ArrayList<ApostaFavoritaDTO>();
    List<ApostaFavoritaDTO> apostasFavoritaListaSelecionadas = new ArrayList<ApostaFavoritaDTO>();

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String arrayApostas =  getIntent().getExtras().getString("apostasFavoritas");
        apostasFavoritaListaSelecionadas = Arrays.asList(new Gson().fromJson(arrayApostas, ApostaFavoritaDTO[].class));

        progressLoadingMore = (ProgressBar) findViewById(R.id.favoritas_loading_more);
        recycleApostasFavoritasRapidao = (RecyclerView) findViewById(R.id.recycleApostasFavoritasRapidao);
        btnSalvar = (Button) findViewById(R.id.salvarNumerosApostaRapidaoSelecao);

        listeners();
        callWebservice();
    }

    private void listeners() {
        btnSalvar.setOnClickListener((View v) -> {
            Intent intent = new Intent();

            String arrayNumeros = new Gson().toJson(apostasFavoritaListaSelecionadas);
            intent.putExtra("apostasSalvas", arrayNumeros);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void callWebservice() {
        offsetItem = 0;
        isMaisCompras = true;
        bloquearRequestScroll = false;

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(this);

        apostaBO.getApostasFavoritas(offsetItem, new RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse>() {
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
                apostaFavoritaAdapter  = new FavoritasRapidaoRecyclerAdapter(RapidaoApostaFavoritasSelecaoActivity.this,RapidaoApostaFavoritasSelecaoActivity.this, apostaFavoritaLista);
                layoutManager = new LinearLayoutManager(RapidaoApostaFavoritasSelecaoActivity.this);

                recycleApostasFavoritasRapidao.setHasFixedSize(true);
                recycleApostasFavoritasRapidao.setAdapter(apostaFavoritaAdapter);
                recycleApostasFavoritasRapidao.setLayoutManager(layoutManager);

                recycleApostasFavoritasRapidao.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        View view = (View) recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                        int diff = (view.getBottom() - (recyclerView.getHeight() + recyclerView.getScrollY()));

                        // if diff is zero, then the bottom has been reached
                        if (diff == 0 && bloquearRequestScroll == false) {
                            // do stuff
                            getFavoritasScroll();
                            bloquearRequestScroll = true;
                        }

                    }
                });

                apostaFavoritaAdapter.setOnFavorito(new FavoritasRapidaoRecyclerAdapter.Favorito() {
                    @Override
                    public void onRemover(ApostaFavoritaDTO aposta) {
                        if (apostasFavoritaListaSelecionadas.contains(aposta)) {
                            apostasFavoritaListaSelecionadas.remove(aposta);
                        }
                    }

                    @Override
                    public void onIncluso(ApostaFavoritaDTO aposta) {
                        if (!apostasFavoritaListaSelecionadas.contains(aposta)) {
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
        final ApostaBO apostaBO = ApostaBO_.getInstance_(RapidaoApostaFavoritasSelecaoActivity.this);
        progressLoadingMore.setVisibility(View.VISIBLE);

        apostaBO.getApostasFavoritas(offsetItem ,new RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse response) {
                progressLoadingMore.setVisibility(View.GONE);
                if(response.getPayload().getLista().size() == 0){
                    isMaisCompras = false;
                }else{
                    apostaFavoritaLista.addAll(response.getPayload().getLista());
                    apostaFavoritaAdapter.notifyDataSetChanged();
                    offsetItem = offsetItem +5;
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

