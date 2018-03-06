package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
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
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.ApostaBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import caixa.gov.br.loterias.view.adapter.FavoritasRecyclerViewAdapter;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;


public class FavoritasActivity extends AppCompatActivity {


    List<ApostaFavoritaDTO> apostaFavoritaLista = new ArrayList<ApostaFavoritaDTO>();
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        scrollViewContentViewFavoritas = (NestedScrollView) findViewById(R.id.scrollViewContentViewFavoritas);

        progressLoadingMore = (ProgressBar) findViewById(R.id.favoritas_loading_more);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        callWebservice();


    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);

        return true;
    }

    // Mudando o texto dos MenuItem de acordo com os dados
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
        TextView tituloFavoritas = (TextView) findViewById(R.id.tituloFavoritas);
        TextView subTituloFavoritas = (TextView) findViewById(R.id.subTituloFavoritas);
        tituloFavoritas.setText("Os seus números da sorte\nno mesmo lugar");
        subTituloFavoritas.setText("Nesta área estão todas as suas apostas da sorte.\nÉ só escolher e apostar. Boa Sorte!");

    }


    private void callWebservice() {
        offsetItem = 0;
        isMaisCompras = true;
        bloquearRequestScroll = false;

        final View loadViewProgress = LoadingViewLoterias.init(this);
        final ApostaBO apostaBO = new ApostaBO(this);

        apostaBO.getApostasFavoritas(offsetItem, new SilceClient.Listener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);

                offsetItem = 8;

                apostaFavoritaLista = response.getPayload().getLista();
                //recyclerView
                recyclerViewFavoritas = (RecyclerView) findViewById(R.id.recyclerFavoritas);
                favoritasAdapter = new FavoritasRecyclerViewAdapter(FavoritasActivity.this, FavoritasActivity.this, apostaFavoritaLista);
                layoutManager = new LinearLayoutManager(FavoritasActivity.this);

                recyclerViewFavoritas.setHasFixedSize(true);
                recyclerViewFavoritas.setAdapter(favoritasAdapter);
                recyclerViewFavoritas.setLayoutManager(layoutManager);

                scrollViewContentViewFavoritas.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        View view = (View) v.getChildAt(v.getChildCount() - 1);
                        int diff = (view.getBottom() - (v.getHeight() + v.getScrollY()));

                        // if diff is zero, then the bottom has been reached
                        if (diff == 0 && bloquearRequestScroll == false) {
                            // do stuff
                            getFavoritasScroll();
                            bloquearRequestScroll = true;
                        }
                    }
                });


                atualizaLayout();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(FavoritasActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));

            }
        });
    }




    private void getFavoritasScroll(){
        if (isMaisCompras == false) {
            return;
        }
        final ApostaBO apostaBO = new ApostaBO(FavoritasActivity.this);
        progressLoadingMore.setVisibility(View.VISIBLE);

        apostaBO.getApostasFavoritas(offsetItem ,new SilceClient.Listener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse>() {
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
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressLoadingMore.setVisibility(View.GONE);
                bloquearRequestScroll = false;
                ViewUtils.alert(FavoritasActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }
    //


}
