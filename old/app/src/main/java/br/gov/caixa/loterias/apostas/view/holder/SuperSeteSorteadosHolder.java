package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.ColunaConfig;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class SuperSeteSorteadosHolder extends LoteriasHolder<List<List<Integer>>> {
	private TextView coluna1, coluna2, coluna3, coluna4, coluna5, coluna6, coluna7;
	private TextView numeroJogo, colunaTxt, numerosTxt;
	private RecyclerView listaColuna1, listaColuna2, listaColuna3,
			listaColuna4, listaColuna5, listaColuna6, listaColuna7;
	private Context context;
	private ColunaConfig colunaConfig;
	private DezenaConfig dezenaConfig;

	public SuperSeteSorteadosHolder(View itemView, Context context, ColunaConfig colunaConfig, DezenaConfig dezenaConfig) {
		super(itemView);
		setViews(itemView);
		this.context = context;
		this.colunaConfig = colunaConfig;
		this.dezenaConfig = dezenaConfig;
	}

	private void setViews(View itemView) {
		numeroJogo   = itemView.findViewById(R.id.numero_jogo);
		colunaTxt 	 = itemView.findViewById(R.id.colunas_txt);
		numerosTxt   = itemView.findViewById(R.id.numeros_txt);
		listaColuna1 = itemView.findViewById(R.id.lista_coluna_1);
		listaColuna2 = itemView.findViewById(R.id.lista_coluna_2);
		listaColuna3 = itemView.findViewById(R.id.lista_coluna_3);
		listaColuna4 = itemView.findViewById(R.id.lista_coluna_4);
		listaColuna5 = itemView.findViewById(R.id.lista_coluna_5);
		listaColuna6 = itemView.findViewById(R.id.lista_coluna_6);
		listaColuna7 = itemView.findViewById(R.id.lista_coluna_7);
		coluna1	 	 = itemView.findViewById(R.id.coluna_1);
		coluna2 	 = itemView.findViewById(R.id.coluna_2);
		coluna3 	 = itemView.findViewById(R.id.coluna_3);
		coluna4 	 = itemView.findViewById(R.id.coluna_4);
		coluna5 	 = itemView.findViewById(R.id.coluna_5);
		coluna6 	 = itemView.findViewById(R.id.coluna_6);
		coluna7 	 = itemView.findViewById(R.id.coluna_7);
	}

	@Override
	public void bind(List<List<Integer>> aposta, int position) {
		colunaTxt.setTextColor(ContextCompat.getColor(context, R.color.branco));
		numerosTxt.setTextColor(ContextCompat.getColor(context, R.color.branco));
		numeroJogo.setVisibility(View.GONE);
		aplicaEstiloColunas();

		preencheColuna(listaColuna1, (List<Integer>) aposta.get(0));
		preencheColuna(listaColuna2, (List<Integer>) aposta.get(1));
		preencheColuna(listaColuna3, (List<Integer>) aposta.get(2));
		preencheColuna(listaColuna4, (List<Integer>) aposta.get(3));
		preencheColuna(listaColuna5, (List<Integer>) aposta.get(4));
		preencheColuna(listaColuna6, (List<Integer>) aposta.get(5));
		preencheColuna(listaColuna7, (List<Integer>) aposta.get(6));
	}

	private void aplicaEstiloColunas() {
		if (colunaConfig != null){
			aplicaCorColuna(coluna1);
			aplicaCorColuna(coluna2);
			aplicaCorColuna(coluna3);
			aplicaCorColuna(coluna4);
			aplicaCorColuna(coluna5);
			aplicaCorColuna(coluna6);
			aplicaCorColuna(coluna7);
		}
	}

	private void aplicaCorColuna(TextView coluna) {
		coluna.setBackgroundColor(ContextCompat.getColor(context, colunaConfig.getBackground()));
		coluna.setTextColor(ContextCompat.getColor(context, colunaConfig.getColor()));
	}

	private void preencheColuna(RecyclerView listView, List<Integer> numeros) {
		ListaDezenaRecyclerView adapter = new ListaDezenaRecyclerView(getDezenas(numeros), numeros,
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
