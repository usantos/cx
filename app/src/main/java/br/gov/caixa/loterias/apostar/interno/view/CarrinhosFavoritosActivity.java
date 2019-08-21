package br.gov.caixa.loterias.apostar.interno.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ListCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.CarrinhosFavoritosAdapter;

public class CarrinhosFavoritosActivity extends SettingToolbarActivity {

    private RecyclerView rvCarrinhosFavoritos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinhos_favoritos);
        setaViews();
        setaMetodos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        buscaCarrinhosFavoritos();
    }

    private void setaViews() {
        rvCarrinhosFavoritos = findViewById(R.id.rv_carrinhos_favoritos);
        getSupportActionBar().setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(CarrinhosFavoritosActivity.this,getString(R.string.carrinho_fav_titulo)));
    }

    private void setaMetodos() {
    }

    private void buscaCarrinhosFavoritos() {
        DialogUtils.show(CarrinhosFavoritosActivity.this);
        ApostaBO_.getInstance_(CarrinhosFavoritosActivity.this).buscaCarrinhosFavoritos(new RequestListener<ListCarrinhoFavoritoDTO>() {
            @Override
            public void onResponse(ListCarrinhoFavoritoDTO result) {
                DialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(result.getRedirect(), CarrinhosFavoritosActivity.this);
                }
                configuraRecyclerView(result);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, CarrinhosFavoritosActivity.this);
            }
        });
    }

    private void configuraRecyclerView(ListCarrinhoFavoritoDTO result){
        rvCarrinhosFavoritos.setAdapter( new CarrinhosFavoritosAdapter(result.getPayload(),CarrinhosFavoritosActivity.this));
        RecyclerView.LayoutManager layout = new LinearLayoutManager(CarrinhosFavoritosActivity.this, LinearLayoutManager.VERTICAL, false);
        rvCarrinhosFavoritos.setLayoutManager(layout);
    }
    //private void
}
