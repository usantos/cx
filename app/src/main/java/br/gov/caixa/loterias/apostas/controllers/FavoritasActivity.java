package br.gov.caixa.loterias.apostas.controllers;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.FavoritaModel;
import br.gov.caixa.loterias.apostas.model.model.ModalidadeModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.SelecaoModalidadesRecyclerAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.FavoritasRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.holder.ModalidadeHolder;
import br.gov.caixa.loterias.apostas.view.listener.ApostaFavoritaListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;


public class FavoritasActivity extends LoteriasBaseAppActivity {

    //region Variables Views
    public RecyclerView recyclerViewFavoritas;
    public TextView textFiltroSelecionado;
    public ImageView imageFiltroSelecionado;
    private ProgressBar progressLoadingMore;
    private NestedScrollView scrollViewContentViewFavoritas;

    private FavoritasRecyclerViewAdapter favoritasAdapter;
    //endregion

    //region Variables class
    private FavoritaModel model;
    //endregion
    private RecyclerView gridModalidades;
    private SelecaoModalidadesRecyclerAdapter adapterModalidades;
    private List<TipoAposta> listTipoAposta;
    private List<ModalidadeDTO> listModalidade = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        model = new FavoritaModel(this);
        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.title_activity_favoritas_bold)));

        scrollViewContentViewFavoritas = findViewById(R.id.scrollViewContentViewFavoritas);
        textFiltroSelecionado = findViewById(R.id.textFiltroSelecionado);
        imageFiltroSelecionado = findViewById(R.id.imageFiltroSelecionado);
        imageFiltroSelecionado.setOnClickListener(v -> abrirFiltroModalidade(FavoritasActivity.this));
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
    public boolean onSupportNavigateUp() {
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

    private void abrirTermosUso() {
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }

    private void callFiltrarModalidade(Integer codModalidade) {
        model.setMaisCompras(true);
        model.setBloquearRequestScroll(false);
        model.setOffsetItem(0);

        AlertDialogUtils.show(this);
        model.buscaApostasModalidade(codModalidade, new OnSilceListener<ResultadoPesquisaPaginadaApostaFavoritaDTO>() {
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
                        getFavoritasScroll(codModalidade);
                        model.setBloquearRequestScroll(true);
                    }
                });

            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }

        });
    }

    private void callWebservice() {
        AlertDialogUtils.show(this);

        if (listModalidade.size() == 0) {
            buscaModalidades();
        }

        model.setMaisCompras(false);
        model.setBloquearRequestScroll(true);
        model.setOffsetItem(0);

        model.buscaApostasAgrupadas(new OnSilceListener<AgrupadorDTOApostaFavoritaDTO>() {
            @Override
            public void success(AgrupadorDTOApostaFavoritaDTO payload) {

                //Transforma para o formato da lista /apostas-favoritas/modalidade
                Map<String, ResultadoPesquisaPaginadaDTOApostaFavoritaDTO> agrupador = payload.getResultado();
                List<ApostaFavoritaDTO> listaUnificada = new ArrayList<>();
                for (ResultadoPesquisaPaginadaDTOApostaFavoritaDTO resultado : agrupador.values()) {
                    listaUnificada.addAll(resultado.getLista());
                }

                AlertDialogUtils.dismiss();

                //model.setOffsetItem(8);
                model.setApostaList(listaUnificada);

                recyclerViewFavoritas = findViewById(R.id.recyclerFavoritas);

                favoritasAdapter = new FavoritasRecyclerViewAdapter(FavoritasActivity.this,
                        FavoritasActivity.this, model.getApostaList(), onItemClickListener());

                recyclerViewFavoritas.setHasFixedSize(true);
                recyclerViewFavoritas.setAdapter(favoritasAdapter);
                recyclerViewFavoritas.setLayoutManager(new LinearLayoutManager(FavoritasActivity.this));
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
            public void onAdd(int position) {
                //if (!AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(FavoritasActivity.this)) {
                model.addApostaCarrinho(model.getApostaList().get(position), onAddCarrinhoListener());
                //}
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

    private void getFavoritasScroll(Integer codModalidade){
        if (!model.getMaisCompras()) {
            return;
        }
        progressLoadingMore.setVisibility(View.VISIBLE);
        //model.buscaApostas(new OnSilceListener<ResultadoPesquisaPaginadaApostaFavoritaDTO>() {
        model.buscaApostasModalidade(codModalidade, new OnSilceListener<ResultadoPesquisaPaginadaApostaFavoritaDTO>() {

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

    private void buscaModalidades() {
        new ModalidadeModel(this).buscaModalidades(new OnSilceListener<List<ModalidadeDTO>>() {
            @Override
            public void success(List<ModalidadeDTO> payload) {
                if (payload != null) {
                    listModalidade = payload;
                    criaListTipoAposta(true);
                }
            }

            @Override
            public void error(VolleyError error) {
            }
        });
    }

    private void criaListTipoAposta(boolean comTodas) {
        listTipoAposta = new ArrayList<>();

        if (comTodas) {
            TipoAposta tipoAposta = new TipoAposta("Todas",
                    R.color.blue_caixa, true, R.color.cinzatextoselecionado);
            listTipoAposta.add(tipoAposta);
        }

        for (ModalidadeDTO item : listModalidade) {
            TipoAposta tipoAposta = null;
            EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(ModalidadeEnum.fromModalidadeDTO(item));
            switch (ModalidadeEnumSelecaoModalidades.toString(item.getDescricao().toLowerCase())) {
                case MAIS_MILIONALIA:
                    tipoAposta = new TipoAposta("+Milionária", estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), R.color.cinzatextoselecionado);
                    break;
                case TIMEMANIA:
                case DIA_DE_SORTE:
                case SUPER_SETE:
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), R.color.cinzatextoselecionado);
                    tipoAposta.setTextSelectColor(estilo.getCorLetraLista());
                    break;
                default:
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), R.color.cinzatextoselecionado);
                    break;
            }

            if (tipoAposta != null) {
                tipoAposta.setTitulo(tipoAposta.getTitulo().toLowerCase(new Locale("pt", "BR")));
                listTipoAposta.add(tipoAposta);
            }
        }
    }

    public void abrirFiltroModalidade(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_grid, null);

        gridModalidades = dialogView.findViewById(R.id.ehgv_grid_modalidades);
        gridModalidades.setLayoutManager(new GridLayoutManager(context, 2));
        adapterModalidades = new SelecaoModalidadesRecyclerAdapter(listTipoAposta, true, onItemClickListenerModalidade());
        gridModalidades.setAdapter(adapterModalidades);


        Dialog dialog = new Dialog(context);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_roudend_branco);

        TextView btnCancel = dialogView.findViewById(R.id.btn_cancel);
        TextView btnFilter = dialogView.findViewById(R.id.btn_filter);
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnFilter.setOnClickListener(v -> {
            dialog.dismiss();
            filtrarModalidade();
        });

        dialog.show();
        dialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private OnItemClickListener<ModalidadeHolder> onItemClickListenerModalidade() {
        return (holder, position) -> {
            if (listTipoAposta.get(position).getSelect()) {
                listTipoAposta.get(position).setSelect(false);
                holder.atualizaNaoSelecionado(listTipoAposta.get(position));
            } else {
                adapterModalidades.limpaSelecaoModalidades();
                listTipoAposta.get(position).setSelect(true);
                holder.atualizaSelecionado(listTipoAposta.get(position));
            }
        };
    }

    public void filtrarModalidade() {
        for (TipoAposta tipoAposta : listTipoAposta) {
            if (tipoAposta.getSelect()) {
                textFiltroSelecionado.setText(tipoAposta.getTitulo());
                if (tipoAposta.getValor() == null) {
                    callWebservice();
                } else {
                    callFiltrarModalidade(tipoAposta.getValor());
                }
            }
        }
    }
}