package br.gov.caixa.loterias.apostas;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.gov.caixa.loterias.apostas.model.model.PreferenciasNotificacoesModel;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.PreferenciaNotificacaoRecyclerView;

public class PreferenciasNotificacoesActivity extends LoteriasAppActivity {

	private RecyclerView preferenciasListView;

	private PreferenciasNotificacoesModel model;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferencias_notificacoes);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.titulo_tela_preferencias_notificacao)));

		preferenciasListView = findViewById(R.id.lista_preferencias);

		model = new PreferenciasNotificacoesModel(this);

		preferenciasListView.setAdapter(new PreferenciaNotificacaoRecyclerView(model.getListNotificacoes()));
		preferenciasListView.setLayoutManager(new LinearLayoutManager(this));
	}
}