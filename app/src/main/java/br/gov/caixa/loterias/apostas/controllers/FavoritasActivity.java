package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.FavoritaModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.FavoritasRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.holder.FavoritaHolder;
import br.gov.caixa.loterias.apostas.view.listener.ApostaFavoritaListener;


public class FavoritasActivity extends AppCompatActivity {

    //region Variables Views
    public RecyclerView recyclerViewFavoritas;
    private ProgressBar progressLoadingMore;
    private NestedScrollView scrollViewContentViewFavoritas;
    private FavoritasRecyclerViewAdapter favoritasAdapter;
    //endregion

    //region Variables class
    private FavoritaModel model;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        model = new FavoritaModel(this);
        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.title_activity_favoritas_bold)));

        scrollViewContentViewFavoritas = findViewById(R.id.scrollViewContentViewFavoritas);

        progressLoadingMore = findViewById(R.id.favoritas_loading_more);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_lista_favoritos));
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
        model.setMaisCompras(true);
        model.setBloquearRequestScroll(false);

        AlertDialogUtils.show(this);
        model.buscaApostas(new OnSilceListener<ResultadoPesquisaPaginadaApostaFavoritaDTO>() {
            @Override
            public void success(ResultadoPesquisaPaginadaApostaFavoritaDTO payload) {
                AlertDialogUtils.dismiss();

                model.setOffsetItem(8);
                model.setApostaList(payload.getLista());

                //recyclerView
                recyclerViewFavoritas = findViewById(R.id.recyclerFavoritas);
                favoritasAdapter = new FavoritasRecyclerViewAdapter(FavoritasActivity.this,
                        FavoritasActivity.this, model.getApostaList(), onItemClickListener());

                recyclerViewFavoritas.setHasFixedSize(true);
                recyclerViewFavoritas.setAdapter(favoritasAdapter);
                recyclerViewFavoritas.setLayoutManager(new LinearLayoutManager(FavoritasActivity.this));

                scrollViewContentViewFavoritas.setOnScrollChangeListener((NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) -> {
                    View view = v.getChildAt(v.getChildCount() - 1);
                    int diff = (view.getBottom() - (v.getHeight() + v.getScrollY()));

                    // if diff is zero, then the bottom has been reached
                    if (diff == 0 && !model.getBloquearRequestScroll()) {
                        // do stuff
                        getFavoritasScroll();
                        model.setBloquearRequestScroll(true);
                    }
                });

                atualizaLayout();
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }

        });
    }

    private ApostaFavoritaListener onItemClickListener() {
        return new ApostaFavoritaListener() {
            @Override
            public void onDeleta(int position) {
                AlertDialogUtils.show(FavoritasActivity.this);
                model.deletaAposta(model.getApostaList().get(position), new OnSilceListener() {
                    @Override
                    public void success(Object payload) {
                        AlertDialogUtils.dismiss();
                        removeAt(position);
                    }

                    @Override
                    public void error(VolleyError error) {
                        AlertDialogUtils.dismiss();
                    }
                });
            }

            @Override
            public void onAltera(int position, FavoritaHolder holder) {
                AlertDialogUtils.show(FavoritasActivity.this);
                ApostaFavoritaDTO apostaFavoritaDTO = model.getApostaList().get(position);
                model.alteraAposta(apostaFavoritaDTO, holder, new OnSilceListener() {
                    @Override
                    public void success(Object payload) {
                        AlertDialogUtils.dismiss();
                        holder.nomeAtualizado(apostaFavoritaDTO);
                    }

                    @Override
                    public void error(VolleyError error) {
                        apostaFavoritaDTO.setOpenSwipeLayout(true);
                        AlertDialogUtils.dismiss();
                    }
                });
            }

            @Override
            public void onAdd(int position) {
                //if (!AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(FavoritasActivity.this)) {
                    model.addApostaCarrinho(model.getApostaList().get(position), onAddCarrinhoListener());
                //}
            }

            @Override
            public boolean onTemSwipe(int position) {
                return model.temSwipe(position);
            }
        };
    }

    public void removeAt(int position) {
        model.removeAposta(position);
        if (model.isListaVazia()) {
            callWebservice();
        }
        favoritasAdapter.notifyItemRemoved(position);
        favoritasAdapter.notifyItemRangeChanged(position, model.getApostaList().size());
    }

    private OnSilceListener<CarrinhoDTO> onAddCarrinhoListener() {
        return new OnSilceListener<CarrinhoDTO>() {
            @Override
            public void success(CarrinhoDTO payload) {
                AlertDialogUtils.dismiss();
                if (payload != null) {
                    CarrinhoSingleton.getInstance().setCarrinho(payload);
                }

                ViewUtils.alert(FavoritasActivity.this, getResources().getString(R.string.label_favorita_no_carrinho));
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        };
    }

    private void getFavoritasScroll(){
        if (!model.getMaisCompras()) {
            return;
        }
        progressLoadingMore.setVisibility(View.VISIBLE);
        model.buscaApostas(new OnSilceListener<ResultadoPesquisaPaginadaApostaFavoritaDTO>() {
            @Override
            public void success(ResultadoPesquisaPaginadaApostaFavoritaDTO payload) {
                progressLoadingMore.setVisibility(View.GONE);
                if(payload.getLista().size() == 0){
                    model.setMaisCompras(false);
                }else{
                    List<ApostaFavoritaDTO> apostaList = model.getApostaList();
                    apostaList.addAll(payload.getLista());
                    model.setApostaList(apostaList);
                    favoritasAdapter.notifyDataSetChanged();
                    model.setOffsetItem(model.getOffsetItem() + 5);
                }
                model.setBloquearRequestScroll(false);
            }

            @Override
            public void error(VolleyError error) {
                progressLoadingMore.setVisibility(View.GONE);
                model.setBloquearRequestScroll(false);
            }
        });
    }
}
