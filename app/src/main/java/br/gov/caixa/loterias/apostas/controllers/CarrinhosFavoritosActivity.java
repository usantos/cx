package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.parceler.Parcels;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.CarrinhosFavoritosAdapter;

public class CarrinhosFavoritosActivity extends AppCompatActivity {

	//region Constants
	public final static int RESPOSTAS_APOSTAS_CODIGO = 1;
	public final static int RESPOSTA_EXCLUIU_CARRINHO = 600;
	public final static int RESPOSTA_EXCLUIU_APOSTAS = 300;
	public final static String RESPOSTA_APOSTAS = "RESPOSTA_APOSTAS";
	//endregion

	//region Layout Variables
	private RecyclerView rvCarrinhosFavoritos;
	private TextView tvSemCarrinhos;
	//endregion

	//region Variables
	private CarrinhosFavoritosAdapter adapter;
	//endregion

	//region Life Cicle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carrinhos_favoritos);
		setaViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		buscaCarrinhosFavoritos();
	}
	//endregion

	//region Initialization Methods
	private void setaViews() {
		tvSemCarrinhos = findViewById(R.id.tv_sem_carrinhos);
		rvCarrinhosFavoritos = findViewById(R.id.rv_carrinhos_favoritos);

		setSupportActionBar(findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(CarrinhosFavoritosActivity.this, getString(R.string.carrinho_fav_titulo)));
	}
	//endregion

	//region Network Methods
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
	//endregion

	//region Layout Configuration Methods
	public void configuraRecyclerView(ListCarrinhoFavoritoDTO result) {
		if (result.getPayload().size() > 0) {
			rvCarrinhosFavoritos.setVisibility(View.VISIBLE);
			tvSemCarrinhos.setVisibility(View.GONE);
			adapter = new CarrinhosFavoritosAdapter(result.getPayload(), CarrinhosFavoritosActivity.this);
			rvCarrinhosFavoritos.setAdapter(adapter);
			RecyclerView.LayoutManager layout = new LinearLayoutManager(CarrinhosFavoritosActivity.this, LinearLayoutManager.VERTICAL, false);
			rvCarrinhosFavoritos.setLayoutManager(layout);
		} else {
			rvCarrinhosFavoritos.setVisibility(View.GONE);
			tvSemCarrinhos.setVisibility(View.VISIBLE);
		}
	}
	//endregion

	//region Support Methods
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESPOSTAS_APOSTAS_CODIGO) {
			switch (resultCode) {
				case RESPOSTA_EXCLUIU_CARRINHO:
					buscaCarrinhosFavoritos();
					break;
				case RESPOSTA_EXCLUIU_APOSTAS:
					Bundle bundle = data.getExtras();
					if (bundle != null) {
						List<ApostaCarrinhoFavoritoDTO> resposta = Parcels.unwrap(bundle.getParcelable(RESPOSTA_APOSTAS));
						adapter.recarregaCelula(resposta, adapter.getLayoutAtual());
						//rvCarrinhosFavoritos.setAdapter(adapter);
					}
					break;
			}
		}
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}
	//endregion
}
