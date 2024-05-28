package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class SuperSeteHolder extends LoteriasHolder<ApostaBolaoDTO> {
	private TextView numeroJogo;
	private RecyclerView listaColuna1, listaColuna2, listaColuna3,
			listaColuna4, listaColuna5, listaColuna6, listaColuna7;
	private Context context;
	private DezenaConfig dezenaConfig;

	public SuperSeteHolder(View itemView, Context context, DezenaConfig dezenaConfig) {
		super(itemView);
		numeroJogo = itemView.findViewById(R.id.numero_jogo);
		listaColuna1 = itemView.findViewById(R.id.lista_coluna_1);
		listaColuna2 = itemView.findViewById(R.id.lista_coluna_2);
		listaColuna3 = itemView.findViewById(R.id.lista_coluna_3);
		listaColuna4 = itemView.findViewById(R.id.lista_coluna_4);
		listaColuna5 = itemView.findViewById(R.id.lista_coluna_5);
		listaColuna6 = itemView.findViewById(R.id.lista_coluna_6);
		listaColuna7 = itemView.findViewById(R.id.lista_coluna_7);
		this.context = context;
		this.dezenaConfig = dezenaConfig;
	}

	@Override
	public void bind(ApostaBolaoDTO aposta, int position) {
		numeroJogo.setText("Jogo " + (position + 1));

		preencheColuna(listaColuna1, (List<Integer>) aposta.getDezenas().get(0));
		preencheColuna(listaColuna2, (List<Integer>) aposta.getDezenas().get(1));
		preencheColuna(listaColuna3, (List<Integer>) aposta.getDezenas().get(2));
		preencheColuna(listaColuna4, (List<Integer>) aposta.getDezenas().get(3));
		preencheColuna(listaColuna5, (List<Integer>) aposta.getDezenas().get(4));
		preencheColuna(listaColuna6, (List<Integer>) aposta.getDezenas().get(5));
		preencheColuna(listaColuna7, (List<Integer>) aposta.getDezenas().get(6));
	}

	private void preencheColuna(RecyclerView listView, List<Integer> numeros) {
		ListaDezenaRecyclerView adapter = new ListaDezenaRecyclerView(getDezenas(numeros), new ArrayList<>(),
				dezenaConfig, onItemClickListener());
		listView.setAdapter(adapter);
	}

	private OnItemClickListener onItemClickListener() {
		return (holder, position) -> {};
	}

	private List<Dezena> getDezenas(List numeros){
		List<Dezena> dezenas = new ArrayList<>();
		for (int i = 0; i < numeros.size(); i++) {
			Integer numero = null;
			if(numeros.get(i) instanceof Double){
				numero = ((Double)numeros.get(i)).intValue();
			} else {
				numero = (Integer) numeros.get(i);
			}
			dezenas.add(new Dezena("" + numero, Boolean.FALSE, "" + numero));
		}
		return dezenas;
	}

}
