package br.gov.caixa.loterias.apostas.controllers;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.FavoritaModel;
import br.gov.caixa.loterias.apostas.model.model.ModalidadeModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.activity.SimulaActivity;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.SelecaoModalidadesRecyclerAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.FavoritasRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.holder.ModalidadeHolder;
import br.gov.caixa.loterias.apostas.view.listener.ApostaFavoritaListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;


public class FavoritasActivity extends LoteriasBaseAppActivity {

    private static final String TODAS = "Todas";

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
    private TextView semModalidade;
    private TextView btnIncApostaFav;
    ImageButton customButton;
    private boolean restaurarFiltroTodasAposRecarga;

    LinearLayout conteudoFiltro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritas);
        TextView textTitulo = findViewById(R.id.textCustom);
        textTitulo.setText(getString(R.string.apostas_favoritas));
        textTitulo.setHint(getString(R.string.titulo));

        customButton = findViewById(R.id.customButton);
        customButton.setFocusable(true);
        customButton.setFocusableInTouchMode(true);
        customButton.requestFocus();
        customButton.setContentDescription(getString(R.string.voltar));
        customButton.setOnClickListener(v -> onBackPressed());

        ImageButton questionButton = findViewById(R.id.buttonQuestions);
        questionButton.setOnClickListener(v ->
                startActivity(new Intent(this, TermosUsoActivity.class))
        );

        model = new FavoritaModel(this);

        scrollViewContentViewFavoritas = findViewById(R.id.scrollViewContentViewFavoritas);
        textFiltroSelecionado = findViewById(R.id.textFiltroSelecionado);
        imageFiltroSelecionado = findViewById(R.id.imageFiltroSelecionado);
        imageFiltroSelecionado.setOnClickListener(v -> abrirFiltroModalidade(FavoritasActivity.this));
        progressLoadingMore = findViewById(R.id.favoritas_loading_more);
        conteudoFiltro = findViewById(R.id.conteudoFiltro);
        conteudoFiltro.setOnClickListener(v -> {
            abrirFiltroModalidade(FavoritasActivity.this);
        });
        String descricao = getString(R.string.selecione_modalidade) + ":Botão:" + textFiltroSelecionado.getText().toString() + "Selecionada";
        conteudoFiltro.setContentDescription(descricao);

        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_lista_favoritos));

        callWebservice();

    }

    @Override
    protected void onResume() {
        super.onResume();
        buscaServicos();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            buscaServicos();
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
    private void buscaServicos() {

        if (listModalidade.size() > 0) {
            callWebservice();
            return;
        }

        AlertDialogUtils.show(this);
        new ModalidadeModel(this).buscaModalidades(new OnSilceListener<List<ModalidadeDTO>>() {
            @Override
            public void success(List<ModalidadeDTO> payload) {
                if (payload != null) {
                    listModalidade = payload;
                    criaListTipoAposta(true);
                }
                AlertDialogUtils.dismiss();
                callWebservice();
            }
            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        });
    }

    private void callWebservice() {
        AlertDialogUtils.show(this);

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

                if (listaUnificada.size() == 0) {
                    restaurarFiltroTodasAposRecarga = false;
                    telaSemApostas();
                    return;
                }

                if (restaurarFiltroTodasAposRecarga) {
                    restaurarFiltroParaTodas();
                    restaurarFiltroTodasAposRecarga = false;
                }
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
    private void callFiltrarModalidade(Integer codModalidade) {
        model.setMaisCompras(true);
        model.setBloquearRequestScroll(false);
        model.setOffsetItem(0);

        AlertDialogUtils.show(this);
        model.buscaApostasModalidade(codModalidade, new OnSilceListener<ResultadoPesquisaPaginadaApostaFavoritaDTO>() {
            @Override
            public void success(ResultadoPesquisaPaginadaApostaFavoritaDTO payload) {
                AlertDialogUtils.dismiss();

                if (payload.getLista() == null || payload.getLista().size() == 0) {
                    ativaIncluirAposta(codModalidade);
                    return;
                }

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
    private ApostaFavoritaListener onItemClickListener() {
        return new ApostaFavoritaListener() {
            @Override
            public void onDeleta(int position) {
                String nomeFavorita = model.getApostaList().get(position).getNome();
                DialogUtils.dialogSim(FavoritasActivity.this,
                        getString(R.string.exclui_favorita).replace("xxx", nomeFavorita),

                        new OnDialogBotaoListener() {
                            @Override
                            public void onButtonClick(DialogInterface dialog, int which) {
                                excluiApostaFavorita(position);
                            }
                        }
                );

            }

            @Override
            public void onAdd(int position) {
                if (!AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(FavoritasActivity.this)) {
                    model.addApostaCarrinho(model.getApostaList().get(position), onAddCarrinhoListener());
                }
            }

        };
    }

    private void excluiApostaFavorita(int position) {
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

    public void removeAt(int position) {
        model.removeAposta(position);
        if (model.isListaVazia()) {
            restaurarFiltroTodasAposRecarga = true;
            buscaServicos();
        }
        if (favoritasAdapter != null) {
            favoritasAdapter.notifyItemRemoved(position);
            favoritasAdapter.notifyItemRangeChanged(position, model.getApostaList().size());
        }
    }

    private void restaurarFiltroParaTodas() {
        if (listTipoAposta == null || listTipoAposta.isEmpty()
                || !TODAS.equalsIgnoreCase(listTipoAposta.get(0).getTitulo())) {
            criaListTipoAposta(true);
        }

        for (int position = 0; position < listTipoAposta.size(); position++) {
            listTipoAposta.get(position).setSelect(position == 0);
        }

        textFiltroSelecionado.setText(listTipoAposta.get(0).getTitulo());
        conteudoFiltro.setContentDescription(getString(R.string.selecione_modalidade)
                + ":Botão:" + listTipoAposta.get(0).getTitulo() + "Selecionada");
    }

    private OnSilceListener<CarrinhoDTO> onAddCarrinhoListener() {
        return new OnSilceListener<CarrinhoDTO>() {
            @Override
            public void success(CarrinhoDTO payload) {
                AlertDialogUtils.dismiss();
                if (payload != null) {
                    CarrinhoSingleton.getInstance().setCarrinho(payload);
                }

                DialogUtils.dialogTituloEntendi(FavoritasActivity.this,
                        getString(R.string.apostas_favoritas),
                        getString(R.string.label_new_favorita_no_carrinho));
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

    private void criaListTipoAposta(boolean comTodas) {
        listTipoAposta = new ArrayList<>();

        if (comTodas) {
            TipoAposta tipoAposta = new TipoAposta(TODAS,
                    R.color.blue_caixa, true, R.color.cinzanaoselecionado);
            listTipoAposta.add(tipoAposta);
        }

        for (ModalidadeDTO item : listModalidade) {
            TipoAposta tipoAposta = null;
            EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(ModalidadeEnum.fromModalidadeDTO(item));
            switch (ModalidadeEnumSelecaoModalidades.toString(item.getDescricao().toLowerCase())) {
                case MAIS_MILIONALIA:
                    tipoAposta = new TipoAposta("+Milionária", estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), R.color.cinzanaoselecionado);
                    break;
                case TIMEMANIA:
                case DIA_DE_SORTE:
                case SUPER_SETE:
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), R.color.cinzanaoselecionado);
                    tipoAposta.setTextSelectColor(estilo.getCorLetraLista());
                    break;
                case LOTECA:  //Loteca não tem Apostas Favoritas (Não apresenta na lista)
                    break;
                default:
                    tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), R.color.cinzanaoselecionado);
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
        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogTitle.setHint(getString(R.string.titulo));
        dialogTitle.requestFocus();
        TextView btnCancel = dialogView.findViewById(R.id.btn_cancel);
        btnCancel.setHint(getString(R.string.botao));
        TextView btnFilter = dialogView.findViewById(R.id.btn_filter);
        btnFilter.setHint(getString(R.string.botao));

        gridModalidades = dialogView.findViewById(R.id.ehgv_grid_modalidades);
        gridModalidades.setLayoutManager(new GridLayoutManager(context, 2));
        adapterModalidades = new SelecaoModalidadesRecyclerAdapter(listTipoAposta, true, onItemClickListenerModalidade());
        gridModalidades.setAdapter(adapterModalidades);


        Dialog dialog = new Dialog(context);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_roudend_branco);

        Button btnClose = dialogView.findViewById(R.id.dialog_close);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnClose.setContentDescription(getString(R.string.fechar));

        btnCancel.setOnClickListener(v -> {
            if (textFiltroSelecionado.getText().toString().equalsIgnoreCase(TODAS)) {
                adapterModalidades.selecionaOPrimeiro();
            }
            dialog.dismiss();
        });
        btnFilter.setOnClickListener(v -> {
            dialog.dismiss();
            desativaIncluirAposta();
            filtrarModalidade();
            textFiltroSelecionado.requestFocus();

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
        conteudoFiltro = findViewById(R.id.conteudoFiltro);

        for (TipoAposta tipoAposta : listTipoAposta) {
            if (tipoAposta.getSelect()) {
                textFiltroSelecionado.setText(tipoAposta.getTitulo());
                String descricao = getString(R.string.selecione_modalidade) + ":Botão:" + tipoAposta.getTitulo() + "Selecionada";
                conteudoFiltro.setContentDescription(descricao);

                if (tipoAposta.getValor() == null) {
                    buscaServicos();
                } else {
                    callFiltrarModalidade(tipoAposta.getValor());
                }
            }
        }
    }

    private void telaSemApostas() {
        criaListTipoAposta(false);

        TextView subTituloFavoritas = findViewById(R.id.subTituloFavoritas);
        subTituloFavoritas.setText(getString(R.string.voce_ainda_nao_tem_apostas));
        subTituloFavoritas.setTextColor(ContextCompat.getColor(this, R.color.blue_caixa));
        conteudoFiltro = findViewById(R.id.conteudoFiltro);
        conteudoFiltro.setVisibility(View.GONE);


        recyclerViewFavoritas = findViewById(R.id.recyclerFavoritas);
        recyclerViewFavoritas.setLayoutManager(new GridLayoutManager(this, 2));
        adapterModalidades = new SelecaoModalidadesRecyclerAdapter(listTipoAposta, true, onItemClickListenerModalidadeSemApostas());
        recyclerViewFavoritas.setAdapter(adapterModalidades);

    }
    private OnItemClickListener<ModalidadeHolder> onItemClickListenerModalidadeSemApostas() {
        return (holder, position) -> {
            if (listTipoAposta.get(position).getSelect()) {
                listTipoAposta.get(position).setSelect(false);
                holder.atualizaNaoSelecionado(listTipoAposta.get(position));
            } else {
                adapterModalidades.limpaSelecaoModalidades();
                listTipoAposta.get(position).setSelect(true);
                holder.atualizaSelecionado(listTipoAposta.get(position));
            }

            Integer codModalidade = listTipoAposta.get(position).getValor();
            redirecionaSimulador(codModalidade);
        };
    }
    private void redirecionaSimulador(int codModalidade) {
        ModalidadeEnum modalidadeEnum = ModalidadeEnum.fromInteger(codModalidade);

        ParametroJogoDTO parametroJogo = getParametroJogoPreferencial(modalidadeEnum, SessaoUsuario.getInstance().getParametrosSimulacao().getParametros());

        if (parametroJogo != null) {
            boolean isEspecial = parametroJogo.getConcurso().getTipoConcurso() == TipoConcursoEnum.ESPECIAL;
            Intent intent = IntentUtil.getIntentOrigemDestino(this, SimulaActivity.class);
            intent.putExtra("tipoAposta", modalidadeEnum);
            intent.putExtra("modalidade", (new Gson()).toJson(parametroJogo));
            intent.putExtra("especial", isEspecial);

            startActivity(intent);
        }
        finish();
    }
    private ParametroJogoDTO getParametroJogoPreferencial(ModalidadeEnum modalidade, List<ParametroSimulacao> listParametroJogo) {
        ParametroJogoDTO parametroEspecial = null;

        for (ParametroSimulacao parametroSimula : listParametroJogo) {
            if (parametroSimula != null
                    && parametroSimula.getParametroJogo() != null
                    && parametroSimula.getParametroJogo().getConcurso() != null
                    && parametroSimula.getParametroJogo().getConcurso().getModalidade() == modalidade) {
                TipoConcursoEnum tipoConcurso = parametroSimula.getParametroJogo().getConcurso().getTipoConcurso();
                if (tipoConcurso == TipoConcursoEnum.NORMAL) {
                    return parametroSimula.getParametroJogo();
                }
                if (tipoConcurso == TipoConcursoEnum.ESPECIAL) {
                    parametroEspecial = parametroSimula.getParametroJogo();
                }
            }
        }
        return parametroEspecial;
    }

    private void ativaIncluirAposta(int codModalidade) {
        semModalidade = findViewById(R.id.semModalidade);
        semModalidade.setVisibility(View.VISIBLE);

        btnIncApostaFav = findViewById(R.id.txtIncApostaFavorita);
        btnIncApostaFav.setHint(R.string.botao);
        GradientDrawable drawable = (GradientDrawable) AppCompatResources
                .getDrawable(this, R.drawable.background_roudend_azul)
                .mutate().getConstantState().newDrawable().mutate();

        drawable.setColor(ContextCompat.getColor(this, R.color.colorAccent));
        btnIncApostaFav.setBackground(drawable);
        btnIncApostaFav.setVisibility(View.VISIBLE);
        btnIncApostaFav.setOnClickListener(v -> {
            redirecionaSimulador(codModalidade);
        });

        recyclerViewFavoritas = findViewById(R.id.recyclerFavoritas);
        recyclerViewFavoritas.setVisibility(View.GONE);
    }

    private void desativaIncluirAposta() {
        semModalidade = findViewById(R.id.semModalidade);
        semModalidade.setVisibility(View.GONE);

        btnIncApostaFav = findViewById(R.id.txtIncApostaFavorita);
        btnIncApostaFav.setHint(R.string.botao);
        btnIncApostaFav.setVisibility(View.GONE);

        recyclerViewFavoritas = findViewById(R.id.recyclerFavoritas);
        recyclerViewFavoritas.setVisibility(View.VISIBLE);
    }
}