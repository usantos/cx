package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.io.Serializable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.view.adapter.DetalhesApostasCarrinhoFavoritoAdapter;

import static br.gov.caixa.loterias.apostas.controllers.CarrinhosFavoritosActivity.RESPOSTA_APOSTAS;
import static br.gov.caixa.loterias.apostas.controllers.CarrinhosFavoritosActivity.RESPOSTA_EXCLUIU_APOSTAS;

public class DetalhesCarrinhoFavoritoActivity extends SettingToolbarActivity {

    public static String ID_CARRINHO = "ID_CARRINHO";

    private RecyclerView rvApostas;
    private ListApostaCarrinhoFavoritoDTO listApostaResponse;
    private Long idCarrinho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_carrinho_favorito);
        pegaExtras();
        setaViews();
        setaMetodos();
    }

    private void setaMetodos(){
        buscaApostas();
    }

    private void setaViews(){
        rvApostas = findViewById(R.id.rv_apostas);
        setTitle(getString(R.string.title_activity_favoritas));
    }

    private void pegaExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idCarrinho = bundle.getLong(ID_CARRINHO);
        }
    }

    public void setListApostaResponse(ListApostaCarrinhoFavoritoDTO listApostaResponse) {
        this.listApostaResponse = listApostaResponse;
    }

    private void buscaApostas(){
        AlertDialogUtils.show(this);
        ApostaBO_.getInstance_(this).buscaApostasCarrinhoFavorito(idCarrinho ,new RequestListener<ListApostaCarrinhoFavoritoDTO>() {
            @Override
            public void onResponse(ListApostaCarrinhoFavoritoDTO result) {
                AlertDialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(result.getRedirect(), DetalhesCarrinhoFavoritoActivity.this);
                }
                listApostaResponse = result;
                configuraRecyclerView(result);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, DetalhesCarrinhoFavoritoActivity.this);
            }
        });
    }

    public void configuraRecyclerView(ListApostaCarrinhoFavoritoDTO listApostaResponse){
        rvApostas.setAdapter( new DetalhesApostasCarrinhoFavoritoAdapter(idCarrinho, listApostaResponse,this));
        RecyclerView.LayoutManager layout = new LinearLayoutManager(DetalhesCarrinhoFavoritoActivity.this, LinearLayoutManager.VERTICAL, false);
        rvApostas.setLayoutManager(layout);
    }

    private void setaResult(){
        Bundle args = new Bundle();
        args.putSerializable(RESPOSTA_APOSTAS, (Serializable) listApostaResponse.getPayload());
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
