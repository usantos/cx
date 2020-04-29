package br.gov.caixa.loterias.apostar.interno.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.FavoritasRecyclerViewAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;


public class FavoritasActivity extends AppCompatActivity {


    List<ApostaFavoritaDTO> apostaFavoritaLista = new ArrayList<>();
    public RecyclerView recyclerViewFavoritas;
    LinearLayoutManager layoutManager;
    Boolean isMaisCompras = true;
    Boolean bloquearRequestScroll = false;
    int offsetItem = 0;

    public ProgressBar progressLoadingMore;
    public NestedScrollView scrollViewContentViewFavoritas;
    FavoritasRecyclerViewAdapter favoritasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_favoritas_bold)));

        scrollViewContentViewFavoritas = findViewById(R.id.scrollViewContentViewFavoritas);

        progressLoadingMore = findViewById(R.id.favoritas_loading_more);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseManager.registraEvento(this ,getResources().getString(R.string.evento_entrou_lista_favoritos));
    }

    @Override
    protected void onResume() {
        super.onResume();

        callWebservice();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    private void atualizaLayout(){
        TextView tituloFavoritas = findViewById(R.id.tituloFavoritas);
        TextView subTituloFavoritas = findViewById(R.id.subTituloFavoritas);
        tituloFavoritas.setText(getResources().getString(R.string.numeros_da_sorte_no_mesmo_lugar));
        subTituloFavoritas.setText(getResources().getString(R.string.nesta_area_estao_todas_suas_apostas));

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

                offsetItem = 8;

                apostaFavoritaLista = response.getPayload().getLista();
                //recyclerView
                recyclerViewFavoritas = findViewById(R.id.recyclerFavoritas);
                favoritasAdapter = new FavoritasRecyclerViewAdapter(FavoritasActivity.this, FavoritasActivity.this, apostaFavoritaLista);
                layoutManager = new LinearLayoutManager(FavoritasActivity.this);

                recyclerViewFavoritas.setHasFixedSize(true);
                recyclerViewFavoritas.setAdapter(favoritasAdapter);
                recyclerViewFavoritas.setLayoutManager(layoutManager);
                favoritasAdapter.setOnExcluiuTudo(() -> callWebservice());

                scrollViewContentViewFavoritas.setOnScrollChangeListener((NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) -> {
                    View view = v.getChildAt(v.getChildCount() - 1);
                    int diff = (view.getBottom() - (v.getHeight() + v.getScrollY()));

                    // if diff is zero, then the bottom has been reached
                    if (diff == 0 && bloquearRequestScroll == false) {
                        // do stuff
                        getFavoritasScroll();
                        bloquearRequestScroll = true;
                    }
                });


                atualizaLayout();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), FavoritasActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, FavoritasActivity.this );

            }
        });
    }

    private void getFavoritasScroll(){
        if (isMaisCompras == false) {
            return;
        }
        final ApostaBO apostaBO = ApostaBO_.getInstance_(FavoritasActivity.this);
        progressLoadingMore.setVisibility(View.VISIBLE);

        apostaBO.getApostasFavoritas(offsetItem ,new RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse response) {
                progressLoadingMore.setVisibility(View.GONE);
                if(response.getPayload().getLista().size() == 0){
                    isMaisCompras = false;
                }else{
                    apostaFavoritaLista.addAll(response.getPayload().getLista());
                    favoritasAdapter.notifyDataSetChanged();
                    offsetItem = offsetItem +5;
                }
                bloquearRequestScroll = false;
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), FavoritasActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                progressLoadingMore.setVisibility(View.GONE);
                bloquearRequestScroll = false;
                RedirectNetwork.checkRedirect( error, FavoritasActivity.this );
            }
        });
    }
}
