package br.gov.caixa.loterias.apostas.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Bolao;
import br.gov.caixa.loterias.apostas.model.model.BolaoModel;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.holder.BolaoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;

public class BolaoActivity extends AppCompatActivity {

	private RecyclerView listView;
	private ListaBolaoAdapter adapter;

	private BolaoModel model;
	private List<Bolao> boloes;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bolao);

		listView = findViewById(R.id.lista_bolao);

		model = new BolaoModel(this);

		boloes = model.getBoloes();
		adapter = new ListaBolaoAdapter(boloes, onItemBolaoListener(), this);
		listView.setAdapter(adapter);

	}

	private OnItemBolaoListener onItemBolaoListener() {
		return new OnItemBolaoListener() {
			@Override
			public void verMais(BolaoHolder holder, int position) {
				boloes.get(position).setAberto(!boloes.get(position).isAberto());

				holder.mostrarDetalhes(boloes.get(position).isAberto());
			}

			@Override
			public void detalhes(int position) {
				TestVisao.toast("Funcionalidade ainda nao implementada!");
			}

			@Override
			public void adicionarCarrinho(int position) {
				TestVisao.toast("Funcionalidade ainda nao implementada!");
			}

			@Override
			public void diminui(BolaoHolder holder, int position) {
				boloes.get(position).diminuiQuantidade();
				TestVisao.toast("Quantidade: " + boloes.get(position).getQuantidade());
				holder.atualizaQuantidadeTextView(boloes.get(position).getQuantidade(), boloes.get(position).getQtdPermitida());
			}

			@Override
			public void aumenta(BolaoHolder holder, int position) {
				boloes.get(position).aumentaQuantidade();
				TestVisao.toast("Quantidade: " + boloes.get(position).getQuantidade());
				holder.atualizaQuantidadeTextView(boloes.get(position).getQuantidade(), boloes.get(position).getQtdPermitida());
			}
		};
	}

}