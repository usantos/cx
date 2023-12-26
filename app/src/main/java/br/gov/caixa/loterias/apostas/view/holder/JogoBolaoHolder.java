package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class JogoBolaoHolder extends LoteriasHolder<ApostaBolaoDTO> {

	private int corTitulo;
	private List<Integer> dezenasSelecionadas;
	private DezenaConfig dezenaConfig;
	private TextView txtNumero;
	private RecyclerView listView;
	private Context context;

	public JogoBolaoHolder(int corTitulo, List<Integer> dezenasSelecionadas, DezenaConfig dezenaConfig, View itemView, Context context) {
		super(itemView);
		this.corTitulo = corTitulo;
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.dezenaConfig = dezenaConfig;
		txtNumero = itemView.findViewById(R.id.id_numero_jogo);
		listView = itemView.findViewById(R.id.id_lista);
		this.context = context;
	}

	@Override
	public void bind(ApostaBolaoDTO aposta, int position) {
		txtNumero.setText("Jogo " + (position + 1));
		txtNumero.setTextColor(ContextCompat.getColor(context, corTitulo));

		List<Dezena> dezenas = new ArrayList<>();
		for (int i = 1; i <= aposta.getDezenas().size(); i++) {
			dezenas.add(new Dezena("" + i, Boolean.FALSE, "" + i));
		}


		ListaDezenaRecyclerView adapter = new ListaDezenaRecyclerView(dezenas, dezenasSelecionadas,
				dezenaConfig, onItemClickListener());
		listView.setAdapter(adapter);

		RecyclerView.LayoutManager layot = new GridLayoutManager(context, 6);
		listView.setLayoutManager(layot);
	}

	private OnItemClickListener onItemClickListener() {
		return (holder, position) -> {

		};
	}
}
