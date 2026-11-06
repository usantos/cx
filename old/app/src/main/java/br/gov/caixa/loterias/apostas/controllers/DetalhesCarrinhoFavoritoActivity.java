package br.gov.caixa.loterias.apostas.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.io.Serializable;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.model.DetalhesCarrinhoFavoritoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalhesApostasCarrinhoFavoritoAdapter;
import br.gov.caixa.loterias.apostas.view.listener.OnDetalheCarrinhoFavoritoClickListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;

import static br.gov.caixa.loterias.apostas.controllers.CarrinhosFavoritosActivity.RESPOSTA_APOSTAS;
import static br.gov.caixa.loterias.apostas.controllers.CarrinhosFavoritosActivity.RESPOSTA_EXCLUIU_APOSTAS;
import static br.gov.caixa.loterias.apostas.controllers.CarrinhosFavoritosActivity.RESPOSTA_EXCLUIU_CARRINHO;

public class DetalhesCarrinhoFavoritoActivity extends SettingToolbarActivity {
    public static String ID_CARRINHO = "ID_CARRINHO";

    private RecyclerView rvApostas;
    private DetalhesCarrinhoFavoritoModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_carrinho_favorito);
        model = new DetalhesCarrinhoFavoritoModel(this);
        pegaExtras();
        setaViews();
        buscaApostas();
    }

    private void setaViews(){
        rvApostas = findViewById(R.id.rv_apostas);
        setTitle(ViewUtils.textCaixaSTDBold(this, getString(R.string.title_activity_favoritas)));
    }

    private void pegaExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            model.setIdCarrinho(bundle.getLong(ID_CARRINHO));
        }
    }

    private void buscaApostas(){
        AlertDialogUtils.show(this);
        model.buscaApostas(new OnSilceListener<List<ApostaCarrinhoFavoritoDTO>>() {
            @Override
            public void success(List<ApostaCarrinhoFavoritoDTO> payload) {
                AlertDialogUtils.dismiss();
                model.setListApostas(payload);
                configuraRecyclerView(payload);
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        });
    }

    public void configuraRecyclerView(List<ApostaCarrinhoFavoritoDTO> list){
        rvApostas.setAdapter( new DetalhesApostasCarrinhoFavoritoAdapter(list, onItemClickListener(), getSupportFragmentManager()));
        RecyclerView.LayoutManager layout = new LinearLayoutManager(DetalhesCarrinhoFavoritoActivity.this, LinearLayoutManager.VERTICAL, false);
        rvApostas.setLayoutManager(layout);
    }

    private OnDetalheCarrinhoFavoritoClickListener onItemClickListener() {
        return position -> {
            DialogUtils.dialogSim(DetalhesCarrinhoFavoritoActivity.this,
                    getResources().getString(R.string.carrinho_fav_deseja_excluir_aposta),

                    new OnDialogBotaoListener() {
                        @Override
                        public void onButtonClick(DialogInterface dialog, int which) {
                            if (model.getList().size() > 1) {
                                AlertDialogUtils.show(DetalhesCarrinhoFavoritoActivity.this);
                                model.deletaApostaCarrinhoFavorito(model.getIdApostaByPosition(position), new OnSilceListener<List<ApostaCarrinhoFavoritoDTO>>() {
                                    @Override
                                    public void success(List<ApostaCarrinhoFavoritoDTO> payload) {
                                        AlertDialogUtils.dismiss();
                                        model.setListApostas(payload);
                                        configuraRecyclerView(payload);
                                    }

                                    @Override
                                    public void error(VolleyError error) {
                                        AlertDialogUtils.dismiss();
                                    }
                                });
                            } else {
                                excluirCarrinho(model.getIdCarrinho());
                            }
                        }
                    }
            );

        };
    }

    private void excluirCarrinho(Long idCarrinhoAtual) {
        AlertDialogUtils.show(DetalhesCarrinhoFavoritoActivity.this);
        model.excluiCarrinho(idCarrinhoAtual, new OnSilceListener<List<CarrinhoFavoritoDTO>>() {
            @Override
            public void success(List<CarrinhoFavoritoDTO> payload) {
                AlertDialogUtils.dismiss();
                setResult(RESPOSTA_EXCLUIU_CARRINHO);
                finish();
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        });
    }

    private void setaResult(){
        Bundle args = new Bundle();
        args.putSerializable(RESPOSTA_APOSTAS, (Serializable) model.getList());
        Intent it = new Intent();
        it.putExtras(args);
        setResult(RESPOSTA_EXCLUIU_APOSTAS, it);
    }

    @Override
    public void onBackPressed() {
        setaResult();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setaResult();
                finish();
                break;
            default:
                abrirAjuda();
        }
        return true;
    }

    private void abrirAjuda() {
        Intent activity = new Intent(DetalhesCarrinhoFavoritoActivity.this, TermosUsoActivity.class);
        activity.putExtra(getResources().getString(R.string.extra_ajuda), true);
        startActivity(activity);
    }
}
