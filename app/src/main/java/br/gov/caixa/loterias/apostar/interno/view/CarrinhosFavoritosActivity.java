package br.gov.caixa.loterias.apostar.interno.view;

import android.os.Bundle;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

public class CarrinhosFavoritosActivity extends SettingToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinhos_favoritos);
        setaViews();
    }

    private void setaViews(){
        getSupportActionBar().setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(CarrinhosFavoritosActivity.this,getString(R.string.carrinho_fav_titulo)));
    }
}
